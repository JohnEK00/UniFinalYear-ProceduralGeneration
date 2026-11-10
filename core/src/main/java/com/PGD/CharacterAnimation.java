package com.PGD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/*
 * Class to handle the animations of the character
 */
public class CharacterAnimation {
    // no of frames from texture sheet
    protected static final int RUN_ANI_FRAMES = 8;
    protected static final int IDLE_ANI_FRAMES = 6;
    protected static final int HURT_ANI_FRAMES = 4;
    protected static final int ATTACK1_ANI_FRAMES = 6;

    // width and height of each texture sheet
    private float width;
    private float height;

    private float aniTime;
    private boolean lookRight;
    protected boolean loop;
    protected float x, y;

    protected Animation<TextureRegion> currentAnimation;

    // all the animations that will be used based on direction
    protected Animation<TextureRegion> runAnimation;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> hurtAnimation;
    protected Animation<TextureRegion> attack1Animation;

    /**
     * Sets up the animations and variables for rendering animations
     */
    protected CharacterAnimation() {
        x = 0;
        y = 0;
        loop = true;
        lookRight = true;
    }

    /**
     * Gets the animation from the texture sheet
     * 
     * @param file        - File containing the animation
     * @param frame_count - Number of frames used in animation
     * @return Animation from file
     */
    protected Animation<TextureRegion> getAnimation(String file, int frame_count) {
        Texture sheet = new Texture(Gdx.files.internal(file));

        width = (float) sheet.getWidth() / frame_count;
        height = sheet.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(sheet, (int) width, (int) height);
        TextureRegion[] frames = tmp[0];

        return new Animation<>(0.1f, frames);
    }

    /**
     * Set current animation to run
     */
    protected void setRunningAnimation() {
        currentAnimation = runAnimation;
    }

    /**
     * Set current animation to attack
     */
    protected void setAttackAnimation() {
        currentAnimation = attack1Animation;
    }

    /**
     * Set current animation to idle
     */
    protected void setIdleAnimation() {
        currentAnimation = idleAnimation;
    }

    /**
     * Set animation to hurt
     */
    protected void setHurtAnimation() {
        currentAnimation = hurtAnimation;
    }

    /**
     * Main loop
     *
     * Simply gets current frame based off of aniTime and draws animation using the
     * sprite
     * batch from main render loop.
     *
     * Animation selection is handled in the character class's update methods.
     * 
     * @param batch     - Batch assoicated with main rendering
     * @param deltaTime - Time unaffected by differing framerate
     */
    protected void render(SpriteBatch batch, float deltaTime) {
        // Update animation time
        aniTime += deltaTime;
        TextureRegion runFrame;
        // Get current frame
        runFrame = currentAnimation.getKeyFrame(aniTime, loop);
        batch.draw(runFrame, lookRight ? x - (width / 2) : x + (width / 2), y - (height / 2),
                lookRight ? width : -width, height);

    }

    /**
     * Set location of animation
     * 
     * @param x - X position in map
     * @param y - Y position in map
     */
    protected void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Reset animation
     */
    protected void resetAnimation() {
        aniTime = 0.0f;
    }

    /**
     * Set direction the animation must face
     * 
     * @param lookRight - True faces right, False faces left
     */
    protected void setDirection(boolean lookRight) {
        this.lookRight = lookRight;
    }

    /**
     * Set animation to loop
     * 
     * @param loop - True to loop animation, false to stop looping behaviour
     */
    protected void setLoop(boolean loop) {
        this.loop = loop;
    }

}