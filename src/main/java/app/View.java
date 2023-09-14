package app;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.Polygon;
import model.Board;
import model.Tile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class View {
    private final Board board;
    private final Pane boardPane;
    private final static double r = 22; // внутренний радиус от центра шестиугольника до внешнего угла
    private final static double n = Math.sqrt(r * r * 0.75); // внутренний радиус от центра шестиугольника до середины оси
    private final static double TILE_HEIGHT = 2 * r;
    private final static double TILE_WIDTH = 2 * n;
    private Controller controller;

    public View(Board board, Pane boardPane) {
        this.board = board;
        this.boardPane = boardPane;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void drawBoard() {
        boardPane.getChildren().clear();
        int rowCount = board.getHeight();
        int tilesPerRow = board.getWidth();
        int xStartOffset = 40;
        int yStartOffset = 40;

        for (int y = 0; y < rowCount; y++) {
            for (int x = 0; x < tilesPerRow; x++) {
                double xCoord = x * TILE_WIDTH + (y % 2) * n + xStartOffset;
                double yCoord = y * TILE_HEIGHT * 0.75 + yStartOffset;

                HexTile hexTile = new HexTile(xCoord, yCoord, x, y);
                boardPane.getChildren().add(hexTile);
            }
        }
    }

    public void updateTile(int row, int col) throws FileNotFoundException {
        tileView(row, col);
    }

    public void updateAllTiles(int row, int column) throws FileNotFoundException {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                updateTile(i, j);
            }
        }
    }

    private void tileView(int row, int column) throws FileNotFoundException {
        HexTile hexTile = getHexTile(row, column);
        Tile tile = board.getTile(row, column);

        if (tile.isOpened()) {
            if (tile.hasMine()) {
                FileInputStream inputStream = new FileInputStream("src/main/java/images/bomb.png");
                Image bomb = new Image(inputStream);
                hexTile.setFill(new ImagePattern(bomb));
            } else {
                int neighborMineCount = tile.getNeighborMineCount();
                if (neighborMineCount == 0) {
                    hexTile.setFill(Color.WHITE);
                } else {
                    switch (neighborMineCount) {
                        case 1 -> {
                            FileInputStream inputStream1 = new FileInputStream("src/main/java/images/1.jpg");
                            Image one = new Image(inputStream1);
                            hexTile.setFill(new ImagePattern(one));
                        }
                        case 2 -> {
                            FileInputStream inputStream2 = new FileInputStream("src/main/java/images/2.jpg");
                            Image two = new Image(inputStream2);
                            hexTile.setFill(new ImagePattern(two));
                        }
                        case 3 -> {
                            FileInputStream inputStream3 = new FileInputStream("src/main/java/images/3.jpg");
                            Image three = new Image(inputStream3);
                            hexTile.setFill(new ImagePattern(three));
                        }
                        case 4 -> {
                            FileInputStream inputStream4 = new FileInputStream("src/main/java/images/4.jpg");
                            Image four = new Image(inputStream4);
                            hexTile.setFill(new ImagePattern(four));
                        }
                        case 5 -> {
                            FileInputStream inputStream5 = new FileInputStream("src/main/java/images/5.jpg");
                            Image five = new Image(inputStream5);
                            hexTile.setFill(new ImagePattern(five));
                        }
                        case 6 -> {
                            FileInputStream inputStream6 = new FileInputStream("src/main/java/images/6.jpg");
                            Image six = new Image(inputStream6);
                            hexTile.setFill(new ImagePattern(six));
                        }
                    }
                }
            }
        } else if (tile.isFlagged()) {
            FileInputStream inputStream = new FileInputStream("src/main/java/images/flag.png");
            Image flag = new Image(inputStream);
            hexTile.setFill(new ImagePattern(flag));
        } else {
            hexTile.setFill(Color.DARKSALMON);
        }
    }


    public void showAll() throws FileNotFoundException {
        board.openAll();
        updateAllTiles(board.getHeight(), board.getWidth());
    }

    public HexTile getHexTile(int row, int column) {
        int index = row * board.getWidth() + column;
        return (HexTile) boardPane.getChildren().get(index);
    }

    public void explodedMine(int row, int col) throws FileNotFoundException {
        HexTile hexTile = getHexTile(row, col);
        Tile tile = board.getTile(row, col);

        if (tile.hasMine()) {
            FileInputStream inputStream = new FileInputStream("src/main/java/images/exploded.png");
            Image exploded = new Image(inputStream);
            hexTile.setFill(new ImagePattern(exploded));
        }
    }

    public class HexTile extends Polygon {

        HexTile(double x, double y, int row, int column) {
            // создает шестиугольник, используя координаты углов
            getPoints().addAll(
                    x, y,
                    x, y + r,
                    x + n, y + r * 1.5,
                    x + TILE_WIDTH, y + r,
                    x + TILE_WIDTH, y,
                    x + n, y - r * 0.5
            );

            setFill(Color.DARKSALMON);
            setStrokeWidth(2);
            setStroke(Color.BLACK);
            setOnMouseClicked(event -> {
                try {
                    controller.handleClick(column, row, event.getButton());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
