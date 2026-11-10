package com.PGD;

import java.util.ArrayList;
import java.util.Objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

enum PlayerState {
    IDLE,
    RUNNING,
    HURT,
    ATTACKING,
    SHOOTING
}

/**
 * Class to handle the player character
 */
public class Player {
    // Constants for player movement and interaction distance
    public static final float SPEED = 100f;
    public static final float INTERACT_DISTANCE = 40f;

    // Position of the player and velocity of the player
    private float posX, posY;
    private final Vector2 velocity;
    private final Vector2 force;
    private int xDirection;
    private PlayerState playerState;
    private final Rectangle hitBox;

    // Attack box
    private int attackType;

    // Rectangle for the player
    private final Rectangle rectangle;

    // Size of the player
    private final float height;
    private final float width;

    // Time for the player to attack
    private final float attackTime = 0.65f;
    private final float dmgTime = 0.3f;
    private float attackTimeCount;
    private boolean attackOccured = false;

    private final float shootTime = 0.85f;
    private float shootTimeCount;


    private final float hurtTime = 0.3f; 
    private float hurtTimeCount;

    // Interact with items
    private boolean interact;
    private Item closestItem;

    // Animation of the player
    private final PlayerAnimation playerAnimation;

    // Health of the player
    private final int maxHealth = 100;
    private int currentHealth = 100;

    private int score = 0;

    public Player(int x, int y, PlayerAnimation playerAnimation) {
        this.playerAnimation = playerAnimation;
        posX = x;
        posY = y;
        velocity = new Vector2(0, 0);
        force = new Vector2(0, 0);
        xDirection = 1;
        playerState = PlayerState.IDLE;

        height = 8;
        width = 8;

        rectangle = new Rectangle(x - width / 2, y - (height * 1.5f), width, height);
        hitBox = new Rectangle(x - width / 2, y - (height * 1.5f), width, height);

        attackTimeCount = 0.0f;
        hurtTimeCount = 0.0f;
        shootTimeCount = 0.0f;
        
        interact = false;
        closestItem = null;
    }

    /**
     * update for character each frame, updates
     * position.
     *
     * before updating the position of the player, a rectangle is drawn to represent
     * a hitbox
     * then collision class is used to check if players hitbox collides with a fixed
     * collider.
     * If there is collision then the position does not get updated.
     *
     * Collision box for interacting is the same as the attack box, will just use a
     * check
     * if player is attacking when detecting collision
     *
     * After movement is calculated a hitbox for attacking is calculated, this will
     * work the same way
     * as movement collider but should be used to detect what the player is hitting
     * when attacking.
     *
     * Finally the animation is played, the animation that plays depends on whether
     * the player is
     * attacking, rolling or just walking.
     *
     *
     * 
     * @param deltaTime
     */
    public void update(float deltaTime, Collider collider, Map map) {

        float hitboxWidth = width * 4;
        float hitboxHeight = height * 4;

        // position calculation dependent on position of hitbox
        float oldPosX = posX;
        float oldPosY = posY;

        //velocity logic
        if(playerState != PlayerState.ATTACKING && playerState != PlayerState.HURT && playerState != PlayerState.SHOOTING) {
            velocity.x = SPEED * force.x;
            velocity.y = SPEED * force.y;
        } else {
            velocity.x = 0.0f;
            velocity.y = 0.0f;
        }

        // hitbox
        ArrayList<Item> items;

        posX += velocity.x * deltaTime;
        this.rectangle.setPosition(posX - width / 2, posY + 6 - (height * 1.5f));
        items = collider.checkInteractableItemCollision(this.rectangle);
        if (collider.checkCollision(this.rectangle) || !items.isEmpty()) {
            posX = oldPosX;
        }
        posY += velocity.y * deltaTime;
        this.rectangle.setPosition(posX - width / 2, posY + 6 - (height * 1.5f));
        items = collider.checkInteractableItemCollision(this.rectangle);
        if (collider.checkCollision(this.rectangle) || !items.isEmpty()) {
            posY = oldPosY;
        }

        // attack box
        if (force.x != 0) {
            xDirection = (int) force.x;
        }

        // Animation
        /**
         * Make sure hitbox width is never negative since collision wont
         * work if it is
         */
        if (playerAnimation != null) {
            playerAnimation.setPosition(posX, posY);


            
            playerAnimation.setDirection(xDirection == 1);

            float xOffset = xDirection > 0 ? -width : width * 3;
            xOffset *= xDirection;
            float yOffset;

            switch (playerState) {
                case RUNNING:
                    yOffset = height * 2;

                    if (force.isZero()) {
                        playerState = PlayerState.IDLE;
                    }
                    break;

                case ATTACKING:
                    attackTimeCount += deltaTime;
                    switch (this.attackType) {
                        case 1:
                            break;
                        case 2:
                            hitboxHeight = height * 2;
                            break;
                    }
                    if (attackTimeCount >= attackTime) {
                        playerState = PlayerState.RUNNING;
                        playerAnimation.setRunningAnimation();
                        attackOccured = false;
                    }

                    yOffset = width * 2;

                    break;

                case HURT:
                    hurtTimeCount += deltaTime;
                    yOffset = height * 2;

                    if (hurtTimeCount >= hurtTime) {
                        playerState = PlayerState.RUNNING;
                        playerAnimation.setRunningAnimation();
                    }
                    break;

                case SHOOTING:
                    shootTimeCount += deltaTime;
                    yOffset = height * 2;

                    if(shootTimeCount >= shootTime){
                        ProjectileManager.AddArrow(posX, posY, xDirection);
                        playerState = PlayerState.RUNNING;
                        playerAnimation.setRunningAnimation();
                    }
                    break;

                    

                default:
                    yOffset = height * 2;

                    playerAnimation.setIdleAnimation();
                    if (!force.isZero()) {
                        playerState = PlayerState.RUNNING;
                        playerAnimation.setRunningAnimation();
                    }
                    break;
            }
            hitBox.set(posX + xOffset, posY - yOffset, hitboxWidth, hitboxHeight);
        }

        //get the closest item that is interactable
        if(map != null) {
            if(playerState == PlayerState.ATTACKING && attackTimeCount >= dmgTime && attackOccured == false && attackType != 3) {
                damageEnemies(collider);
                breakItems(collider, map);
                attackOccured = true;
            }

            closestItem = closestInteractableItem(map);
            interact = closestItem != null
                    && Vector2.dst(posX, posY, closestItem.getX(), closestItem.getY()) <= INTERACT_DISTANCE;

        }
    }

    /**
     * Damage enemy characters in hitbot at time of attack
     */
    private void damageEnemies(Collider collider) {
        int deaths = collider.damageEnemies(hitBox, -10);
        addScore(10 * deaths);
    }

    /**
     * gets a list of items that are within the attack box and destroys those items
     * as long as they are destructible
     * 
     * @param collider
     * @param map
     */
    private void breakItems(Collider collider, Map map) {
        ArrayList<Item> items = collider.checkInteractableItemCollision(hitBox);

        for (Item item : items) {
            if (item.getType().isDestructible()) {
                map.getItems().remove(item);
                item.destroy();
                addScore(1);
                collider.removeInteractableItem(item);
            }
        }
    }

    /**
     * gets the closes item to that is interactable to the player
     * 
     * @param map
     * @return
     */
    private Item closestInteractableItem(Map map) {
        ArrayList<Item> items = map.getItems();
        if (items.isEmpty()) {
            return null;
        }

        Item closest = null;
        float closestDistance = Float.MAX_VALUE;
        for (Item item : items) {
            if (item.getType().isInteractable() && !item.isUsed()) {
                float cDist = Vector2.dst(posX, posY, item.getX(), item.getY());
                if (cDist < closestDistance) {
                    closestDistance = cDist;
                    closest = item;
                }
            }
        }
        return closest;
    }

    public Rectangle getCollider() {
        return rectangle;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    /**
     * sets player state to attacking
     * 
     * @param type
     */
    public void attack(int type){
        if(Objects.requireNonNull(playerState) != PlayerState.ATTACKING && playerState != PlayerState.SHOOTING && playerAnimation != null) {

            this.attackType = type;
            playerAnimation.resetAnimation();
            playerState = PlayerState.ATTACKING;
            attackTimeCount = 0.0f;
            playerAnimation.setAttackAnimation(type);
        }
    }

    public void shoot(){
        if(Objects.requireNonNull(playerState) != PlayerState.ATTACKING && playerState != PlayerState.SHOOTING && playerAnimation != null) {

            playerAnimation.resetAnimation();
            playerState = PlayerState.SHOOTING;
            shootTimeCount = 0.0f;
            playerAnimation.setAttackAnimation(3);
        }
    }

    /**
     * press down function updates the velocity
     * while the correct key is being pressed
     */
    public void pressLeft() {
        force.x -= 1;
    }

    public void pressRight() {
        force.x += 1;
    }

    public void pressUp() {
        force.y += 1;
    }

    public void pressDown() {
        force.y -= 1;
    }

    public void interact() {
        if (closestItem != null && interact) {
            if (closestItem.getType().getName().equals("chest")) {
                addScore(15);
                updateHealth(20);
            }

            closestItem.interact();

            if (closestItem.getType().getName().equals("door")) {
                Main.endLevel();
            }
        }
    }

    /**
     * release functions update players velocity when
     * the movement button has been released
     */
    public void releaseLeft() {
        force.x += 1;
    }

    public void releaseRight() {
        force.x -= 1;
    }

    public void releaseUp() {
        force.y -= 1;
    }

    public void releaseDown() {
        force.y += 1;
    }

    /**
     * getter functions
     * 
     * @return
     */
    public float getPositionX() {
        return posX;
    }

    public float getPositionY() {
        return posY;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getAngle() {
        return force.angleDeg();
    }

    public Vector2 getForce() {
        return force;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Update current health
     */
    public void updateHealth(int health) {
        if (Objects.requireNonNull(playerState) != PlayerState.HURT && playerAnimation != null) {
            if (currentHealth + health > maxHealth) {
                currentHealth = maxHealth;
            } else {
                currentHealth += health;
            }

            if (health < 0) {
                playerAnimation.resetAnimation();
                playerState = PlayerState.HURT;
                hurtTimeCount = 0.0f;
                playerAnimation.setHurtAnimation();
            }
        }
    }

    /**
     * Returns true if health is below or equal to zero
     */
    public boolean isDead() {
        return currentHealth <= 0;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        score += points;
    }
}
