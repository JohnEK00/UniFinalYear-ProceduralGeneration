package com.PGD;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Map cell type stores information about which floor it is and the texture to
 * use
 * Also used to check if it is a floor, this can later be used to check if
 * player is on the
 * ground
 */
public class MapCellType {
    private final CellState cellState;
    private final TextureRegion texture;
    private final boolean isFloor;

    /**
     * Create a new cell type
     * 
     * @param cellState - state of the cell
     * @param texture   - texture to use
     * @param isFloor   - is this cell a floor
     */
    public MapCellType(CellState cellState, TextureRegion texture, boolean isFloor) {
        this.cellState = cellState;
        this.texture = texture;
        this.isFloor = isFloor;
    }

    // Getters
    public TextureRegion getTextureRegion() {
        return texture;
    }

    public CellState getCellState() {
        return cellState;
    }

    public boolean isFloor() {
        return isFloor;
    }
}