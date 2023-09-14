package app;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

public class Controller {
    private final Board board;
    private final View view;
    private final Label mineCountLabel;

    public Controller(Board board, View view, Label mineCountLabel) {
        this.board = board;
        this.view = view;
        this.mineCountLabel = mineCountLabel;
        view.setController(this);
    }

    public void handleClick(int row, int col, MouseButton button) throws FileNotFoundException {
        Tile tile = board.getTile(row, col);

        if (button == MouseButton.PRIMARY) {
            if (!tile.isOpened()) {
                openTilesRecursively(row, col);

                if (tile.hasMine()) {
                    view.showAll();
                    view.explodedMine(row, col);
                    showGameOverDialog("Вы проиграли!");
                }

                if (board.getResult()) {
                    showGameOverDialog("Поздравляем, вы выиграли!");
                }
            }
        } else if (button == MouseButton.SECONDARY) {
            if (!tile.isOpened() && !tile.isFlagged()) {
                board.flagTile(row, col);
                view.updateTile(row, col);
            } else {
                board.unflagTile(row, col);
                view.updateTile(row, col);
            }
            int flaggedMinesCount = board.getFlaggedTilesCount();
            int remainingMines = board.getMines() - flaggedMinesCount;
            mineCountLabel.setText("Бомб осталось: " + remainingMines);
            if (board.getResult()) {
                showGameOverDialog("Поздравляем, вы выиграли!");
            }
        }
    }

    private void openTilesRecursively(int row, int col) throws FileNotFoundException {
        Tile tile = board.getTile(row, col);

        if (tile.isOpened() || tile.isFlagged()) {
            return;
        }

        board.openTile(row, col);
        view.updateTile(row, col);

        if (tile.getNeighborMineCount() == 0) {
            // рекурсивно открываем соседние клетки без мин
            int[][] neighborOffsets = board.getNeighborOffsets(row);

            for (int[] offset : neighborOffsets) {
                int newRow = row + offset[0];
                int newColumn = col + offset[1];

                if (board.isValidPosition(newRow, newColumn)) {
                    openTilesRecursively(newRow, newColumn);
                }
            }
        }
    }

    private void showGameOverDialog(String message) throws FileNotFoundException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Игра окончена");
        alert.setHeaderText(message);
        alert.setContentText("Хотите начать игру заново?");
        FileInputStream inputStream = new FileInputStream("src/main/java/images/icon.png");
        Image icon = new Image(inputStream);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);

        ButtonType buttonTypeYes = new ButtonType("Да");
        ButtonType buttonTypeNo = new ButtonType("Нет");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == buttonTypeYes) {
            // Начать новую игру
            board.resetBoard();
            mineCountLabel.setText("Бомб осталось: " + board.getMines());
            view.drawBoard();
        } else {
            Platform.exit();
        }
    }
}