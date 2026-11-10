package com.PGD;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Arrow {

    private static Texture ARROW_TEXTURE;
    private static final int WIDTH = 15, HEIGHT = 2;
    private static final float SPEED = 300;

    private final Sprite sprite;
    private float posX;
    private final float posY;
    private final Rectangle rectangle;
    private boolean moving = true;
    private final int xDir;
    private float aliveTimer;

    /**
     * Arrow has a fixed speed and moves in the whatever direction
     * is set by xDir.
     *
     * @param xDir int either 1 or -1
     * @param posX
     * @param posY
     * @param arrowTexture
     */
    public Arrow(int xDir, float posX, float posY, Texture arrowTexture) {
        ARROW_TEXTURE = arrowTexture;

        if(arrowTexture != null) {
            sprite = new Sprite(ARROW_TEXTURE);
        } else{
            sprite = null;
        }

        this.xDir = xDir;
        this.posX = posX;
        this.posY = posY;
        if(xDir<0 && sprite != null) {
            sprite.flip(true, false);
        }
        if(sprite != null) {
            sprite.setPosition(posX, posY);
        }
        rectangle = new Rectangle(posX - (WIDTH/2), posY - 1, WIDTH, HEIGHT);
        aliveTimer = 0f;
    }

    /**
     * Main loop to update sprite position and check collisions
     * @param batch
     * @param collider
     * @param delta
     */
    public void update(SpriteBatch batch, Collider collider, float delta) {
        if(moving){
            posX += SPEED * xDir * delta;
            ArrayList<Item> itemArr =collider.checkInteractableItemCollision(rectangle);

            if(collider.checkCollision(rectangle) || !itemArr.isEmpty()){
                moving = false;
            }
            collider.damageEnemies(rectangle, -10);

        }

        rectangle.setPosition(posX- (WIDTH/2), posY - 1);
        if(sprite != null){
            batch.draw(sprite, posX -16f, posY-16f);
        }
        aliveTimer += delta;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public float getTimer(){
        return aliveTimer;
    }

    public void Destroy(){
        FXAnimation.addAnimation(posX -16f, posY -16f, 6);
    }

    public boolean isMoving() {
        return moving;
    }

}
