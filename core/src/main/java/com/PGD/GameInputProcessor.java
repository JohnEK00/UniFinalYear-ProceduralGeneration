package com.PGD;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Class to handle the input from the player
 */
public class GameInputProcessor implements InputProcessor {

    // Key bindings
    public static int LEFT = Input.Keys.LEFT;
    public static int RIGHT = Input.Keys.RIGHT;
    public static int UP = Input.Keys.UP;
    public static int DOWN = Input.Keys.DOWN;
    public static int ATTACK1 = Input.Keys.A;
    public static int ATTACK2 = Input.Keys.S;
    public static int ATTACK3 = Input.Keys.D;

    // Player instance
    private final Player player;

    /**
     * Constructor for GameInputProcessor
     * 
     * @param player - Player instance
     */
    public GameInputProcessor(Player player) {
        this.player = player;
    }

    /**
     * Handle key down events
     * 
     * @param keycode - Key pressed
     * 
     * @return - True if key is pressed, false otherwise
     */
    @Override
    public boolean keyDown(int keycode) {

        if (LEFT == keycode) {
            player.pressLeft();
        }

        if (RIGHT == keycode) {
            player.pressRight();
        }

        if (UP == keycode) {
            player.pressUp();
        }

        if (DOWN == keycode) {
            player.pressDown();
        }

        if (keycode == Input.Keys.Z) {
            Main.decreaseView();
        }

        if (keycode == Input.Keys.C) {
            Main.increaseView();
        }

        if (keycode == Input.Keys.Q) {
            player.interact();
        }

        if (keycode == ATTACK1) {
            player.attack(1);
        }

        if (keycode == ATTACK2) {
            player.attack(2);
        }

        if(keycode == ATTACK3){
            player.shoot();
        }

        return true;
    }

    /**
     * Handle key up events
     * 
     * @param keycode - Key released
     * 
     * @return - True if key is released, false otherwise
     */
    @Override
    public boolean keyUp(int keycode) {

        if (keycode == Input.Keys.LEFT) {
            player.releaseLeft();
        }

        if (keycode == Input.Keys.RIGHT) {
            player.releaseRight();
        }

        if (keycode == Input.Keys.UP) {
            player.releaseUp();
        }

        if (keycode == Input.Keys.DOWN) {
            player.releaseDown();
        }

        return true;
    }

    /**
     * Handle key typed events
     * 
     * @param c - Character typed
     * 
     * @return - True if character is typed, false otherwise
     */
    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    /**
     * Handle touch down events
     * 
     * @param i  - x coordinate of touch
     * @param i1 - y coordinate of touch
     * @param i2 - pointer
     * @param i3 - button
     * 
     * @return - True if touch is down, false otherwise
     */
    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    /**
     * Handle touch up events
     * 
     * @param i  - x coordinate of touch
     * @param i1 - y coordinate of touch
     * @param i2 - pointer
     * @param i3 - button
     * 
     * @return - True if touch is up, false otherwise
     */
    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    /**
     * Handle touch dragged events
     * 
     * @param i  - x coordinate of touch
     * @param i1 - y coordinate of touch
     * @param i2 - pointer
     * 
     * @return - True if touch is dragged, false otherwise
     */
    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    /**
     * Handle touch dragged events
     * 
     * @param i  - x coordinate of touch
     * @param i1 - y coordinate of touch
     * @param i2 - pointer
     * 
     * @return - True if touch is dragged, false otherwise
     */
    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    /**
     * Handle mouse moved events
     * 
     * @param i  - x coordinate of mouse
     * @param i1 - y coordinate of mouse
     * 
     * @return - True if mouse is moved, false otherwise
     */
    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    /**
     * Handle scrolled events
     * 
     * @param v  - x coordinate of scroll
     * @param v1 - y coordinate of scroll
     * 
     * @return - True if scrolled, false otherwise
     */
    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}