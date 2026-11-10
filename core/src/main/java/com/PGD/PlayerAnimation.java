package com.PGD;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/*
 * Player Animation class
 * 
 */
public class PlayerAnimation extends CharacterAnimation {
    // no of frames from texture sheet
    private static final int ATTACK2_ANI_FRAMES = 6;
    private static final int ATTACK3_ANI_FRAMES = 9;

    //all the animations that will be used based on direction
    private final Animation<TextureRegion> attack2Animation;

    private final Animation<TextureRegion> attack3Animation;



    /**
     * Sets up the animations and variables for rendering animations
     */
    public PlayerAnimation() {
        super();

        runAnimation = getAnimation("TinyRpgAssetPack/Characters(100x100)/Soldier/Soldier/Soldier-Walk.png", RUN_ANI_FRAMES);
        idleAnimation = getAnimation("TinyRpgAssetPack/Characters(100x100)/Soldier/Soldier/Soldier-Idle.png", IDLE_ANI_FRAMES);
        hurtAnimation = getAnimation("TinyRpgAssetPack/Characters(100x100)/Soldier/Soldier/Soldier-Hurt.png", HURT_ANI_FRAMES);
        attack1Animation = getAnimation("TinyRpgAssetPack/Characters(100x100)/Soldier/Soldier/Soldier-Attack01.png", ATTACK1_ANI_FRAMES);
        attack2Animation = getAnimation("TinyRpgAssetPack/Characters(100x100)/Soldier/Soldier/Soldier-Attack02.png", ATTACK2_ANI_FRAMES);
        attack3Animation = getAnimation("TinyRpgAssetPack/Characters(100x100)/Soldier/Soldier/Soldier-Attack03.png", ATTACK3_ANI_FRAMES);
        currentAnimation = runAnimation;
    }

    /**
     * Select player attack animation
     * 
     * @param attackType - 1 = Attack, 2 = Slash
     */
    public void setAttackAnimation(int attackType) {
        switch (attackType) {
            case 1:
                currentAnimation = attack1Animation;
                break;

            case 2:
                currentAnimation = attack2Animation;
                break;

            case 3:
                currentAnimation = attack3Animation;
        }
    }
}
