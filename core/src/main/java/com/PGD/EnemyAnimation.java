package com.PGD;

/**
 * Class to handle the enemy animations
 */
public class EnemyAnimation extends CharacterAnimation {

    /**
     * Sets up the animations for the enemy
     */
    public EnemyAnimation() {
        super();
        runAnimation = getAnimation("TinyRpgAssetPack/Characters(100x100)/Orc/Orc/Orc-Walk.png", RUN_ANI_FRAMES);
        idleAnimation = getAnimation("TinyRpgAssetPack/Characters(100x100)/Orc/Orc/Orc-Idle.png", IDLE_ANI_FRAMES);
        hurtAnimation = getAnimation("TinyRpgAssetPack/Characters(100x100)/Orc/Orc/Orc-Hurt.png", HURT_ANI_FRAMES);
        attack1Animation = getAnimation("TinyRpgAssetPack/Characters(100x100)/Orc/Orc/Orc-Attack01.png",
                ATTACK1_ANI_FRAMES);
        currentAnimation = runAnimation;
    }
}