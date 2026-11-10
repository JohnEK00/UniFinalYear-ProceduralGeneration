package com.PGD;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

/*
* Enum representing the directions the Random walk algorithm may travel within the grid.
*/
enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

/*
 * Map class is responsible for housing the multiple functions for generating an
 * enum 2D array that will act as a player space,
 * along with performing additional operations such as the flood fill algorithm
 * and tunneling algorithm to greater expand the range of generations possible.
 * 
 * References:
 * https://blog.jrheard.com/procedural-dungeon-generation-cellular-automata,
 * https://natureofcode.com/cellular-automata/
 * https://medium.com/@koray.kara98.kk/understanding-and-implementing-flood-fill
 * -algorithm-60ab81538d54
 * https://www.gridsagegames.com/blog/2014/06/mapgen-cellular-automata/
 * 
 */
public class Map {
    private final int height;
    private final int width;
    private final char[][] mapFlags;
    private LinkedList<Region> regions;
    private MapCell[][] map;
    private final WallCell[][] wallCells;

    private static final int TILE_WIDTH = 16;
    private static final int TILE_HEIGHT = 16;
    private static final int NUM_OF_DIRECTIONS = 4; // Number of available directions the Random Walk algorithm may move

    private final HashMap<Point, Item> itemMap;
    private final ArrayList<Point> enemyLocations;
    private final ArrayList<Item> items;

    /**
     * Create map instance
     * 
     * @param height - Number of rows for intended map
     * @param width  - Number of columns for intended map
     */
    public Map(int height, int width) {
        this.height = height;
        this.width = width;

        itemMap = new HashMap<>();
        enemyLocations = new ArrayList<>();
        items = new ArrayList<>();
        regions = new LinkedList<>();
        map = new MapCell[height][width];

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                map[x][y] = new MapCell(MapCellFactory.getCellType(CellState.BLANK), x, y);
            }
        }

        mapFlags = new char[height][width];
        wallCells = new WallCell[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                wallCells[i][j] = new WallCell(null, i, j);
            }
        }
    }

    // creates the tiledMap
    /**
     * Transforms the 2D array of the map into a LibGDX tileMap for rendering
     * 
     * @param collider - Check interactions with map collison
     * @return TileMap of 2D array map ready for rendering in main
     */
    public TiledMap drawMap(Collider collider) {

        TiledMap tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();

        // each tiled map contains layers
        TiledMapTileLayer mainLayer = new TiledMapTileLayer(width, height, TILE_WIDTH, TILE_HEIGHT);

        // Required to place images over another. E.g. chests, props etc.
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(width, height, TILE_WIDTH, TILE_HEIGHT);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                TiledMapTileLayer.Cell wallCell = new TiledMapTileLayer.Cell();

                TextureRegion region = MapCellFactory.getCellType(map[i][j].getState().getCellState())
                        .getTextureRegion();

                cell.setTile(new StaticTiledMapTile(region));

                mainLayer.setCell(i, j, cell);

                if (wallCells[i][j].getCellType() != null && wallCells[i][j].getType() > -1) {
                    TextureRegion wallTexture = WallCellFactory.getWallType(wallCells[i][j].getType())
                            .getTextureRegion();
                    StaticTiledMapTile wallTile = new StaticTiledMapTile(wallTexture);
                    wallCell.setTile(wallTile);
                    wallLayer.setCell(i, j, wallCell);
                }
            }
        }

        // Add items to the map
        for (Item item : itemMap.values()) {
            collider.addInteractableItem(item);
        }

        // Add enemies to the map
        layers.add(mainLayer);
        collider.updateWallColliders(wallCells, TILE_WIDTH, TILE_HEIGHT);
        layers.add(wallLayer);

        return tiledMap;
    }

    /**
     * Populates a map following the cellurlar automata algorithm.
     * 
     * @param iterations   - Number of cycles the alogrithm will proccess the map
     *                     before its finished.
     * @param birthRate    - Number of living neighbours needed for a blank cell to
     *                     turn into a tile.
     * @param survivalRate - Number of living neighbours needed for a tile cell to
     *                     stay a tile.
     */
    public void generateMapCA(int iterations, int birthRate, int survivalRate) {

        Random random = new Random();
        for (int x = 0; x < height; x++) {

            for (int y = 0; y < width; y++) {

                if (random.nextDouble(1.01) > 0.48)
                    map[x][y].setState(MapCellFactory.getCellType(CellState.TILE));
            }
        }

        MapCell[][] newMap = map.clone();
        while (iterations > 0) {

            for (int x = 0; x < height; x++) {

                for (int y = 0; y < width; y++) {

                    int count = countClearNeighbours(x, y);
                    newMap[x][y]
                            .setState(MapCellFactory.getCellType(ruleSet(count, map[x][y], birthRate, survivalRate)));
                }
            }
            map = newMap;
            iterations--;
        }

        regions = getRegions();
    }

    /**
     * Populates a map following the Random walk (aka "Drunkard walk") algorithm.
     * 
     * @param iterations - Number of cycles the alogrithm will proccess the map
     *                   before its finished.
     */
    public void generateMapRW(int iterations) {
        if (height <= 0 || width <= 0) {
            return;
        }

        Random random = new Random();

        int x = random.nextInt(height);
        int y = random.nextInt(width);

        while (iterations > 0) {
            Direction path = Direction.values()[random.nextInt(NUM_OF_DIRECTIONS)];

            switch (path) {
                case UP:
                    y++;
                    break;
                case DOWN:
                    y--;
                    break;
                case RIGHT:
                    x++;
                    break;
                case LEFT:
                    x--;
            }

            if (x > 0 && x < height - 1 && y > 0 && y < width - 1)
                map[x][y].setState(MapCellFactory.getCellType(CellState.TILE));
            else {
                x = random.nextInt(height);
                y = random.nextInt(width);
            }

            iterations--;
        }

        regions = getRegions();
    }

    /**
     * Populates a map following the Binary space partitioning algorithm.
     * 
     * @param iterations - Number of cycles the alogrithm will proccess the map
     *                   before its finished.
     */
    public void generateMapBSP(int iterations) {

        MapCell[][] result = splitMap(iterations, map);

        for (int x = 0; x < result.length; x++) {

            for (int y = 0; y < result[x].length; y++) {
                if (result[x][y] != null && result[x][y].getState().getCellState() == CellState.TILE) {
                    map[x][y].setState(MapCellFactory.getCellType(CellState.TILE));
                }
            }
        }

        regions = getRegions();
    }

    /**
     * Generates a map following the random boxes algorithm
     * 
     * @param iterations - Number of cycles the alogrithm will proccess the map
     *                   before its finished.
     */
    public void generateMapRB(int iterations) {
        Random rand = new Random();
        int posX, posY, boxHeight, boxWidth;

        while (iterations > 0 && height > 0) {

            posX = rand.nextInt(1, height - 1);
            posY = rand.nextInt(1, width - 1);

            boxHeight = rand.nextInt(0, ((int) (height * 0.25))); // These limits prevent a single box from covering the
                                                                  // entire map at once
            boxWidth = rand.nextInt(0, ((int) (width * 0.25)));

            for (int x = 0; x < boxHeight; x++) {
                for (int y = 0; y < boxWidth; y++) {
                    if (withinMapBoundary(x + posX, y + posY) && x + posX > 0 && y + posY > 0 && x + posX < height - 1
                            && y + posY < width - 1)
                        map[x + posX][y + posY].setState(MapCellFactory.getCellType(CellState.TILE));
                }
            }

            iterations--;
        }

        regions = getRegions();
    }

    /**
     * Splits a map for the BSP algorithm.
     * 
     * @param iterations - Number of cycles the alogrithm will proccess the map
     *                   before its finished.
     * @param splitMap   - A portion of the main map after a split, used for
     *                   recursive function calls.
     * @return Array combined with past iterations. E.g., Map = split(n - 1 + n - 2
     *         + ...)
     */
    private MapCell[][] splitMap(int iterations, MapCell[][] splitMap) {
        // Limiting array size to 7 and above means that user can have an impossible
        // large number of devision without crashing system, and still generating a
        // result
        if (iterations > 0 && splitMap.length > 6 && splitMap[0].length > 6) {
            Random rand = new Random();

            int startLimitX = (int) (splitMap.length * 0.25); // These ensure the split occurs around the
                                                              // middle of the array, rather then the start
                                                              // or end
            int endLimitX = splitMap.length - startLimitX;

            int x = rand.nextInt(startLimitX, endLimitX);

            int startLimitY = (int) (splitMap[x].length * 0.25);
            int endLimitY = splitMap[x].length - startLimitY;

            int y = rand.nextInt(startLimitY, endLimitY);

            if (rand.nextBoolean()) { // Determines if a horizontal or vertical split will take place
                MapCell[][] first = new MapCell[x][splitMap[0].length];

                MapCell[][] second = new MapCell[splitMap.length - x][splitMap[0].length];
                return copyArraysVertical(splitMap(iterations - 1, first), splitMap(iterations - 1, second));
            }

            else {
                MapCell[][] first = new MapCell[splitMap.length][y];
                MapCell[][] second = new MapCell[splitMap.length][splitMap[0].length - y];

                return copyArraysHorizontal(splitMap(iterations - 1, first), splitMap(iterations - 1, second));
            }
        } else {

            return setBox(splitMap, 2);
        }
    }

    /**
     * Will set a box in tiles, with a specificed border of blank tiles
     * Used in BSP generation
     * 
     * @param mapSection - Section of the main map that a box of tiles will be
     *                   drawn.
     * @param border     - Width of the blank cells drawn around the box of tiles,
     *                   done so that there is a bit of distance between two
     *                   different splits.
     * @return Populated array of tiles in a box shape.
     */
    private MapCell[][] setBox(MapCell[][] mapSection, int border) {
        for (int x = 0; x < mapSection.length; x++) {
            for (int y = 0; y < mapSection[x].length; y++) {
                if (x < border || x >= mapSection.length - border || y < border || y >= mapSection[x].length - border) {
                    mapSection[x][y] = new MapCell(MapCellFactory.getCellType(CellState.BLANK), x, y);
                } else {
                    mapSection[x][y] = new MapCell(MapCellFactory.getCellType(CellState.TILE), x, y);
                }
            }
        }
        return mapSection;
    }

    /**
     * Combines two 2D MapCells together for use with BSP generation vertically
     * after horizontal split.
     * 
     * @param first  - First array to be jointed onto the second.
     * @param second - second array to be jointed onto the first.
     * @return Combined array.
     */
    private MapCell[][] copyArraysVertical(MapCell[][] first, MapCell[][] second) {
        MapCell[][] combined = new MapCell[first.length + second.length][first[0].length];

        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }

    /**
     * Combines two 2D MapCells together for use with BSP generation horizontally
     * after vertical split.
     * 
     * @param first  - First array to be jointed onto the second.
     * @param second - second array to be jointed onto the first.
     * @return Combined array.
     */
    private MapCell[][] copyArraysHorizontal(MapCell[][] first, MapCell[][] second) {
        MapCell[][] combined = new MapCell[first.length][first[0].length + second[0].length];

        for (int x = 0; x < first.length; x++) {
            System.arraycopy(first[x], 0, combined[x], 0, first[x].length);
            System.arraycopy(second[x], 0, combined[x], first[x].length, second[x].length);
        }

        return combined;
    }

    /**
     * Count the number of traversable cells around a given cell in the map
     * 
     * @param row    - X position of cell in map.
     * @param column - Y position of cell in map.
     * @return Number of tiles surrounding cell.
     */
    private int countClearNeighbours(int row, int column) {
        int count = 0;

        // Removes the state of the cell inself from calculation
        if (map[row][column].getState().getCellState() == CellState.TILE)
            count--;

        for (int x = row - 1; x <= row + 1; x++) {

            for (int y = column - 1; y <= column + 1; y++) {

                if (!withinMapBoundary(x, y))
                    count--;
                else if (map[x][y].getState() != null && map[x][y].getState().getCellState() == CellState.TILE)
                    count++;
            }
        }

        return count;
    }

    /**
     * Based on the number of traversable cells around cell, set its state.
     * 
     * @param count        - Number of tiles around cell that have tile state
     * @param cell         - Cell the function will be transforming
     * @param birthRate    - Number of living neighbours needed for a blank cell to
     *                     turn into a tile.
     * @param survivalRate - Number of living neighbours needed for a tile cell to
     *                     stay a tile.
     * @return State of cell after ruleset
     */
    private CellState ruleSet(int count, MapCell cell, int birthRate, int survivalRate) {
        if (cell.getState() != null && cell.getState().getCellState() == CellState.TILE && count > survivalRate)
            return CellState.TILE;
        else if (cell.getState() != null && cell.getState().getCellState() == CellState.BLANK && count > birthRate)
            return CellState.TILE;
        else
            return CellState.BLANK;
    }

    /**
     * Check that a coordinate is within the map.
     * 
     * @param x - X position of cell in map.
     * @param y - Y position of cell in map.
     * 
     * @return True within bounds, false otherwise.
     */
    private boolean withinMapBoundary(int x, int y) {
        return x >= 0 && x < height && y >= 0 && y < width;
    }

    // ----------------------------------------------------------------------------------------------------------------
    /*
     * Following functions mainly perform floodfill algorithm to remove regions that
     * are too small for use in map.
     */

    /*
     * Return a list of all cells that form a region.
     */
    private Region findRegionCells(int startX, int startY) {

        LinkedList<MapCell> cells = new LinkedList<>();

        CellState cellType = map[startX][startY].getState().getCellState();
        MapCellType mapCellType = MapCellFactory.getCellType(cellType);

        Queue<MapCell> queue = new LinkedList<>();
        queue.add(new MapCell(mapCellType, startX, startY));

        mapFlags[startX][startY] = 'X';

        while (!queue.isEmpty()) {

            MapCell cell = queue.poll();
            cells.add(cell);

            int cellX = cell.getX();
            int cellY = cell.getY();

            for (int x = cellX - 1; x <= cellX + 1; x++) {

                for (int y = cellY - 1; y <= cellY + 1; y++) {

                    if (withinMapBoundary(x, y)
                            && (mapFlags[x][y] != 'X' && map[x][y].getState().getCellState() == cellType)) {

                        if (x == cellX || y == cellY) {

                            mapFlags[x][y] = 'X';
                            queue.add(new MapCell(mapCellType, x, y));
                        }
                    }
                }
            }
        }

        return new Region(cells);
    }

    /*
     * Return a list of all the regions within the map.
     */
    private LinkedList<Region> getRegions() {
        LinkedList<Region> foundRegions = new LinkedList<>();

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                if (mapFlags[x][y] != 'X' && map[x][y].isFloor()) {

                    Region newRegion = findRegionCells(x, y);
                    newRegion.setEdgeCells(map);
                    foundRegions.add(newRegion);
                }
            }
        }
        return foundRegions;
    }

    /*
     * Fill all the regions that have less cells than the threshold states is
     * required.
     *
     * set to public for testing purposes
     */
    public void fillRegions(int threshold) {
        LinkedList<Region> remainingRooms = new LinkedList<>();

        for (Region region : regions) {

            if (region.getRegionSize() < threshold) {

                for (MapCell cell : region.getRegionCells()) {

                    map[cell.getX()][cell.getY()].setState(MapCellFactory.getCellType(CellState.BLANK));
                }
            } else {

                remainingRooms.add(region);
            }

        }

        if (!remainingRooms.isEmpty()) {

            remainingRooms.sort((a, b) -> {
                return -1 * a.compareTo(b);
            });

            remainingRooms.getFirst().setMainRegionStatus(true);
            remainingRooms.getFirst().setMainRegionAccess(true);

            regions = remainingRooms;
        } else {
            regions.clear();
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    /*
     * Following functions mainly perform tunneling algorithm to connect distant
     * regions together.
     */

    /*
     * Finds the best regions to link to each other
     */
    public void connectRegions() {
        if (regions.size() < 2) { // Needs to be at least two regions for connections to be established
            return;
        }

        Random rand = new Random();
        LinkedList<Region> regionsA = regions;
        LinkedList<Region> regionsB = regions;

        double bestDistance = 0;

        MapCell edgeCellA = new MapCell(MapCellFactory.getCellType(CellState.TILE), 0, 0);
        MapCell edgeCellB = new MapCell(MapCellFactory.getCellType(CellState.TILE), 0, 0);

        Region targetRegionA = new Region(new LinkedList<>());
        Region targetRegionB = new Region(new LinkedList<>());

        boolean possibleConnection;

        for (Region regionA : regionsA) {

            possibleConnection = false;

            if (!regionA.getConnectedRegions().isEmpty())
                continue;

            LinkedList<MapCell> cellsA = regionA.getRegionEdgeCells();
            MapCell cellA = cellsA.get(rand.nextInt(cellsA.size()));

            for (Region regionB : regionsB) {
                if (regionA == regionB || regionA.isConnected(regionB))
                    continue;

                LinkedList<MapCell> cellsB = regionB.getRegionEdgeCells();
                MapCell cellB = cellsB.get(rand.nextInt(cellsB.size()));

                double distantBetweenRegions = Math.sqrt(
                        Math.pow(cellA.getX() - cellB.getX(), 2) + Math.pow(cellA.getY() - cellB.getY(), 2));

                if (distantBetweenRegions < bestDistance || !possibleConnection) {
                    bestDistance = distantBetweenRegions;
                    possibleConnection = true;

                    edgeCellA = cellA;
                    edgeCellB = cellB;

                    targetRegionA = regionA;
                    targetRegionB = regionB;
                }
            }

            if (possibleConnection) {

                createPassage(targetRegionA, targetRegionB, edgeCellA, edgeCellB);
            }
        }

        LinkedList<Region> nonMainConnected = new LinkedList<>();

        for (Region region : regions) {
            if (!region.checkMainRegionAccess())
                nonMainConnected.add(region);
        }

        Queue<Region> queue = new LinkedList<>();
        queue.add(regions.getFirst());

        while (!queue.isEmpty()) {
            Region mainConnected = queue.poll();

            possibleConnection = false;

            LinkedList<MapCell> cellsA = mainConnected.getRegionEdgeCells();
            MapCell cellA = cellsA.get(rand.nextInt(cellsA.size()));

            for (Region region : nonMainConnected) {
                if (mainConnected == region || mainConnected.isConnected(region) || region.checkMainRegionAccess())
                    continue;

                LinkedList<MapCell> cellsB = region.getRegionEdgeCells();
                MapCell cellB = cellsB.get(rand.nextInt(cellsB.size()));

                double distantBetweenRegions = Math.sqrt(
                        Math.pow(cellA.getX() - cellB.getX(), 2) + Math.pow(cellA.getY() - cellB.getY(), 2));

                if (distantBetweenRegions < bestDistance || !possibleConnection) {
                    bestDistance = distantBetweenRegions;
                    possibleConnection = true;

                    edgeCellA = cellA;
                    edgeCellB = cellB;

                    targetRegionA = mainConnected;
                    targetRegionB = region;
                }
            }

            if (possibleConnection) {
                queue.add(targetRegionB);
                createPassage(targetRegionA, targetRegionB, edgeCellA, edgeCellB);
            }
        }
    }

    /*
     * Sets the regions as connected to each other before begining passage creation.
     */
    private void createPassage(Region regionA, Region regionB, MapCell cellA, MapCell cellB) {
        regionA.joinRegions(regionB);

        int x1 = cellA.getX();
        int y1 = cellA.getY();
        int x2 = cellB.getX();
        int y2 = cellB.getY();

        int newXValue = x1;

        Boolean movingLeft = false; // Checks are needed when x1 > x2 as the passages shift away from each other in
                                    // this case.

        if (x2 > x1) {
            for (int index = x1; index <= x2; index++) {
                if (withinMapBoundary(index, y1 + 2) && withinMapBoundary(index, y1 + 1)) // Boundary checks for
                                                                                          // intended space + 1 is to
                                                                                          // ensure a wall can always
                                                                                          // surrround the passage at
                                                                                          // the time of rendering
                    map[index][y1 + 1].setState(MapCellFactory.getCellType(CellState.TILE));

                if (withinMapBoundary(index, y1 + 1))
                    map[index][y1].setState(MapCellFactory.getCellType(CellState.TILE));

                newXValue = index;
            }

        } else {
            movingLeft = true;
            for (int index = x1; index >= x2; index--) {
                if (withinMapBoundary(index, y1 + 2) && withinMapBoundary(index, y1 + 1))
                    map[index][y1 + 1].setState(MapCellFactory.getCellType(CellState.TILE));

                if (withinMapBoundary(index, y1 + 1))
                    map[index][y1].setState(MapCellFactory.getCellType(CellState.TILE));

                newXValue = index;
            }

        }

        if (y2 > y1) {

            for (int index = y1; index <= y2; index++) {

                if (movingLeft) {
                    if (withinMapBoundary(newXValue + 1, index))
                        map[newXValue][index].setState(MapCellFactory.getCellType(CellState.TILE));

                    if (withinMapBoundary(newXValue + 2, index) && withinMapBoundary(newXValue + 1, index))
                        map[newXValue + 1][index].setState(MapCellFactory.getCellType(CellState.TILE));

                } else {
                    if (withinMapBoundary(newXValue - 1, index))
                        map[newXValue][index].setState(MapCellFactory.getCellType(CellState.TILE));

                    if (withinMapBoundary(newXValue - 2, index) && withinMapBoundary(newXValue - 1, index))
                        map[newXValue - 1][index].setState(MapCellFactory.getCellType(CellState.TILE));
                }

            }

        } else {
            for (int index = y1 + 1; index >= y2; index--) {
                if (movingLeft) {
                    if (withinMapBoundary(newXValue + 1, index))
                        map[newXValue][index].setState(MapCellFactory.getCellType(CellState.TILE));

                    if (withinMapBoundary(newXValue + 2, index) && withinMapBoundary(newXValue + 1, index))
                        map[newXValue + 1][index].setState(MapCellFactory.getCellType(CellState.TILE));

                } else {
                    if (withinMapBoundary(newXValue - 1, index))
                        map[newXValue][index].setState(MapCellFactory.getCellType(CellState.TILE));

                    if (withinMapBoundary(newXValue - 2, index) && withinMapBoundary(newXValue - 1, index))
                        map[newXValue - 1][index].setState(MapCellFactory.getCellType(CellState.TILE));
                }
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    /*
     * Following functions mainly place elements such as walls, props, enemies etc.
     */

    /*
     *
     * floor tile key:
     * HALF_LEFT - Half floor tile facing left
     * HALF_RIGHT - Half floor tile facing right
     * FULL_FLOOR - Complete tile under wall, made seperate so this tile can be used
     * in if statements for wall creation
     *
     */
    public void setWalls() {
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                if (map[x][y].getState().getCellState() == CellState.BLANK) {

                    if (withinMapBoundary(x - 1, y) && map[x - 1][y].isFloor() ||
                            withinMapBoundary(x + 1, y) && map[x + 1][y].isFloor() ||
                            withinMapBoundary(x, y + 1) && map[x][y + 1].isFloor() ||
                            withinMapBoundary(x, y - 1) && map[x][y - 1].isFloor() ||
                            withinMapBoundary(x - 1, y - 1)
                                    && map[x - 1][y - 1].isFloor()
                            ||
                            withinMapBoundary(x + 1, y + 1)
                                    && map[x + 1][y + 1].isFloor()
                            ||
                            withinMapBoundary(x - 1, y + 1)
                                    && map[x - 1][y + 1].isFloor()
                            ||
                            withinMapBoundary(x + 1, y - 1)
                                    && map[x + 1][y - 1].isFloor()) {
                        wallCells[x][y].setType(WallCellFactory.getWallType(0));
                    }
                }
            }
        }

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {

                int wc;

                if (wallCells[x][y] != null && wallCells[x][y].getCellType() != null
                        && wallCells[x][y].getType() > -1) {
                    wc = wallCells[x][y].getType();
                } else {
                    continue;
                }

                if (withinMapBoundary(x, y + 1) && wallCells[x][y + 1].getCellType() != null) {
                    wc += 1;
                }
                if (withinMapBoundary(x - 1, y) && wallCells[x - 1][y].getCellType() != null) {
                    wc += 2;
                }

                if (withinMapBoundary(x + 1, y) && wallCells[x + 1][y].getCellType() != null) {
                    wc += 4;
                }
                if (withinMapBoundary(x, y - 1) && wallCells[x][y - 1].getCellType() != null) {
                    wc += 8;
                }

                switch (wc) {
                    case 1:
                        if (withinMapBoundary(x - 1, y) && map[x - 1][y].isFloor() && withinMapBoundary(x + 1, y)
                                && map[x + 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.FULL_FLOOR));
                        } else if (withinMapBoundary(x + 1, y) && map[x + 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.HALF_RIGHT));
                        } else if (withinMapBoundary(x - 1, y) && map[x - 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.HALF_LEFT));
                        }
                        break;

                    case 3:
                        if (withinMapBoundary(x + 1, y) && map[x + 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.HALF_RIGHT));
                        } else {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.BLANK));
                        }
                        break;

                    case 5:
                        if (withinMapBoundary(x - 1, y) && map[x - 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.HALF_LEFT));
                        }
                        break;

                    case 8:
                        map[x][y].setState(MapCellFactory.getCellType(CellState.FULL_FLOOR));
                        break;

                    case 9:
                        if (withinMapBoundary(x - 1, y) && map[x - 1][y].isFloor() && withinMapBoundary(x + 1, y)
                                && map[x + 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.FULL_FLOOR));
                        } else if (withinMapBoundary(x + 1, y) && map[x + 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.HALF_RIGHT));
                        } else if (withinMapBoundary(x - 1, y) && map[x - 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.HALF_LEFT));
                        }
                        break;

                    case 10:
                        if (withinMapBoundary(x + 1, y) && map[x + 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.HALF_RIGHT));
                        } else {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.BLANK));
                        }
                        break;

                    case 11:
                        if (withinMapBoundary(x + 1, y) && map[x + 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.HALF_RIGHT));
                        } else {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.BLANK));
                        }
                        break;

                    case 12:
                        if (withinMapBoundary(x - 1, y) && map[x - 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.HALF_LEFT));
                        }
                        break;

                    case 13:
                        if (withinMapBoundary(x - 1, y) && map[x - 1][y].isFloor()) {
                            map[x][y].setState(MapCellFactory.getCellType(CellState.HALF_LEFT));
                        }
                        break;
                }

                if (wc > -1)
                    wallCells[x][y].setType(WallCellFactory.getWallType(wc));

            }
        }
    }

    /*
     * Places different tiles around edges, places props around edges and chests in
     * the middle.
     * 
     * Place enemies in unoccupied spaces.
     * 
     * Kept seperate from decorate walls as attempting to do both causes wall
     * placements to behave errectic.
     */
    public void decorateRegions(int propDensity, int itemDensity, int enemyDensity) {

        Random rand = new Random();

        for (Region region : regions) {
            LinkedList<MapCell> edgeCells = region.getRegionEdgeCells();

            for (int index = 0; index < edgeCells.size(); index++) {
                int chance = rand.nextInt(propDensity + 6); // Plus 6 to cover all the different prop types implmented
                                                            // and prevent all spaces being filled with props

                int x = edgeCells.get(index).getX();
                int y = edgeCells.get(index).getY();

                if (withinMapBoundary(x - 1, y) && (wallCells[x - 1][y].getCellType() != null
                        || map[x - 1][y].getState().getCellState() == CellState.BLANK)) {
                    switch (chance) {
                        case 0:
                            map[x][y].setState(MapCellFactory.getCellType(CellState.TILE_LEFT));
                            break;
                        case 1:
                            itemMap.put(new Point(x, y),
                                    new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("torch")));
                            break;
                        case 2:
                            itemMap.put(new Point(x, y),
                                    new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("box")));
                            break;
                        case 3:
                            itemMap.put(new Point(x, y),
                                    new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("skull")));
                            break;
                        default:
                            break;
                    }
                } else if (withinMapBoundary(x + 1, y) && (wallCells[x + 1][y].getCellType() != null
                        || map[x + 1][y].getState().getCellState() == CellState.BLANK)) {
                    switch (chance) {
                        case 0:
                            map[x][y].setState(MapCellFactory.getCellType(CellState.TILE_RIGHT));
                            break;
                        case 1:
                            itemMap.put(new Point(x, y),
                                    new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("box")));
                            break;
                        case 2:
                            itemMap.put(new Point(x, y),
                                    new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("torch")));
                            break;
                        case 3:
                            itemMap.put(new Point(x, y),
                                    new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("skull")));
                            break;
                        default:
                            break;
                    }
                } else if (withinMapBoundary(x, y + 1) && (wallCells[x][y + 1].getCellType() != null
                        || map[x][y + 1].getState().getCellState() == CellState.BLANK)) {

                    switch (chance) {
                        case 0:
                            map[x][y].setState(MapCellFactory.getCellType(CellState.TILE_UP_LEFT));
                            break;
                        case 1:
                            map[x][y].setState(MapCellFactory.getCellType(CellState.TILE_UP_RIGHT));
                            break;
                        case 2:
                            itemMap.put(new Point(x, y),
                                    new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("box")));
                            break;
                        case 3:
                            itemMap.put(new Point(x, y),
                                    new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("torch")));
                            break;
                        case 4:
                            itemMap.put(new Point(x, y),
                                    new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("skull")));
                        default:
                            break;
                    }
                } else if (withinMapBoundary(x, y - 1) && (wallCells[x][y - 1].getCellType() != null
                        || map[x][y - 1].getState().getCellState() == CellState.BLANK)) {

                    switch (chance) {
                        case 0:
                            map[x][y].setState(MapCellFactory.getCellType(CellState.TILE_LEFT));
                            break;
                        case 1:
                            map[x][y].setState(MapCellFactory.getCellType(CellState.TILE_RIGHT));
                            break;
                        case 2:
                            itemMap.put(new Point(x, y),
                                    new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("box")));
                            break;
                        case 3:
                            map[x][y].setState(MapCellFactory.getCellType(CellState.TILE_LIGHT));
                            break;
                        case 4:
                            itemMap.put(new Point(x, y),
                                    new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("skull")));
                        default:
                            break;
                    }
                }
            }

            LinkedList<MapCell> allCells = region.getRegionCells();
            allCells.removeAll(edgeCells);
        }

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {

                if (map[x][y].isFloor() && map[x][y].getState().getCellState() == CellState.TILE) {
                    switch (rand.nextInt(5)) {
                        case 0:
                            map[x][y].setState(MapCellFactory.getCellType(CellState.TILE_VAR1));
                            break;
                        case 1:
                            map[x][y].setState(MapCellFactory.getCellType(CellState.TILE_VAR2));
                            break;
                        case 2:
                            map[x][y].setState(MapCellFactory.getCellType(CellState.TILE_VAR3));
                            break;
                        default:
                            break;
                    }
                }

                if (withinMapBoundary(x - 2, y) && withinMapBoundary(x + 2, y) && withinMapBoundary(x, y - 2)
                        && withinMapBoundary(x, y + 2)) {

                    boolean empty = true;
                    for (int i = -1; i < 2; i++) {
                        for (int j = -1; j < 2; j++) {
                            if (!checkCellEmpty(x + i, y + j)) {
                                empty = false;
                            }
                        }
                    }
                    if (empty && rand.nextInt(itemDensity + 2) == 0) { // Plus 2 to ensure not every space can be
                                                                       // filled.
                        itemMap.put(new Point(x, y),
                                new Item(x * TILE_WIDTH, y * TILE_HEIGHT, ItemFactory.getItem("chest")));
                    }
                }

                if ((!itemMap.containsKey(new Point(x, y))) && map[x][y].getState().getCellState() == CellState.TILE) {
                    if (rand.nextInt(enemyDensity + 1) == 0) {
                        enemyLocations.add(new Point(x, y));
                    }
                }

            }
        }

        items.addAll(itemMap.values());
    }

    /**
     * Get the locations of all enemies on the map, so that they may be rendered by
     * LibGDX
     */
    public ArrayList<Point> getEnemyLocations() {
        return enemyLocations;
    }

    // ----------------------------------------------------------------------------------------------------------------
    /*
     * Following functions perform player start and level end positioning
     */

    private boolean checkCellEmpty(int x, int y) {
        boolean isFloor = map[x][y].isFloor();
        boolean hasItem = itemMap.containsKey(new Point(x, y));
        boolean isWall = wallCells[x][y].getCellType() != null;

        return isFloor && !hasItem && !isWall;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public MapCell[][] getMap() {
        return map;
    }

    public WallCell[][] getWallCells() {
        return wallCells;
    }

    public Point getFurthestPoints() {
        LinkedList<Point> validSpawnPoints = new LinkedList<>();

        // First, collect all valid floor positions with empty space around them
        for (int x = 1; x < height - 1; x++) {
            for (int y = 1; y < width - 1; y++) {
                if (map[x][y].isFloor() && !itemMap.containsKey(new Point(x, y))) {
                    // Check surrounding tiles to ensure there's space
                    boolean hasSpace = true;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (!map[x + dx][y + dy].isFloor() ||
                                    itemMap.containsKey(new Point(x + dx, y + dy))) {
                                hasSpace = false;
                                break;
                            }
                        }
                        if (!hasSpace)
                            break;
                    }
                    if (hasSpace) {
                        validSpawnPoints.add(new Point(x, y));
                    }
                }
            }
        }

        if (validSpawnPoints.isEmpty()) {
            return null;
        }

        Point start = null;
        Point end = null;
        double maxDistance = 0;

        // Compare each point with every other point to find the furthest pair
        for (Point p1 : validSpawnPoints) {
            for (Point p2 : validSpawnPoints) {
                if (p1 == p2)
                    continue;

                double distance = Math.sqrt(
                        Math.pow(p1.x - p2.x, 2) +
                                Math.pow(p1.y - p2.y, 2));

                if (distance > maxDistance) {
                    if (hasValidPath(p1, p2)) {
                        maxDistance = distance;
                        start = p1;
                        end = p2;
                    }
                }
            }
        }

        // Place exit item at end position
        if (end != null) {
            Item door = new Item(end.x * TILE_WIDTH, end.y * TILE_HEIGHT, ItemFactory.getItem("door"));
            itemMap.put(end, door);
            items.add(door);

            // These for loops ensure that there is a floor around the door, as the asset is
            // 32x32 instead of the standard 16x16
            for (int x = end.x - 1; x <= end.x + 1; x++) {
                for (int y = end.y - 1; y <= end.y + 1; y++) {
                    if (withinMapBoundary(x, y)) {
                        map[x][y].setState(MapCellFactory.getCellType(CellState.TILE));
                    }
                }
            }
        }

        return start;
    }

    private boolean hasValidPath(Point start, Point end) {
        boolean[][] visited = new boolean[height][width];
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        visited[start.x][start.y] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.x == end.x && current.y == end.y) {
                return true;
            }

            // Check all adjacent cells
            int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];

                if (withinMapBoundary(newX, newY) &&
                        !visited[newX][newY] &&
                        map[newX][newY].isFloor()) {
                    queue.add(new Point(newX, newY));
                    visited[newX][newY] = true;
                }
            }
        }

        return false;
    }
}