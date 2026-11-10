package com.PGD;

import java.util.LinkedList;

import org.junit.Before;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/*
 * this class tests the Region class
 */
public class RegionTest {

    @Before
    public void setUp() throws Exception {
        MapCellFactory.getCellType(CellState.TILE, null, true);
        MapCellFactory.getCellType(CellState.BLANK, null, true);
    }

    // test the region creation
    @Test
    public void testRegionCreation() {
        LinkedList<MapCell> cells = new LinkedList<>();
        cells.add(new MapCell(MapCellFactory.getCellType(CellState.TILE), 0, 0));
        cells.add(new MapCell(MapCellFactory.getCellType(CellState.TILE), 0, 1));

        Region region = new Region(cells);

        assertEquals(2, region.getRegionSize());
        assertFalse(region.checkMainRegion());
        assertFalse(region.checkMainRegionAccess());
    }

    // test the region connection
    @Test
    public void testRegionConnection() {
        LinkedList<MapCell> cellsA = new LinkedList<>();
        LinkedList<MapCell> cellsB = new LinkedList<>();

        cellsA.add(new MapCell(MapCellFactory.getCellType(CellState.TILE), 0, 0));
        cellsB.add(new MapCell(MapCellFactory.getCellType(CellState.TILE), 1, 1));

        Region regionA = new Region(cellsA);
        Region regionB = new Region(cellsB);

        regionA.joinRegions(regionB);
        assertTrue(regionA.isConnected(regionB));
        assertTrue(regionB.isConnected(regionA));
    }

    // test main region access setting
    @Test
    public void testMainRegionAccess() {
        LinkedList<MapCell> cells = new LinkedList<>();
        cells.add(new MapCell(MapCellFactory.getCellType(CellState.TILE), 0, 0));

        Region region = new Region(cells);
        assertFalse(region.checkMainRegionAccess());

        region.setMainRegionAccess(true);
        assertTrue(region.checkMainRegionAccess());
    }

    // test region comparison by size
    @Test
    public void testRegionComparison() {
        LinkedList<MapCell> smallCells = new LinkedList<>();
        smallCells.add(new MapCell(MapCellFactory.getCellType(CellState.TILE), 0, 0));
        LinkedList<MapCell> largeCells = new LinkedList<>();
        largeCells.add(new MapCell(MapCellFactory.getCellType(CellState.TILE), 0, 0));
        largeCells.add(new MapCell(MapCellFactory.getCellType(CellState.TILE), 1, 1));
        Region smallRegion = new Region(smallCells);
        Region largeRegion = new Region(largeCells);
        assertTrue(smallRegion.compareTo(largeRegion) < 0);
        assertTrue(largeRegion.compareTo(smallRegion) > 0);
        assertEquals(0, smallRegion.compareTo(new Region(smallCells)));
    }
}