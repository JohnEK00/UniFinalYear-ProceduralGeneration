package com.PGD;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * this class tests the Map class
 */
class MapTest {

    // Set up MapCellFactory for testing (TextureRegions must be null as GDX files
    // does not work in test classes)
    @BeforeEach
    public void setup() throws Exception {
        MapCellFactory.getCellType(CellState.TILE, null, true);
        MapCellFactory.getCellType(CellState.TILE_VAR1, null, true);
        MapCellFactory.getCellType(CellState.TILE_VAR2, null, true);
        MapCellFactory.getCellType(CellState.TILE_VAR3, null, true);
        MapCellFactory.getCellType(CellState.TILE_LEFT, null, true);
        MapCellFactory.getCellType(CellState.TILE_RIGHT, null, true);
        MapCellFactory.getCellType(CellState.TILE_UP_LEFT, null, true);
        MapCellFactory.getCellType(CellState.TILE_UP_RIGHT, null, true);
        MapCellFactory.getCellType(CellState.TILE_LIGHT, null, true);
        MapCellFactory.getCellType(CellState.HALF_LEFT, null, true);
        MapCellFactory.getCellType(CellState.HALF_RIGHT, null, true);
        MapCellFactory.getCellType(CellState.BLANK, null, false);
        MapCellFactory.getCellType(CellState.FULL_FLOOR, null, true);
    }

    // Test the initialization of a Map instance
    @Test
    public void testMapInitialization() {
        Map map = new Map(10, 10);

        // Verify initialization
        assertNotNull(map, "Map should be initialized.");
    }

    // Test the cellular automata generation of Map instance
    @Test
    public void testMapGenerationCA() throws Exception {
        // Create a generic map
        Map map = new Map(100, 100);
        map.generateMapCA(15, 5, 3);
        MapCell[][] mapData = map.getMap();

        // Map generation is not null
        assertNotNull(mapData);

        // Map is created with the desired sizes
        assertEquals(100, mapData.length);
        assertEquals(100, mapData[0].length);

        boolean blankFound = false;
        boolean tileFound = false;

        for (MapCell[] row : mapData) {
            for (MapCell cell : row) {
                if (cell.getState().getCellState() == CellState.BLANK) {
                    blankFound = true;
                } else if (cell.getState().getCellState() == CellState.TILE) {
                    tileFound = true;
                }
                if (blankFound && tileFound) {
                    break;
                }
            }
            if (blankFound && tileFound) {
                break;
            }
        }

        // They're can be found at least one blank & tile cell within map instance
        assertTrue(tileFound);
        assertTrue(blankFound);
    }

    // Test the cellular automata generation of Map instances with differing sizes
    @Test
    public void testMapSizeCA() throws Exception {
        // Populates item factory for decorate regions method
        ItemFactory.getItem("chest", null,
                10, 10, 5, 5, false, true, null, null);
        ItemFactory.getItem("torch", null,
                10, 10, 5, 5, true, false, null, null);
        ItemFactory.getItem("box", null,
                10, 10, 5, 5, true, false, null, null);
        ItemFactory.getItem("skull", null,
                2, 2, 5, 5, true, false, null, null);

        Map map1 = new Map(0, 0);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map1.generateMapCA(15, 5, 3);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map1.fillRegions(30);
            map1.connectRegions();
            map1.setWalls();
            map1.decorateRegions(20, 20, 20);
        });

        Map map2 = new Map(100, 100);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map2.generateMapCA(15, 5, 3);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map2.fillRegions(30);
            map2.connectRegions();
            map2.setWalls();
            map2.decorateRegions(20, 20, 20);
        });

        Map map3 = new Map(1000, 1000);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map3.generateMapCA(15, 5, 3);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map3.fillRegions(30);
            map3.connectRegions();
            map3.setWalls();
            map3.decorateRegions(20, 20, 20);
        });

        Map map4 = new Map(7, 48);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map4.generateMapCA(15, 5, 3);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map4.fillRegions(30);
            map4.connectRegions();
            map4.setWalls();
            map4.decorateRegions(20, 20, 20);
        });

        Map map5 = new Map(63, 13);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map5.generateMapCA(15, 5, 3);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map5.fillRegions(30);
            map5.connectRegions();
            map5.setWalls();
            map5.decorateRegions(20, 20, 20);
        });
    }

    // Test the Random Walk generation of Map instance
    @Test
    public void testMapGenerationRW() throws Exception {
        // Create a generic map
        Map map = new Map(100, 100);
        map.generateMapRW(500);
        MapCell[][] mapData = map.getMap();

        // Map generation is not null
        assertNotNull(mapData);

        // Map is created with the desired sizes
        assertEquals(100, mapData.length);
        assertEquals(100, mapData[0].length);

        boolean blankFound = false;
        boolean tileFound = false;

        for (MapCell[] row : mapData) {
            for (MapCell cell : row) {
                if (cell.getState().getCellState() == CellState.BLANK) {
                    blankFound = true;
                } else if (cell.getState().getCellState() == CellState.TILE) {
                    tileFound = true;
                }
                if (blankFound && tileFound) {
                    break;
                }
            }
            if (blankFound && tileFound) {
                break;
            }
        }

        // They're can be found at least one blank & tile cell within map instance
        assertTrue(tileFound);
        assertTrue(blankFound);
    }

    // Test the Random walk generation of Map instances with differing sizes
    @Test
    public void testMapSizeRW() throws Exception {
        // Populates item factory for decorate regions method
        ItemFactory.getItem("chest", null,
                10, 10, 5, 5, false, true, null, null);
        ItemFactory.getItem("torch", null,
                10, 10, 5, 5, true, false, null, null);
        ItemFactory.getItem("box", null,
                10, 10, 5, 5, true, false, null, null);
        ItemFactory.getItem("skull", null,
                2, 2, 5, 5, true, false, null, null);

        Map map1 = new Map(0, 0);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map1.generateMapRW(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map1.fillRegions(30);
            map1.connectRegions();
            map1.setWalls();
            map1.decorateRegions(20, 20, 20);
        });

        Map map2 = new Map(100, 100);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map2.generateMapRW(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map2.fillRegions(30);
            map2.connectRegions();
            map2.setWalls();
            map2.decorateRegions(20, 20, 20);
        });

        Map map3 = new Map(1000, 1000);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map3.generateMapRW(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map3.fillRegions(30);
            map3.connectRegions();
            map3.setWalls();
            map3.decorateRegions(20, 20, 20);
        });

        Map map4 = new Map(7, 48);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map4.generateMapRW(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map4.fillRegions(30);
            map4.connectRegions();
            map4.setWalls();
            map4.decorateRegions(20, 20, 20);
        });

        Map map5 = new Map(63, 13);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map5.generateMapRW(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map5.fillRegions(30);
            map5.connectRegions();
            map5.setWalls();
            map5.decorateRegions(20, 20, 20);
        });
    }

    // Test the BSP generation of Map instance
    @Test
    public void testMapGenerationBSP() throws Exception {
        // Create a generic map
        Map map = new Map(100, 100);
        map.generateMapBSP(100);
        MapCell[][] mapData = map.getMap();

        // Map generation is not null
        assertNotNull(mapData);

        // Map is created with the desired sizes
        assertEquals(100, mapData.length);
        assertEquals(100, mapData[0].length);

        boolean blankFound = false;
        boolean tileFound = false;

        for (MapCell[] row : mapData) {
            for (MapCell cell : row) {
                if (cell.getState().getCellState() == CellState.BLANK) {
                    blankFound = true;
                } else if (cell.getState().getCellState() == CellState.TILE) {
                    tileFound = true;
                }
                if (blankFound && tileFound) {
                    break;
                }
            }
            if (blankFound && tileFound) {
                break;
            }
        }

        // They're can be found at least one blank & tile cell within map instance
        assertTrue(tileFound);
        assertTrue(blankFound);
    }

    // further test the BSP generation of Map instances with differing sizes
    @Test
    public void testMapGenerationRandomPlacement() throws Exception {
        Map map1 = new Map(100, 100);
        map1.generateMapBSP(100);
        MapCell[][] mapData1 = map1.getMap();
        Map map2 = new Map(100, 100);
        map2.generateMapBSP(100);
        MapCell[][] mapData2 = map2.getMap();
        assertNotNull(mapData1);
        assertNotNull(mapData2);
        assertEquals(100, mapData1.length);
        assertEquals(100, mapData1[0].length);
        assertEquals(100, mapData2.length);
        assertEquals(100, mapData2[0].length);
        boolean mapsAreDifferent = false;
        for (int i = 0; i < mapData1.length; i++) {
            for (int j = 0; j < mapData1[i].length; j++) {
                if (mapData1[i][j].getState().getCellState() != mapData2[i][j].getState().getCellState()) {
                    mapsAreDifferent = true;
                    break;
                }
            }
            if (mapsAreDifferent) {
                break;
            }
        }
        assertTrue(mapsAreDifferent);
    }

    // further test the BSP generation of Map instances with differing sizes
    @Test
    public void testMapGenerationEdgeCaseMinimumSize() throws Exception {
        // create a generic map with the smallest size (1x1)
        Map map = new Map(1, 1);
        map.generateMapBSP(100);
        MapCell[][] mapData = map.getMap();
        // map generation is not null
        assertNotNull(mapData);
        // ensure the map is created with size 1x1
        assertEquals(1, mapData.length);
        assertEquals(1, mapData[0].length);
        CellState cellState = mapData[0][0].getState().getCellState();
        assertTrue(cellState == CellState.BLANK || cellState == CellState.TILE);
        assertNotNull(cellState);
    }

    // Test the BSP generation split iterations, to ensure that system failure
    // cannot occur from too many/few splits
    @Test
    public void testBSPIterations() throws Exception {
        // Create a generic map
        Map map = new Map(10, 10);

        map.generateMapBSP(0);
        MapCell[][] mapData = map.getMap();
        // Map generation is not null
        assertNotNull(mapData);

        map.generateMapBSP(1);
        mapData = map.getMap();
        // Map generation is not null
        assertNotNull(mapData);

        map.generateMapBSP(2);
        mapData = map.getMap();
        // Map generation is not null
        assertNotNull(mapData);

        map.generateMapBSP(5);
        mapData = map.getMap();
        // Map generation is not null
        assertNotNull(mapData);

        map.generateMapBSP(10);
        mapData = map.getMap();
        // Map generation is not null
        assertNotNull(mapData);

        map.generateMapBSP(100);
        mapData = map.getMap();
        // Map generation is not null
        assertNotNull(mapData);

        map.generateMapBSP(1000);
        mapData = map.getMap();
        // Map generation is not null
        assertNotNull(mapData);
    }

    // Test the Binary space partitioning generation of Map instances with differing
    // sizes
    @Test
    public void testMapSizeBSP() throws Exception {
        // Populates item factory for decorate regions method
        ItemFactory.getItem("chest", null,
                10, 10, 5, 5, false, true, null, null);
        ItemFactory.getItem("torch", null,
                10, 10, 5, 5, true, false, null, null);
        ItemFactory.getItem("box", null,
                10, 10, 5, 5, true, false, null, null);
        ItemFactory.getItem("skull", null,
                2, 2, 5, 5, true, false, null, null);

        Map map1 = new Map(0, 0);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map1.generateMapBSP(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map1.fillRegions(30);
            map1.connectRegions();
            map1.setWalls();
            map1.decorateRegions(20, 20, 20);
        });

        Map map2 = new Map(100, 100);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map2.generateMapBSP(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map2.fillRegions(30);
            map2.connectRegions();
            map2.setWalls();
            map2.decorateRegions(20, 20, 20);
        });

        Map map3 = new Map(1000, 1000);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map3.generateMapBSP(500); // BSP needs iterations to scale with map size, else system struggles to render
                                      // on standard hardware
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map3.fillRegions(30);
            map3.connectRegions();
            map3.setWalls();
            map3.decorateRegions(20, 20, 20);
        });

        Map map4 = new Map(7, 48);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map4.generateMapBSP(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map4.fillRegions(30);
            map4.connectRegions();
            map4.setWalls();
            map4.decorateRegions(20, 20, 20);
        });

        Map map5 = new Map(63, 13);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map5.generateMapBSP(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map5.fillRegions(30);
            map5.connectRegions();
            map5.setWalls();
            map5.decorateRegions(20, 20, 20);
        });
    }

    // Test the random boxes generation of Map instance
    @Test
    public void testMapGenerationRB() throws Exception {
        // Create a generic map
        Map map = new Map(100, 100);

        map.generateMapRB(20);
        MapCell[][] mapData = map.getMap();

        // Map generation is not null
        assertNotNull(mapData);

        // Map is created with the desired sizes
        assertEquals(100, mapData.length);
        assertEquals(100, mapData[0].length);

        boolean blankFound = false;
        boolean tileFound = false;

        for (MapCell[] row : mapData) {
            for (MapCell cell : row) {
                if (cell.getState().getCellState() == CellState.BLANK) {
                    blankFound = true;
                } else if (cell.getState().getCellState() == CellState.TILE) {
                    tileFound = true;
                }
                if (blankFound && tileFound) {
                    break;
                }
            }
            if (blankFound && tileFound) {
                break;
            }
        }

        // They're can be found at least one blank & tile cell within map instance
        assertTrue(tileFound);
        assertTrue(blankFound);
    }

    // Test the random boxes generation of Map instances with differing sizes
    @Test
    public void testMapSizeRB() throws Exception {
        // Populates item factory for decorate regions method
        ItemFactory.getItem("chest", null,
                10, 10, 5, 5, false, true, null, null);
        ItemFactory.getItem("torch", null,
                10, 10, 5, 5, true, false, null, null);
        ItemFactory.getItem("box", null,
                10, 10, 5, 5, true, false, null, null);
        ItemFactory.getItem("skull", null,
                2, 2, 5, 5, true, false, null, null);

        Map map1 = new Map(0, 0);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map1.generateMapRB(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map1.fillRegions(30);
            map1.connectRegions();
            map1.setWalls();
            map1.decorateRegions(20, 20, 20);
        });

        Map map2 = new Map(100, 100);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map2.generateMapRB(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map2.fillRegions(30);
            map2.connectRegions();
            map2.setWalls();
            map2.decorateRegions(20, 20, 20);
        });

        Map map3 = new Map(1000, 1000);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map3.generateMapRB(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map3.fillRegions(30);
            map3.connectRegions();
            map3.setWalls();
            map3.decorateRegions(20, 20, 20);
        });

        Map map4 = new Map(7, 48);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map4.generateMapRB(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map4.fillRegions(30);
            map4.connectRegions();
            map4.setWalls();
            map4.decorateRegions(20, 20, 20);
        });

        Map map5 = new Map(63, 13);

        // Map is generated without error
        assertDoesNotThrow(() -> {
            map5.generateMapRB(15);
        });

        // Map functions are called without error
        assertDoesNotThrow(() -> {
            map5.fillRegions(30);
            map5.connectRegions();
            map5.setWalls();
            map5.decorateRegions(20, 20, 20);
        });
    }

    // Test the count clear neighbours method
    @Test
    public void testCountClearNeighbours() throws Exception {
        // Create map of size 10x10
        Map map = new Map(10, 10);
        MapCell[][] testMapData = new MapCell[10][10];

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {

                testMapData[x][y] = new MapCell(MapCellFactory.getCellType(CellState.BLANK), x, y);
            }
        }

        // Set traversable tiles
        testMapData[5][5].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[5][6].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[4][5].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[6][5].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[5][4].setState(MapCellFactory.getCellType(CellState.TILE));

        // Use reflection to set the map data
        Field mapField = Map.class.getDeclaredField("map");
        mapField.setAccessible(true);
        mapField.set(map, testMapData);

        // Access private method countClearNeighbours
        Method countClearNeighboursMethod = Map.class.getDeclaredMethod("countClearNeighbours", int.class, int.class);
        countClearNeighboursMethod.setAccessible(true);
        int neighborCount = (int) countClearNeighboursMethod.invoke(map, 5, 5);

        // Cell should have 4 clear neighbors
        assertEquals(4, neighborCount);
    }

    // Test the ruleSet method
    @Test
    public void testRuleSet() throws Exception {
        // Create map of size 10x10
        Map map = new Map(10, 10);

        // Access private method ruleSet
        Method ruleSetMethod = Map.class.getDeclaredMethod("ruleSet", int.class, MapCell.class, int.class, int.class);
        ruleSetMethod.setAccessible(true);

        // Create test tile cell
        MapCell tileCell = new MapCell(MapCellFactory.getCellType(CellState.TILE), 0, 0);

        // Invoke method with count that passes the survival rate;
        CellState cellState = (CellState) ruleSetMethod.invoke(map, 4, tileCell, 3, 3);

        // State should remain unchanged
        assertEquals(CellState.TILE, cellState);

        // Invoke method with count that fails the survival rate
        cellState = (CellState) ruleSetMethod.invoke(map, 0, tileCell, 3, 3);

        // State should change to blank
        assertEquals(CellState.BLANK, cellState);

        // Create test blank cell
        MapCell blankCell = new MapCell(MapCellFactory.getCellType(CellState.BLANK), 0, 0);

        // Invoke method with count that fails the birth rate
        cellState = (CellState) ruleSetMethod.invoke(map, 0, blankCell, 3, 3);

        // State should remain unchanged
        assertEquals(CellState.BLANK, cellState);

        // Invoke method with count that passes the birth rate
        cellState = (CellState) ruleSetMethod.invoke(map, 8, blankCell, 3, 3);

        // State should change to tile
        assertEquals(CellState.TILE, cellState);
    }

    // Test the withinMapBoundary method
    @Test
    public void testWithinMapBoundary() throws Exception {
        // Create map of size 10x10
        Map map = new Map(10, 10);

        // Access method
        Method withinMapBoundaryMethod = Map.class.getDeclaredMethod("withinMapBoundary", int.class, int.class);
        withinMapBoundaryMethod.setAccessible(true);

        // Valid map position
        Boolean inBoundary = (Boolean) withinMapBoundaryMethod.invoke(map, 0, 0);

        assertTrue(inBoundary);

        // Invalid map position
        inBoundary = (Boolean) withinMapBoundaryMethod.invoke(map, -1, 0);

        assertFalse(inBoundary);
    }

    // test the floodFill method
    @Test
    public void testFloodFillLogic() throws Exception {
        Map map = new Map(10, 10);

        // Create a test map with known regions
        MapCell[][] testMapData = new MapCell[10][10];
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                testMapData[x][y] = new MapCell(MapCellFactory.getCellType(CellState.BLANK), x, y);
            }
        }
        // Define a small connected region
        testMapData[5][5].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[5][6].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[6][6].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[6][5].setState(MapCellFactory.getCellType(CellState.TILE));

        // Use reflection to set the map
        var mapField = Map.class.getDeclaredField("map");
        mapField.setAccessible(true);
        mapField.set(map, testMapData);

        // Declare region in map
        Method getRegionsMethod = Map.class.getDeclaredMethod("getRegions");
        getRegionsMethod.setAccessible(true);

        var mapRegions = Map.class.getDeclaredField("regions");
        mapRegions.setAccessible(true);
        mapRegions.set(map, getRegionsMethod.invoke(map));

        // Fill region as it lack the required cells
        map.fillRegions(10);

        // Count the number of traversable cells in map
        int count = 0;
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (testMapData[x][y].isFloor())
                    count++;
            }
        }

        // Ensure region has been filled
        assertEquals(0, count);
    }

    // test the connectivity of the map
    @Test
    public void testRegionDiscover() throws Exception {
        Map map = new Map(10, 10);

        // Create a test map
        MapCell[][] testMapData = new MapCell[10][10];
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                testMapData[x][y] = new MapCell(MapCellFactory.getCellType(CellState.BLANK), x, y);
            }
        }
        // Create a single connected region
        testMapData[4][4].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[4][5].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[5][4].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[5][5].setState(MapCellFactory.getCellType(CellState.TILE));

        // Use reflection to set the map
        var mapField = Map.class.getDeclaredField("map");
        mapField.setAccessible(true);
        mapField.set(map, testMapData);

        // Declare region in map
        Method getRegionsMethod = Map.class.getDeclaredMethod("getRegions");
        getRegionsMethod.setAccessible(true);

        var mapRegions = Map.class.getDeclaredField("regions");
        mapRegions.setAccessible(true);
        mapRegions.set(map, getRegionsMethod.invoke(map));

        LinkedList<Region> regions = (LinkedList<Region>) mapRegions.get(map);

        assertEquals(1, regions.size());
    }

    // test the tunneling between regions
    @Test
    public void testTunnelingBetweenRegions() throws Exception {
        Map map = new Map(10, 10);
        // Create a test map with two disconnected regions
        MapCell[][] testMapData = new MapCell[10][10];
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                testMapData[x][y] = new MapCell(MapCellFactory.getCellType(CellState.BLANK), x, y);
            }
        }
        // Define two disconnected regions
        testMapData[3][3].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[3][4].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[3][5].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[7][7].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[7][8].setState(MapCellFactory.getCellType(CellState.TILE));
        testMapData[7][9].setState(MapCellFactory.getCellType(CellState.TILE));

        // Use reflection to set the map
        Field mapField = Map.class.getDeclaredField("map");
        mapField.setAccessible(true);
        mapField.set(map, testMapData);

        Method getRegionsMethod = Map.class.getDeclaredMethod("getRegions");
        getRegionsMethod.setAccessible(true);

        Field regionField = Map.class.getDeclaredField("regions");
        regionField.setAccessible(true);
        regionField.set(map, getRegionsMethod.invoke(map));

        map.connectRegions();

        // Verify that the two regions are now connected
        int connectedRegionSize = countRegionSizeHelper(testMapData, 3, 3, CellState.TILE);

        // the two regions should now be connected
        assertTrue(connectedRegionSize > 6);
    }

    /*
     * -----------------------------------------------------------------------------
     * ------
     * Helper Methods to access private methods and fields in the Map class
     * -----------------------------------------------------------------------------
     * ------
     */

    private int countRegionSizeHelper(MapCell[][] map, int startX, int startY, CellState cellType) {
        boolean[][] visited = new boolean[map.length][map[0].length];
        return floodFill(map, visited, startX, startY, cellType);
    }

    private int floodFill(MapCell[][] map, boolean[][] visited, int x, int y, CellState cellType) {
        if (x < 0 || x >= map.length || y < 0 || y >= map[0].length || visited[x][y] ||
                (map[x][y].getState() != null && map[x][y].getState().getCellState() != cellType)) {
            return 0;
        }

        visited[x][y] = true;
        int size = 1;

        size += floodFill(map, visited, x + 1, y, cellType);
        size += floodFill(map, visited, x - 1, y, cellType);
        size += floodFill(map, visited, x, y + 1, cellType);
        size += floodFill(map, visited, x, y - 1, cellType);

        return size;
    }
}