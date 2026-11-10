package com.PGD;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Used to store, updated and destory all enemies on map instance.
 */
public class EnemyManager {
    private final ArrayList<Enemy> allEnemies = new ArrayList<>();
    private final HashMap<Enemy, EnemyAnimation> allAnimations = new HashMap<>();
    private final Player player;

    /*
     * Constructor for EnemyManager
     */
    public EnemyManager(Player player) {
        this.player = player;
    }

    /**
     * Add enemy to manager
     * 
     * @param x - x coordinate in map
     * @param y - y coordinate in map
     */
    public void addEnemy(int x, int y) {
        EnemyAnimation EnemyAnimation = new EnemyAnimation();
        Enemy enemy = new Enemy(x, y, EnemyAnimation, player);

        allEnemies.add(enemy);
        allAnimations.put(enemy, EnemyAnimation);
    }

    /**
     * Remove enemies from manager
     */
    public void remove(ArrayList<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            allEnemies.remove(enemy);
            allAnimations.remove(enemy);
        }
    }

    /**
     * Update all enemies in manager
     * 
     * @param deltaTime - Time unaffected by differing framerate
     * @param collider  - Check interactions with map collison
     * @param map       - Used to remove any item broken by character
     */
    public void updateEnemies(float deltaTime, Collider collider, Map map) {
        for (Enemy enemy : allEnemies) {
            enemy.update(deltaTime, collider, map);
        }
    }

    /**
     * Render all enemy animations in manager
     * 
     * @param batch     - Batch assoicated with main rendering
     * @param deltaTime - Time unaffected by differing framerate
     */
    public void renderEnemyAnimations(SpriteBatch batch, float deltaTime) {
        for (EnemyAnimation animation : allAnimations.values()) {
            animation.render(batch, deltaTime);
        }
    }

    /**
     * Get all enemies in manager
     */
    public ArrayList<Enemy> getAllEnemies() {
        return allEnemies;
    }
}