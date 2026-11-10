package com.PGD;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Contains dimensions of the hitbox and the texture used
 * also integer of which wall it is
 */
public class WallCellType {
    private final int type;
    private final TextureRegion textureRegion;
    int width;
    int height;
    int xOffset;
    int yOffset;

    /**
     * Create a new wall type
     * 
     * @param type          - type of wall
     * @param textureRegion - texture to use
     * @param width         - width of wall
     * @param height        - height of wall
     * @param xOffset       - x offset
     * @param yOffset       - y offset
     */
    WallCellType(int type, TextureRegion textureRegion, int width, int height, int xOffset, int yOffset) {
        this.type = type;
        this.textureRegion = textureRegion;
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
    }

    // Getters
    public Rectangle getRectangle(int x, int y, int tileWidth, int tileHeight) {
        return new Rectangle(x * tileWidth + xOffset, y * tileHeight + yOffset, width, height);
    }

    public int getType() {
        return type;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

}