package com.PGD;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/*
 * this class tests the Player class
 */
public class PlayerTest {

    // test the player movement
    @Test
    public void testPlayerMovement() {
        Player player = new Player(0, 0, null);
        Collider collider = new Collider(new EnemyManager(player));
        // Test initial state
        assertEquals(0, player.getPositionX());
        assertEquals(0, player.getPositionY());
        assertEquals(0, player.getVelocity().x);
        assertEquals(0, player.getVelocity().y);

        player.pressLeft();
        player.update(1, collider, null);
        assert (player.getPositionX() != 0);
        player.releaseLeft();

        // testing balance the player
        player.pressRight();
        player.update(1, collider, null);
        assert (player.getPositionX() == 0);
        player.releaseRight();

        player.pressUp();
        player.update(1, collider, null);
        assert (player.getPositionY() != 0);
        player.releaseUp();

        player.pressDown();
        player.update(1, collider, null);
        assertEquals(0, player.getPositionY());
    }

    // test the player update
    @Test
    public void testPlayerUpdate() {
        Player player = new Player(0, 0, null);
        player.pressRight();
        player.update(1, new Collider(new EnemyManager(player)), null);
        assert (player.getVelocity().x != 0);
        assertEquals(0, player.getPositionY());
    }
}