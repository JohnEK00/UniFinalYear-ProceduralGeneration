package com.PGD;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * this class tests the MapCell class
 */
class MapCellTest {

    // Set up MapCellFactory for testing (TextureRegions must be null as GDX files
    // does not work in test classes)
    @BeforeEach
    public void setup() throws Exception {
        MapCellFactory.getCellType(CellState.TILE, null, true);
        MapCellFactory.getCellType(CellState.BLANK, null, false);
    }

    // test the initialization of a MapCell instance
    @Test
    void testMapCellInitialization() {
        MapCell cell = new MapCell(MapCellFactory.getCellType(CellState.TILE), 5, 10);

        // Verify initialization
        assertEquals(CellState.TILE, cell.getState().getCellState());
        assertEquals(5, cell.getX());
        assertEquals(10, cell.getY());
    }

    // test the state of a MapCell instance
    @Test
    void testSetState() {
        MapCell cell = new MapCell(MapCellFactory.getCellType(CellState.TILE), 5, 10);
        cell.setState(MapCellFactory.getCellType(CellState.BLANK));
        // cell state should be updated to CellState.BLANK
        assertEquals(CellState.BLANK, cell.getState().getCellState());
        // Change the state back and verify
        cell.setState(MapCellFactory.getCellType(CellState.TILE));
        assertEquals(CellState.TILE, cell.getState().getCellState());
    }
}