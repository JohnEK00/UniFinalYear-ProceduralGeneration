package com.PGD;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Stores texture and name of item
 */
public class ItemType {
    private final String name;
    private final Texture texture;
    private final float width;
    private final float height;
    private final float xOffset;
    private final float yOffset;
    private final boolean destructible;
    private final boolean interactable;
    private final Texture usedTexture;
    private final Animation<TextureRegion> animation;

    ItemType(String name, Texture texture, float width, float height, float xOffset, float yOffset,
            boolean destructible, boolean interactable, Texture usedTexture, Animation<TextureRegion> animation) {
        this.name = name;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.destructible = destructible;
        this.interactable = interactable;
        this.usedTexture = usedTexture;
        this.animation = animation;
    }

    // Getters
    public Texture getTexture() {
        return texture;
    }

    public String getName() {
        return name;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getXOffset() {
        return xOffset;
    }

    public float getYOffset() {
        return yOffset;
    }

    public boolean isDestructible() {
        return destructible;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public Texture getUsedTexture() {
        return usedTexture;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }
}