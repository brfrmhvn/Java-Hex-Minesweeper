package model;

import java.util.Random;

public class Board {
    private final Tile[][] grid;
    private final int rows;
    private final int columns;
    private final int mines;
    private int correctFlags;
    private boolean result; //true = win
    private int openedTiles;
    private int flaggedTilesCount;

    public Board(int rows, int columns, int mines) {
        this.rows = rows;
        this.columns = columns;
        this.mines = mines;
        grid = new Tile[rows][columns];
        correctFlags = 0;
        openedTiles = 0;
        flaggedTilesCount = 0;
        initializeGrid();
        assignNeighborMineCounts();
        result = false;
    }

    public int[][] getNeighborOffsets(int row) {
        int[][] neighborOffsets = {
                {0, 0}, {0, 0}, {-1, 0}, {0, -1}, {0, 1}, {1, 0},
        };

        if (row % 2 == 0) {
            neighborOffsets[0] = new int[]{-1, -1};
            neighborOffsets[1] = new int[]{1, -1};
        } else {
            neighborOffsets[0] = new int[]{1, 1};
            neighborOffsets[1] = new int[]{-1, 1};
        }

        return neighborOffsets;
    }

    public int getHeight() {
        return rows;
    }

    public int getWidth() {
        return columns;
    }

    public Tile getTile(int row, int column) {
        return grid[row][column];
    }

    private void initializeGrid() {
        // Заполнение сетки клетками
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = new Tile();
            }
        }

        // Расстановка случайных мин
        Random random = new Random();
        int minesToPlace = mines;

        while (minesToPlace > 0) {
            int row = random.nextInt(rows);
            int col = random.nextInt(columns);

            if (!grid[row][col].hasMine()) {
                grid[row][col].setHasMine(true);
                minesToPlace--;
            }
        }
    }

    public void openTile(int row, int column) {
        Tile tile = grid[row][column];

        if (!tile.isOpened() && !tile.isFlagged()) {
            tile.setOpened(true);
            openedTiles++;
        }
    }

    public void flagTile(int row, int column) {
        Tile tile = grid[row][column];
        if (!tile.isOpened()) {
            tile.setFlagged(true);
            flaggedTilesCount++;
            if (tile.isFlagged() && tile.hasMine()) {
                correctFlags++;

            }
        }
    }

    public void unflagTile(int row, int column) {
        Tile tile = grid[row][column];
        if (tile.isFlagged()) {
            tile.setFlagged(false);
            if (!tile.isFlagged() && tile.hasMine()) {
                correctFlags--;
            }
            flaggedTilesCount--;
        }
    }

    public boolean winCase() {
        if (correctFlags == mines && openedTiles == (rows * columns) - mines) {
            result = true;
        }
        return result;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }

    public void assignNeighborMineCounts() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Tile tile = grid[row][col];

                if (!tile.hasMine()) {
                    tile.setNeighborMineCount(0);

                    int count = countNeighborMines(row, col);
                    tile.setNeighborMineCount(count);
                }
            }
        }
    }

    private int countNeighborMines(int row, int col) {
        int count = 0;

        int[][] neighborOffsets = getNeighborOffsets(row);

        for (int[] offset : neighborOffsets) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            if (isValidPosition(newRow, newCol) && grid[newRow][newCol].hasMine()) {
                count++;
            }
        }

        return count;
    }

    public boolean getResult() {
        return winCase();
    }

    public void resetBoard() {
        correctFlags = 0;
        openedTiles = 0;
        flaggedTilesCount = 0;
        initializeGrid();
        assignNeighborMineCounts();
        result = false;
    }

    public int getMines() {
        return this.mines;
    }

    public void openAll() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                openTile(row, col);
            }
        }
    }

    public int getFlaggedTilesCount() {
        return flaggedTilesCount;
    }

    public int getOpenedTilesCount() {
        return openedTiles;
    }
}