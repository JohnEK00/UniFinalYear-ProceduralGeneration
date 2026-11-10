package com.PGD;

import java.util.ArrayList;
import java.util.Objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

enum EnemyState {
    IDLE,
    RUNNING,
    HURT,
    ATTACKING
}

/*
 * Class to handle the enemy character
 */
public class Enemy {
    private static final float SPEED = 60f;

    // Position of the enemy
    private float posX, posY;
    private final Vector2 velocity;

    // Direction of the enemy
    private int xDirection;
    private EnemyState enemyState;

    // Hitbox and rectangle for the enemy
    private final Rectangle hitBox;
    private final Rectangle rectangle;

    // Size of the enemy
    private final float height;
    private final float width;

    // Time for the enemy to attack
    private final float attackTime = 0.65f;
    private final float dmgTime = 0.3f;
    private float attackTimeCount;
    private boolean attackOccured = false;

    // Time for the enemy to be hurt
    private final float hurtTime = 0.8f;
    private float hurtTimeCount;

    // Health of the enemy
    private final int maxHealth = 20;
    private int currentHealth = 20;

    // Animation of the enemy
    private final EnemyAnimation EnemyAnimation;
    private final Player player;

    /**
     * Create enemy instance at given location.
     * 
     * @param x              - x coordinate in map
     * @param y              - y coordinate in map
     * @param EnemyAnimation - Animations character will follow
     * @param player         - Player character needed to attack/chase
     */
    public Enemy(int x, int y, EnemyAnimation EnemyAnimation, Player player) {
        this.EnemyAnimation = EnemyAnimation;
        this.player = player;

        posX = x;
        posY = y;

        velocity = new Vector2(0, 0);

        xDirection = 1;
        enemyState = EnemyState.IDLE;

        height = 8;
        width = 8;

        rectangle = new Rectangle(x - width / 2, y - (height * 1.5f), width, height);
        hitBox = new Rectangle(x - width / 2, y - (height * 1.5f), width, height);

        attackTimeCount = 0.0f;
        hurtTimeCount = 0.0f;
    }

    /**
     * Update for character each frame, updating position.
     * 
     * @param deltaTime - Time unaffected by differing framerate
     * @param collider  - Check interactions with map collison
     * @param map       - Used to remove any item broken by character
     */
    public void update(float deltaTime, Collider collider, Map map) {

        float hitboxWidth = width * 4;
        float hitboxHeight = height * 4;

        // position calculation dependent on position of hitbox
        float oldPosX = posX;
        float oldPosY = posY;

        Vector2 direction = new Vector2();

        direction.x = player.getPositionX() - posX;
        direction.y = player.getPositionY() - posY;

        // If within range of player attack
        if (direction.x < 15 && direction.x > -15 && direction.y < 15 && direction.y > -15
                && enemyState != EnemyState.HURT) {
            attack();
        }

        // Enemy will chase player when within a specific distance, but not in attacking
        // distance.
        else if (direction.x < 100 && direction.x > -100 && direction.y < 100 && direction.y > -100
                && enemyState != EnemyState.ATTACKING && enemyState != EnemyState.HURT) {
            enemyState = EnemyState.RUNNING;
        }

        else if (enemyState != EnemyState.ATTACKING && enemyState != EnemyState.HURT) {
            enemyState = EnemyState.IDLE;
        }

        direction.nor();

        // velocity logic
        if (enemyState == EnemyState.RUNNING) {
            velocity.x = SPEED * direction.x;
            velocity.y = SPEED * direction.y;
        } else {
            velocity.x = 0.0f;
            velocity.y = 0.0f;
        }

        // hitbox
        ArrayList<Item> items;

        // Before updating the position of the player, a rectangle is drawn to represent
        // a hitbox
        // then collision class is used to check if players hitbox collides with a fixed
        // collider.
        // If there is collision then the position does not get updated.
        posX += velocity.x * deltaTime;
        this.rectangle.setPosition(posX - width / 2, posY + 8 - (height * 1.5f));
        items = collider.checkInteractableItemCollision(this.rectangle);
        if (collider.checkCollision(this.rectangle) || !items.isEmpty()) {
            posX = oldPosX;
        }

        posY += velocity.y * deltaTime;
        this.rectangle.setPosition(posX - width / 2, posY + 8 - (height * 1.5f));
        items = collider.checkInteractableItemCollision(this.rectangle);
        if (collider.checkCollision(this.rectangle) || !items.isEmpty()) {
            posY = oldPosY;
        }

        // attack box
        if (direction.x > 0) {
            xDirection = 1;
        } else if (direction.x == 0) {
            xDirection = 0;
        } else {
            xDirection = -1;
        }

        // Animation is played, the animation that plays depends on whether the enemy is
        // attacking, ideling or just walking.
        if (EnemyAnimation != null) {
            EnemyAnimation.setPosition(posX, posY);

            attackTimeCount += deltaTime;
            hurtTimeCount += deltaTime;

            if (enemyState != EnemyState.IDLE) // Should not be changing directions when not seeing player
                EnemyAnimation.setDirection(xDirection == 1);

            float xOffset = xDirection > 0 ? -width : width * 3;
            xOffset *= xDirection;

            float yOffset = height * 2;

            switch (enemyState) {
                case RUNNING:

                    EnemyAnimation.setRunningAnimation();
                    break;

                case ATTACKING:
                    if (attackTimeCount >= attackTime) {
                        enemyState = EnemyState.RUNNING;
                        EnemyAnimation.setRunningAnimation();
                        attackOccured = false;
                    }

                    yOffset = width * 2;
                    break;

                case HURT:
                    if (hurtTimeCount >= hurtTime) {
                        enemyState = EnemyState.RUNNING;
                        EnemyAnimation.setRunningAnimation();
                    }
                    break;

                default:
                    EnemyAnimation.setIdleAnimation();
                    break;
            }
            hitBox.set(posX + xOffset, posY - yOffset, hitboxWidth, hitboxHeight);
        }

        // get the closest item that is interactable
        if (map != null) {
            if (enemyState == EnemyState.ATTACKING && attackTimeCount >= dmgTime && attackOccured == false) {
                damagePlayer();
                breakItems(collider, map);
                attackOccured = true;
            }

        }
    }

    /**
     * Damage player character if in hitbot at time of attack
     */
    private void damagePlayer() {
        Rectangle playerCollider = player.getCollider();

        if (playerCollider.overlaps(hitBox)) {
            player.updateHealth(-5);
        }

    }

    /**
     * Gets a list of items that are within the attack box and destroys those items
     * as long as they are destructible
     * 
     * @param collider - Check interactions with map collison
     * @param map      - Used to remove any item broken by character
     */
    private void breakItems(Collider collider, Map map) {
        ArrayList<Item> items = collider.checkInteractableItemCollision(hitBox);

        for (Item item : items) {
            if (item.getType().isDestructible()) {
                map.getItems().remove(item);
                item.destroy();
                collider.removeInteractableItem(item);
            }
        }
    }

    /**
     * Get enemy collider
     */
    public Rectangle getCollider() {
        return rectangle;
    }

    /**
     * Get enemy attack collider (used in debugging mode)
     */
    public Rectangle getHitBox() {
        return hitBox;
    }

    /**
     * Sets enemy state to attacking
     */
    private void attack() {
        if (Objects.requireNonNull(enemyState) != EnemyState.ATTACKING && EnemyAnimation != null) {
            EnemyAnimation.resetAnimation();
            enemyState = EnemyState.ATTACKING;
            attackTimeCount = 0.0f;
            EnemyAnimation.setAttackAnimation();
        }
    }

    /**
     * Get enemy position x
     */
    public float getPositionX() {
        return posX;
    }

    /**
     * Get enemy position y
     */
    public float getPositionY() {
        return posY;
    }

    /**
     * Update current health
     * 
     * @param health - To add or subtract from current health
     */
    public void updateHealth(int health) {
        if (Objects.requireNonNull(enemyState) != EnemyState.HURT && EnemyAnimation != null) {
            if (currentHealth + health > maxHealth) {
                currentHealth = maxHealth;
            } else {
                currentHealth += health;
            }

            if (health < 0) {
                EnemyAnimation.resetAnimation();
                enemyState = EnemyState.HURT;
                hurtTimeCount = 0.0f;
                EnemyAnimation.setHurtAnimation();
            }
        }
    }

    /**
     * Play death animation of enemy character
     */
    public void destroy() {
        FXAnimation.addAnimation(posX - 10, posY - 10, 5); // -10 as the animation starts to the top right of the
                                                           // character instead of on their body
    }

    /**
     * Returns true if health is below or equal to zero
     */
    public boolean isDead() {
        return currentHealth <= 0;
    }
}