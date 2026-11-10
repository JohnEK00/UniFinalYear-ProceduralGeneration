package com.PGD;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

/**
 * Wall Factory to create new types of walls and get stored types
 */
public class WallCellFactory {
    static HashMap<Integer, WallCellType> wallTypes = new HashMap<>();

    /**
     * Get wall type
     * 
     * @param type          - type of wall
     * @param textureRegion - texture to use
     * @param width         - width of wall
     * @param height        - height of wall
     * @param XOffset       - x offset
     * @param YOffset       - y offset
     * @return WallCellType
     */
    public static WallCellType getWallType(Integer type, TextureRegion textureRegion, int width, int height,
            int XOffset, int YOffset) {
        WallCellType cellType = wallTypes.computeIfAbsent(type,
                t -> new WallCellType(t, textureRegion, width, height, XOffset, YOffset));
        return cellType;

    }

    /**
     * Get wall type
     * 
     * @param type - type of wall
     * @return WallCellType
     */
    public static WallCellType getWallType(Integer type) {
        return wallTypes.get(type);
    }
}