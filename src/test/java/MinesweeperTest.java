import model.Board;
import model.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MinesweeperTest {

    @Test
    public void testTileProperties() {
        Tile tile = new Tile();

        assertFalse(tile.isOpened());
        assertFalse(tile.isFlagged());
        assertFalse(tile.hasMine());
        assertEquals(0, tile.getNeighborMineCount());

        tile.setOpened(true);
        assertTrue(tile.isOpened());

        tile.setFlagged(true);
        assertTrue(tile.isFlagged());

        tile.setHasMine(true);
        assertTrue(tile.hasMine());

        tile.setNeighborMineCount(5);
        assertEquals(5, tile.getNeighborMineCount());
    }

    @Test
    public void testBoardInitialization() {
        int rows = 16;
        int columns = 30;
        int mines = 99;

        Board board = new Board(rows, columns, mines);

        assertEquals(rows, board.getHeight());
        assertEquals(columns, board.getWidth());
        assertEquals(mines, board.getMines());

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                assertFalse(board.getTile(row, col).isOpened());
                assertFalse(board.getTile(row, col).isFlagged());
            }
        }
    }

    @Test
    public void testFlaggingTile() {
        Board board = new Board(8, 8, 10);

        board.flagTile(2, 3);
        assertTrue(board.getTile(2, 3).isFlagged());
        assertEquals(1, board.getFlaggedTilesCount());

        board.unflagTile(2, 3);
        assertFalse(board.getTile(2, 3).isFlagged());
        assertEquals(0, board.getFlaggedTilesCount());
    }

    @Test
    public void testOpeningTile() {
        Board board = new Board(8, 8, 10);

        // Проверка открытия клетки и обновления счетчика открытых клеток
        board.openTile(4, 5);
        assertTrue(board.getTile(4, 5).isOpened());
        assertEquals(1, board.getOpenedTilesCount());
    }

    @Test
    public void testAssignNeighborMineCounts() {
        Board board = new Board(16, 30, 99);
        board.assignNeighborMineCounts();

        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                Tile tile = board.getTile(row, col);
                if (!tile.hasMine()) {
                    int count = tile.getNeighborMineCount();
                    int expectedCount = countNeighborMines(board, row, col);
                    assertEquals(expectedCount, count);
                }
            }
        }
    }

    private int countNeighborMines(Board board, int row, int col) {
        int count = 0;
        int[][] neighborOffsets = board.getNeighborOffsets(row);

        for (int[] offset : neighborOffsets) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            if (board.isValidPosition(newRow, newCol) && board.getTile(newRow, newCol).hasMine()) {
                count++;
            }
        }

        return count;
    }
    @Test
    public void testWinCase() {
        Board board = new Board(8, 8, 10);

        assertFalse(board.getResult());

        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                Tile tile = board.getTile(row, col);
                if (tile.hasMine()) {
                    board.flagTile(row, col);
                } else {
                    board.openTile(row, col);
                }
            }
        }

        assertTrue(board.getResult());

        board = new Board(16, 16, 40);

        assertFalse(board.getResult());

        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                Tile tile = board.getTile(row, col);
                if (tile.hasMine()) {
                    board.flagTile(row, col);
                } else {
                    board.openTile(row, col);
                }
            }
        }

        assertTrue(board.getResult());

        board = new Board(16, 30, 99);

        assertFalse(board.getResult());

        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                Tile tile = board.getTile(row, col);
                if (tile.hasMine()) {
                    board.flagTile(row, col);
                } else {
                    board.openTile(row, col);
                }
            }
        }

        assertTrue(board.getResult());
    }

    @Test
    public void testOpenAllTiles() {
        int rows = 8;
        int columns = 8;
        int mines = 10;

        Board board = new Board(rows, columns, mines);
        board.openAll();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                assertTrue(board.getTile(row, col).isOpened());
            }
        }
    }

    @Test
    public void testResetBoard() {
        int rows = 8;
        int columns = 8;
        int mines = 10;

        Board board = new Board(rows, columns, mines);

        board.openTile(2, 3);
        board.openTile(4, 5);

        assertTrue(board.getTile(2, 3).isOpened());
        assertTrue(board.getTile(4, 5).isOpened());
        assertFalse(board.getTile(3, 4).isOpened());

        board.resetBoard();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                assertFalse(board.getTile(row, col).isOpened());
            }
        }
    }

    @Test
    public void testGetFlaggedMinesCount() {
        int rows = 8;
        int columns = 8;
        int mines = 10;

        Board board = new Board(rows, columns, mines);

        board.flagTile(2, 3);
        board.flagTile(4, 5);

        assertEquals(2, board.getFlaggedTilesCount());

        board.unflagTile(2, 3);

        assertEquals(1, board.getFlaggedTilesCount());
    }

    @Test
    public void testIsValidPosition() {
        int rows = 8;
        int columns = 8;
        int mines = 10;

        Board board = new Board(rows, columns, mines);

        assertTrue(board.isValidPosition(0, 0));
        assertTrue(board.isValidPosition(3, 5));
        assertTrue(board.isValidPosition(7, 7));

        assertFalse(board.isValidPosition(-1, 0));
        assertFalse(board.isValidPosition(0, -1));
        assertFalse(board.isValidPosition(8, 5));
        assertFalse(board.isValidPosition(3, 8));
    }
}