package com.PGD;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

/*
 * Tests for the Menu and hud
 */
public class MenuAndHudTest {
    private Main game;

    @BeforeEach
    public void setup() {
        game = new Main();
    }

    @Test
    public void testInitialMenuState() {
        assertTrue("Game should start in main menu", game.isMainMenu());
        assertFalse("Pause menu should not be visible at start", game.isPauseVisible());
    }

    @Test
    public void testMenuTransitions() {
        // Start game from main menu
        game.startGame();
        assertFalse("Main menu should not be visible after starting", game.isMainMenu());
        assertFalse("Pause should not be visible after starting", game.isPauseVisible());

        // Test pause menu
        game.togglePause();
        assertTrue("Pause menu should be visible after toggle", game.isPauseVisible());

        // Return to game
        game.togglePause();
        assertFalse("Pause menu should not be visible after second toggle", game.isPauseVisible());

        // Test end screen
        assertFalse("End screen should not be visible whilst the game is in progress", game.isEndVisible());

        // End game
        Main.endLevel();
        assertTrue("End screen should visible when a level is complete", game.isEndVisible());
    }

    @Test
    public void testHealthBarVisibility() {
        game.startGame(); // Exit main menu
        assertTrue("Health bar should be visible during gameplay", game.isHealthBarVisible());

        game.togglePause();
        assertFalse("Health bar should not be visible in pause menu", game.isHealthBarVisible());

        game.togglePause();
        assertTrue("Health bar should be visible again after unpausing", game.isHealthBarVisible());
    }

    @Test
    public void testMinimapVisibility() {
        game.startGame();
        assertTrue("Minimap should be visible during gameplay", game.isMinimapVisible());

        game.togglePause();
        assertFalse("Minimap should not be visible in pause menu", game.isMinimapVisible());
    }

    @Test
    public void testScoreVisibility() {
        game.startGame();
        assertTrue("Score should be visible during gameplay", game.isScoreVisible());

        game.togglePause();
        assertFalse("Score should not be visible in pause menu", game.isScoreVisible());
    }
}