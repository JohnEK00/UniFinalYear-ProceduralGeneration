package com.PGD;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Item Type factory, used to create new item types and get item types
 */
public class ItemFactory {
    public static HashMap<String, ItemType> items = new HashMap<>();

    static public void getItem(String itemName, Texture texture, float x, float y, float xOffset,
            float yOffset, boolean destructible, boolean interactable, Texture usedTexture,
            Animation<TextureRegion> animation) {
        items.computeIfAbsent(itemName, t -> new ItemType(itemName, texture, x, y, xOffset,
                yOffset, destructible, interactable, usedTexture, animation));
    }

    // Getter
    static public ItemType getItem(String itemName) {
        return items.get(itemName);
    }
}