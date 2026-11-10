package com.PGD;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Rectangle;

/*
 * Tests for the Collider class
 */
public class ColliderTest {
    private Collider collider;

    // Set up collider instance for testing
    @BeforeEach
    public void setup() throws Exception {
        EnemyManager enemyManager = new EnemyManager(null);
        collider = new Collider(enemyManager);
        ItemFactory.getItem("box", null,
                10, 10, 5, 5, true, false, null, null);
    }

    // Test addFixedCollider method
    @Test
    public void testAddFixedCollider() throws Exception {
        // Create test rectangle and invoke method
        Rectangle box = new Rectangle(0, 0, 16, 16);
        collider.addFixedCollider(box);

        // Access field containing fixed colliders
        Field colliderField = Collider.class.getDeclaredField("fixedColliders");
        colliderField.setAccessible(true);
        List<Rectangle> fixedColliders = (List<Rectangle>) colliderField.get(collider);

        assertEquals(1, fixedColliders.size());
    }

    // Test getfixedColliders method
    @Test
    public void testGetFixedColliders() throws Exception {
        // Create test rectangle and invoke add Fixed collider
        Rectangle box = new Rectangle(0, 0, 16, 16);
        collider.addFixedCollider(box);

        // Invoke method
        List<Rectangle> fixedColliders = collider.getFixedColliders();

        assertNotNull(fixedColliders);
        assertEquals(1, fixedColliders.size());
    }

    // Test addDynamicCollider method
    @Test
    public void testAddInteractableItem() throws Exception {
        // Create test rectangle
        Item item = new Item(0, 0, ItemFactory.getItem("box"));

        // Invoke method
        collider.addInteractableItem(item);

        // Access field containing dynamic colliders
        Field colliderField = Collider.class.getDeclaredField("interactableItems");
        colliderField.setAccessible(true);
        ArrayList<Item> dynamicColliders = (ArrayList<Item>) colliderField.get(collider);

        assertTrue(dynamicColliders.contains(item));
    }

    // Test checkCollision method
    @Test
    public void testCheckCollision() throws Exception {
        // Create test rectangle and invoke addFixedCollider
        Rectangle box = new Rectangle(0, 0, 16, 16);
        collider.addFixedCollider(box);

        assertTrue(collider.checkCollision(box));
    }

    // Test checkDynamicCollision method
    @Test
    public void testCheckInteractableItemCollision() throws Exception {
        // Create test rectangle and invoke addDynamicCollider
        Item item = new Item(0, 0, ItemFactory.getItem("box"));
        collider.addInteractableItem(item);

        Rectangle box = new Rectangle(0, 0, 16, 16);

        assertNotNull(collider.checkInteractableItemCollision(box));
    }

    // Test updateWallColliders method
    @Test
    public void testUpdateWallColliders() throws Exception {
        // Create WallCells for test
        WallCellFactory.getWallType(0, null, 16, 16, 0, 0);
        WallCell[][] wallCells = new WallCell[10][10];

        // Populate cells with non-null type
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                wallCells[x][y] = new WallCell(null, x, y);
                wallCells[x][y].setType(WallCellFactory.getWallType(0));
            }
        }

        // Invoke method
        collider.updateWallColliders(wallCells, 16, 16);

        assertEquals(100, collider.getFixedColliders().size());
    }

    // Test removeInteractableItem method
    @Test
    public void testRemoveInteractableItem() throws Exception {
        // Create test rectangle and invoke addDynamicCollider
        Item item = new Item(0, 0, ItemFactory.getItem("box"));
        collider.addInteractableItem(item);
        collider.removeInteractableItem(item);
        Field colliderField = Collider.class.getDeclaredField("interactableItems");
        colliderField.setAccessible(true);
        ArrayList<Item> dynamicColliders = (ArrayList<Item>) colliderField.get(collider);
        assertTrue(dynamicColliders.isEmpty());
    }

    // Test getInteractableItems method
    @Test
    public void testGetFixedCollidersNull() throws Exception {
        // Create test rectangle and invoke addFixedCollider
        Rectangle box = new Rectangle(0, 0, 16, 16);
        collider.addFixedCollider(box);
        assertNotNull(collider.getFixedColliders());
    }

}