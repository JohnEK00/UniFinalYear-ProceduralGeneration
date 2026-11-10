package com.PGD;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Stores details information about item and the sprite that
 * will be rendered in
 */
public class Item {
    // x and y coordinates of the item
    private final float x, y;
    private final ItemType type;
    private final Sprite sprite;
    private final Rectangle hitBox;
    private boolean used;

    /**
     * Create item instance at given location.
     * 
     * @param x    - x coordinate in map
     * @param y    - y coordinate in map
     * @param type - type of item
     */
    Item(float x, float y, ItemType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        hitBox = new Rectangle(x + type.getXOffset(), y + type.getYOffset(), type.getWidth(), type.getHeight());
        // Due to GDX framework textures must be set to null during tests
        if (type.getTexture() != null) {
            this.sprite = new Sprite(type.getTexture());
        } else {
            this.sprite = new Sprite();
        }

        used = false;
        this.sprite.setPosition(x, y);
    }

    /**
     * Draw the item on the screen
     * 
     * @param batch   - SpriteBatch to draw the item
     * @param aniTime - Time unaffected by differing framerate
     */
    public void draw(SpriteBatch batch, float aniTime) {
        Animation<TextureRegion> animation = type.getAnimation();
        if (animation != null) {
            TextureRegion frame = animation.getKeyFrame(aniTime, true);
            batch.draw(frame, x, y);
        } else if (sprite != null) {
            batch.draw(sprite, x, y);
        }
    }

    // Getters
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public ItemType getType() {
        return type;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    /**
     * destroys the object currently missing animation of the item getting destroyed
     */
    public void destroy() {
        System.out.println(type.getName() + " destroyed");
        FXAnimation.addAnimation(x, y, 6);
    }

    /**
     * interaction logic, once an item is used it cant be used again
     * changes sprites texture to a used texture
     */
    public void interact() {
        System.out.println("You found a " + type.getName() + "!");
        this.sprite.setRegion(type.getUsedTexture());
        used = true;
    }

    /**
     * check if item has been used
     * 
     * @return true if item has been used
     */
    public boolean isUsed() {
        return used;
    }
}