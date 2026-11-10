package com.PGD;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * Handles all projectiles that are present on the map
 */
public class ProjectileManager {
    static ArrayList<Arrow> arrows = new ArrayList<>();
    private static final Texture arrTexture = new Texture("TinyRpgAssetPack/Arrow(Projectile)/Arrow01(32x32).png");

    private static final float ALIVE_TIME = 4f;

    public static void AddArrow(float x, float y, int xDir) {
        arrows.add(new Arrow(xDir, x, y, arrTexture));
    }

    /**
     * main loop update, updates all arrows, checks if arrow is still alive
     * and if timer runs out removes from list and plays animation
     *
     * @param sprite
     * @param collider
     * @param delta
     */
    public static void Update(SpriteBatch sprite, Collider collider, float delta) {
        ArrayList<Arrow> removeArrows = new ArrayList<>();

        for(Arrow arrow : arrows){
            arrow.update(sprite, collider, delta);

            if(arrow.getTimer()>=ALIVE_TIME){
                arrow.Destroy();
                removeArrows.add(arrow);
            }
        }

        for(Arrow arrow : removeArrows){
            arrows.remove(arrow);
        }
    }

    public static ArrayList<Arrow> GetArrows() {
        return arrows;
    }
}
