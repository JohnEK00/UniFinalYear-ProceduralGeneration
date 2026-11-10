package com.PGD;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * MapCellFactory gets MapCellTypes that store texture and other details
 */
public class MapCellFactory {
    // HashMap to store cell types
    public static HashMap<CellState, MapCellType> cellTypes = new HashMap<>();

    // Get cell type
    public static MapCellType getCellType(CellState cellState, TextureRegion textureRegion, boolean isFloor) {
        MapCellType cellType = cellTypes.computeIfAbsent(cellState, s -> new MapCellType(s, textureRegion, isFloor));
        return cellType;
    }

    // Get cell type
    public static MapCellType getCellType(CellState cellState) {
        return cellTypes.get(cellState);
    }
}