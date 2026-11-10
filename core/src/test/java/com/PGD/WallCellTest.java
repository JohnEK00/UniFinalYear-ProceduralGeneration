package com.PGD;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Rectangle;

/*
 * Tests for the WallCell class
 */
public class WallCellTest {

    // Set up WallCellFactory for testing (TextureRegions must be null as GDX files
    // does not work in test classes)
    @BeforeEach
    public void setup() throws Exception {
        WallCellFactory.getWallType(0, null, 16, 16, 0, 0);
        WallCellFactory.getWallType(1, null, 6, 16, 5, 0);
    }

    // test the initialization of a WallCell instance
    @Test
    void testWallCellInitialization() {
        // Create test WallCell
        WallCell cell = new WallCell(WallCellFactory.getWallType(0), 10, 4);

        // Verify initialization
        assertEquals(0, cell.getType());
        assertNotNull(cell.getCellType());
        assertEquals(10, cell.getX());
        assertEquals(4, cell.getY());
    }

    // test the setState method
    @Test
    void testSetState() {
        // Create test WallCell
        WallCell cell = new WallCell(WallCellFactory.getWallType(0), 10, 4);

        // Verify state
        assertEquals(0, cell.getType());

        // Change state
        cell.setType(WallCellFactory.getWallType(1));

        // Verity new state
        assertEquals(1, cell.getType());
    }

    // test that retrieving an undefined wall type returns null
    @Test
    void testUndefinedWallType() {
        assertNull(WallCellFactory.getWallType(999));
    }

    // rest immutability of x and y coordinates
    @Test
    void testWallCellCoordinatesImmutability() {
        WallCell cell = new WallCell(WallCellFactory.getWallType(0), 15, 25);
        // Ensure coordinates are correctly set
        assertEquals(15, cell.getX());
        assertEquals(25, cell.getY());
    }

    // rest the getRectangle method in WallCellType
    @Test
    void testWallCellTypeRectangle() {
        WallCellType type = WallCellFactory.getWallType(0);
        assertNotNull(type);
        // Generate rectangle based on wall type and check dimensions
        Rectangle rect = type.getRectangle(3, 2, 16, 16);
        assertEquals(3 * 16 + type.xOffset, rect.x);
        assertEquals(2 * 16 + type.yOffset, rect.y);
        assertEquals(type.width, rect.width);
        assertEquals(type.height, rect.height);
    }
}