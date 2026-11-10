package com.PGD;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

/**
 * Class to handle the collision detection of the game
 */
public class Collider {
    private final List<Rectangle> fixedColliders;
    private final List<Item> interactableItems;
    private final EnemyManager enemyManager;

    // Constructor
    Collider(EnemyManager enemyManager) {
        fixedColliders = new ArrayList<>();
        interactableItems = new ArrayList<>();
        this.enemyManager = enemyManager;
    }

    /*
     * Check if the player collides with any of the fixed colliders
     * 
     * @param collider - Collider to check against
     * 
     * @return - True if there is a collision, false otherwise
     */
    public boolean checkCollision(Rectangle collider) {
        for (Rectangle rectangle : fixedColliders) {
            if (Intersector.overlaps(rectangle, collider)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Check if the player collides with any of the interactable items
     * 
     * @param collider - Collider to check against
     * 
     * @return - List of items that the player collided with
     */
    public ArrayList<Item> checkInteractableItemCollision(Rectangle collider) {
        ArrayList<Item> foundItems = new ArrayList<>();
        for (Item item : this.interactableItems) {
            if (item.getHitBox().overlaps(collider)) {
                foundItems.add(item);
            }
        }
        return foundItems;
    }

    /*
     * Add an interactable item to the list
     * 
     * @param item - Item to add
     */
    public void addInteractableItem(Item item) {
        interactableItems.add(item);
    }

    /*
     * Remove an interactable item from the list
     * 
     * @param item - Item to remove
     */
    public void removeInteractableItem(Item item) {
        interactableItems.remove(item);
    }

    /*
     * Update the wall colliders
     * 
     * @param wallCells - 2D array of wall cells
     * 
     * @param tileWidth - Width of the tile
     * 
     * @param tileHeight - Height of the tile
     */
    public void updateWallColliders(WallCell[][] wallCells, int tileWidth, int tileHeight) {
        for (WallCell[] wallCell : wallCells) {
            for (WallCell wall : wallCell) {
                if (wall.getCellType() == null) {
                    continue;
                }
                Rectangle rect = wall.getCellType().getRectangle(wall.getX(), wall.getY(), tileWidth, tileHeight);
                fixedColliders.add(rect);
            }
        }
    }

    /*
     * Add a fixed collider to the list
     * 
     * @param rect - Rectangle to add
     */
    public void addFixedCollider(Rectangle rect) {
        fixedColliders.add(rect);
    }

    /*
     * Remove a fixed collider from the list
     * 
     * @param - Rectangle to remove
     */
    public List<Rectangle> getFixedColliders() {
        return fixedColliders;
    }

    /**
     * Returns the enemies that the player hit, should there by any
     * 
     * @param collider - Attack hitbox
     * @param damage   - Intended damage
     * @return - Number of enemies killed by player
     */
    public int damageEnemies(Rectangle collider, int damage) {
        if(enemyManager == null) return 0;
        int deaths = 0;
        ArrayList<Enemy> removeEnemies = new ArrayList<>();

        for (Enemy enemy : enemyManager.getAllEnemies()) {
            if (enemy.getCollider().overlaps(collider)) {
                enemy.updateHealth(damage);

                if (enemy.isDead()) {
                    enemy.destroy();
                    removeEnemies.add(enemy);
                    deaths += 1;
                }
            }
        }

        if (!removeEnemies.isEmpty())
            enemyManager.remove(removeEnemies);
        return deaths;
    }
}