package com.PGD;

/**
 * holds coordinates but wall cell type holds the dimensions of the wall
 */
public class WallCell {
    private WallCellType type;
    private final int x;
    private final int y;

    /**
     * Create wall cell instance at given location.
     * 
     * @param type - type of wall cell
     * @param x    - x coordinate in map
     * @param y    - y coordinate in map
     */
    WallCell(WallCellType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    // Getters and Setters
    public int getType() {
        return type.getType();
    }

    public void setType(WallCellType type) {
        this.type = type;
    }

    public WallCellType getCellType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}