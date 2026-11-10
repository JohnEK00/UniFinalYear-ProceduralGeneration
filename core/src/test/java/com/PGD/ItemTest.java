package com.PGD;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/*
 * Tests for the Item class
 */
public class ItemTest {

    // Set up ItemFactory for testing (Textures must be null as GDX files does not
    // work in test classes)
    // Get sprite test will be absent due to this path finding issue with GDX in
    // test classes meaning sprite can't be assigned.

    // test the initialization of a Item instance
    @Test
    void testItemInitialization() {
        // Create test item
        ItemFactory.getItem("chest", null,
                10, 10, 5, 5, false, true, null, null);
        Item item = new Item(3, 7, ItemFactory.getItem("chest"));

        // Verify initialization
        assertEquals("chest", item.getType().getName());
        assertEquals(3, item.getX());
        assertEquals(7, item.getY());
    }
}