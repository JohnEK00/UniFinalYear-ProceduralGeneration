package com.PGD;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * this class tests the GameInputProcessor class
 */
public class GameInputProcessorTest {
    private Player player;
    private GameInputProcessor inputProcessor;
    private float forceX = 0;
    private float forceY = 0;

    // Set up initial variables for testing
    @BeforeEach
    public void setup() throws Exception {
        player = new Player(0, 0, null);
        inputProcessor = new GameInputProcessor(player);
        forceX = player.getForce().x;
        forceY = player.getForce().y;
    }

    // test the keyDown method
    @Test
    public void testKeyDown() {
        // Simulate LEFT key press
        inputProcessor.keyDown(GameInputProcessor.LEFT);
        assertEquals(player.getForce().x, forceX - 1);

        // Simulate UP key press
        inputProcessor.keyDown(GameInputProcessor.UP);
        assertEquals(player.getForce().y, forceY + 1);
    }

    // test the keyUp method
    @Test
    public void testKeyUp() {
        // Simulate key press and release
        inputProcessor.keyDown(GameInputProcessor.LEFT);
        inputProcessor.keyUp(GameInputProcessor.LEFT);
        assertEquals(0, forceX);
    }
}