package com.PGD;

/*
 * Represents the type of cell in the map.
 */
enum CellState {
    TILE, // Traversable
    BLANK, // Non-Traversable (Mainly for initial generation then replaced with WALL)
    // Different variants of tile in appearance only
    TILE_VAR1,
    TILE_VAR2,
    TILE_VAR3,
    // Half floor tiles for the slight distance between WALL and TILE (and a full
    // tile in specific cases underneath walls)
    HALF_LEFT,
    HALF_RIGHT,
    FULL_FLOOR,
    // Tile with prop
    TILE_CHEST,
    TILE_BOX,
    TILE_LIGHT,
    TILE_SKULL,
    // Tile near walls for extra decoration
    TILE_RIGHT,
    TILE_LEFT,
    TILE_UP_RIGHT,
    TILE_UP_LEFT
}

/*
 * Represents each cell inside the generated map.
 */
public class MapCell {
    private MapCellType type;
    private final int X;
    private final int Y;

    /*
     * Constructor for MapCell
     * 
     * @param type - type of cell
     * 
     * @param X - x coordinate in map
     * 
     * @param Y - y coordinate in map
     */
    public MapCell(MapCellType type, int X, int Y) {
        this.type = type;
        this.X = X;
        this.Y = Y;
    }

    // Getters and Setters
    public MapCellType getState() {
        return type;
    }

    public void setState(MapCellType type) {
        this.type = type;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public boolean isFloor() {
        if (type != null) {
            return type.isFloor();
        }
        return false;

    }
}