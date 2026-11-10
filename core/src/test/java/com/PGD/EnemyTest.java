package com.PGD;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/*
 * this class tests the Player class
 */
public class EnemyTest {

    // Test enemy instance creation.
    @Test
    public void testEnemyInitialization() {
        Enemy enemy = new Enemy(10, 5, null, null);

        assertNotNull(enemy);
        assertEquals(10, enemy.getPositionX());
        assertEquals(5, enemy.getPositionY());
    }

    // Test enemy update
    @Test
    public void testEnemyUpdate() {
        Player player = new Player(50, 50, null);
        Enemy enemy = new Enemy(0, 0, null, player);

        enemy.update(1, new Collider(new EnemyManager(player)), null);

        // Assert enemy has moved away from position (toward player)
        assert (enemy.getPositionX() > 0);
        assert (enemy.getPositionY() > 0);
    }

    // rest enemy does not move when far from player
    @Test
    public void testEnemyIdleWhenFar() {
        Player player = new Player(500, 500, null);
        Enemy enemy = new Enemy(0, 0, null, player);
        enemy.update(1, new Collider(new EnemyManager(player)), null);
        assertTrue(enemy.getPositionX() == 0, "Enemy should not move when player is too far");
        assertTrue(enemy.getPositionY() == 0, "Enemy should not move when player is too far");
    }

    // rest enemy attacks player when in range
    @Test
    public void testEnemyAttacksPlayer() {
        Player player = new Player(10, 10, null);
        Enemy enemy = new Enemy(5, 5, null, player);
        enemy.update(1, new Collider(new EnemyManager(player)), null);

        assertTrue(enemy.getCollider().overlaps(player.getCollider()), "Enemy should be in attacking state");
    }

    // expansion of testEnemyIdleWhenFar
    @Test
    public void testEnemyDoesNotMoveWhenFar() {
        Player player = new Player(500, 500, null);
        Enemy enemy = new Enemy(10, 10, null, player);
        enemy.update(1, new Collider(new EnemyManager(player)), null);
        assertTrue(enemy.getPositionX() == 10, "Enemy should not move when player is too far");
        assertTrue(enemy.getPositionY() == 10, "Enemy should not move when player is too far");
    }

    /*
     * 
     * =============================================================================
     * ====================
     * Remaining tests conducted with manual testing, as these involve colliders and
     * human interaction.
     * =============================================================================
     * ====================
     * 
     */
}