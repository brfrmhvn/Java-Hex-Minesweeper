package app;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Board;

import java.io.*;
import java.util.Optional;

public class MinesweeperApp extends Application {
    private static final int EASY_HEIGHT = 8;
    private static final int EASY_WIDTH = 8;
    private static final int EASY_MINES = 10;

    private static final int MEDIUM_HEIGHT = 16;
    private static final int MEDIUM_WIDTH = 16;
    private static final int MEDIUM_MINES = 40;

    private static final int HARD_HEIGHT = 16;
    private static final int HARD_WIDTH = 30;
    private static final int HARD_MINES = 99;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        showDifficultyDialog(primaryStage);
    }

    private void showDifficultyDialog(Stage primaryStage) throws FileNotFoundException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Шестигранный сапер");
        alert.setHeaderText("Выберите уровень сложности");
        FileInputStream inputStream = new FileInputStream("src/main/java/images/icon.png");
        Image icon = new Image(inputStream);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);

        ButtonType easyButton = new ButtonType("Легкий");
        ButtonType mediumButton = new ButtonType("Средний");
        ButtonType hardButton = new ButtonType("Сложный");
        ButtonType customButton = new ButtonType("Пользовательский");
        ButtonType cancelButton = new ButtonType("Отмена", ButtonType.CANCEL.getButtonData());

        alert.getButtonTypes().setAll(easyButton, mediumButton, hardButton, customButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (easyButton.equals(buttonType)) {
                try {
                    startNewGame(primaryStage, EASY_WIDTH, EASY_HEIGHT, EASY_MINES);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (mediumButton.equals(buttonType)) {
                try {
                    startNewGame(primaryStage, MEDIUM_WIDTH, MEDIUM_HEIGHT, MEDIUM_MINES);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (hardButton.equals(buttonType)) {
                try {
                    startNewGame(primaryStage, HARD_WIDTH, HARD_HEIGHT, HARD_MINES);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (customButton.equals(buttonType)) {
                showCustomDialog(primaryStage);
            } else if (cancelButton.equals(buttonType)) {
                primaryStage.close();
            }
        });
    }

    record CustomDifficulty(int width, int height, int mines) {
    }

    private void showCustomDialog(Stage primaryStage) {
        Dialog<CustomDifficulty> dialog = new Dialog<>();
        dialog.setTitle("Шестигранный сапер");
        dialog.setHeaderText("Выберите уровень сложности");

        TextField widthField = new TextField();
        widthField.setPromptText("от 9 до 35");
        TextField heightField = new TextField();
        heightField.setPromptText("от 9 до 20");
        TextField minesField = new TextField();
        minesField.setPromptText("Макс. = ширина * высота");

        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButton, okButton);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.addRow(0, new Label("Ширина:"), widthField);
        gridPane.addRow(1, new Label("Высота:"), heightField);
        gridPane.addRow(2, new Label("Количество мин:"), minesField);
        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                String widthText = widthField.getText();
                String heightText = heightField.getText();
                String minesText = minesField.getText();

                if (widthText.isEmpty() || heightText.isEmpty() || minesText.isEmpty()) {
                    return null;
                }

                try {
                    int width = Integer.parseInt(widthText);
                    int height = Integer.parseInt(heightText);
                    int mines = Integer.parseInt(minesText);

                    if (width <= 9 || width > 36 || height <= 9 || height > 21 || mines < 0 || mines > height * width) {
                        return null;
                    }

                    return new CustomDifficulty(width, height, mines);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });


        // Отображение диалога и обработка результата
        Optional<CustomDifficulty> result = dialog.showAndWait();
        result.ifPresentOrElse(
                customDifficulty -> {
                    try {
                        startNewGame(primaryStage, customDifficulty.width(), customDifficulty.height(), customDifficulty.mines());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        showDifficultyDialog(primaryStage);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    private void startNewGame(Stage primaryStage, int width, int height, int numMines) throws IOException {
        VBox centerPane = new VBox(25);
        centerPane.setAlignment(Pos.TOP_CENTER);
        HBox hBox = new HBox(70);
        Pane gameBoardPane = new Pane();


        Board board = new Board(height, width, numMines);
        Label mineCountLabel = new Label("Бoмб осталось: " + numMines);
        View view = new View(board, gameBoardPane);
        Controller controller = new Controller(board, view, mineCountLabel);

        view.drawBoard();

        Button stopGameButton = new Button("Новая игра");
        stopGameButton.setOnAction(event -> {
            try {
                showDifficultyDialog(primaryStage);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });


        hBox.getChildren().addAll(stopGameButton, mineCountLabel);

        centerPane.getChildren().addAll(gameBoardPane, hBox);
        hBox.setAlignment(Pos.CENTER);

        double tileHeight = 2 * 22;
        double tileWidth = 2 * Math.sqrt(22 * 22 * 0.75);
        int windowWidth = (int) (80 + tileWidth * width + 11);
        int windowHeight = (int) (100 + tileHeight * height * 3 / 4);

        primaryStage.setScene(new Scene(centerPane, windowWidth, windowHeight));

        primaryStage.setResizable(false);
        primaryStage.setTitle("Шестигранный сапер");
        FileInputStream inputStream = new FileInputStream("src/main/java/images/icon.png");
        Image icon = new Image(inputStream);
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}