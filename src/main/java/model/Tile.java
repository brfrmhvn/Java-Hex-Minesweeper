package model;

public class Tile {
    private boolean isOpened;
    private boolean isFlagged;
    private boolean hasMine;
    private int neighborMineCount;

    public Tile() {
        isOpened = false;
        isFlagged = false;
        hasMine = false;
        neighborMineCount = 0;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public boolean hasMine() {
        return hasMine;
    }

    public void setHasMine(boolean hasMine) {
        this.hasMine = hasMine;
    }

    public int getNeighborMineCount() {
        return neighborMineCount;
    }

    public void setNeighborMineCount(int count) {
        neighborMineCount = count;
    }
}