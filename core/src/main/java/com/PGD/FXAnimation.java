package com.PGD;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Class to handle the animations of the effects
 */
public class FXAnimation {
    private final static Texture texture = new Texture(
            Gdx.files.internal("Free_Smoke_Fx_Pixel_2/Free Smoke Fx  Pixel 05.png"));
    private static final ArrayList<Animation<TextureRegion>> animations = new ArrayList<>();
    private static final ArrayList<float[]> coords = new ArrayList<>();
    private static final ArrayList<Float> aniTimes = new ArrayList<>();

    /**
     * Play the animation
     * 
     * @param batch     - SpriteBatch to draw the animation
     * @param deltaTime - Time unaffected by differing framerate
     */
    public static void playAnimation(SpriteBatch batch, float deltaTime) {
        Iterator<Animation<TextureRegion>> it = animations.iterator();
        int i = 0;

        while (it.hasNext()) {
            Animation<TextureRegion> animation = it.next();
            float[] coord = coords.get(i);
            aniTimes.set(i, aniTimes.get(i) + deltaTime);
            float aniTime = aniTimes.get(i);

            aniTime += deltaTime;
            TextureRegion runFrame = animation.getKeyFrame(aniTime, false);
            batch.draw(runFrame, coord[0] - 20, coord[1] - 20);

            i++;
        }
    }

    /**
     * Add an animation to the list
     * 
     * @param x               - x coordinate in map
     * @param y               - y coordinate in map
     * @param selectAnimation - Animation to select
     */
    public static void addAnimation(float x, float y, int selectAnimation) {
        animations.add(getAnimation(texture, 11, selectAnimation));
        coords.add(new float[] { x, y });
        aniTimes.add(0f);
    }

    /**
     * Get the animation from the texture sheet
     * 
     * @param sheet           - Texture sheet containing the animation
     * @param frame_count     - Number of frames used in animation
     * @param selectAnimation - Animation to select
     * @return Animation from file
     */
    public static Animation<TextureRegion> getAnimation(Texture sheet, int frame_count, int selectAnimation) {

        int width = sheet.getWidth() / frame_count;
        int height = sheet.getHeight() / 15;

        TextureRegion[][] tmp = TextureRegion.split(sheet, width, height);
        TextureRegion[] frames = tmp[selectAnimation];

        return new Animation<>(0.05f, frames);
    }
}