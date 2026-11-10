package com.PGD;

import java.util.LinkedList;

/*
 * Represents a collection of cells generated on the map that have formed a "cluster".
 * 
 */
public class Region implements Comparable<Region> {
    private final LinkedList<MapCell> regionCells;
    private final LinkedList<MapCell> edgeCells;
    private final LinkedList<Region> connectedRegions;
    private final int regionSize;
    private boolean accessMainRegion;
    private boolean isMainRegion;

    /*
     * Constructor for Region
     * 
     * @param regionCells - Cells that form the region
     */
    public Region(LinkedList<MapCell> regionCells) {
        this.regionCells = regionCells;
        regionSize = regionCells.size();
        connectedRegions = new LinkedList<>();
        edgeCells = new LinkedList<>();
        accessMainRegion = false;
        isMainRegion = false;
    }

    // Getters and Setters
    public LinkedList<MapCell> getRegionCells() {
        return regionCells;
    }

    public LinkedList<MapCell> getRegionEdgeCells() {
        return edgeCells;
    }

    public LinkedList<Region> getConnectedRegions() {
        return connectedRegions;
    }

    public boolean isConnected(Region otherRegion) {
        return connectedRegions.contains(otherRegion);
    }

    public void addConnection(Region otherRegion) {
        connectedRegions.add(otherRegion);
    }

    public int getRegionSize() {
        return regionSize;
    }

    public boolean checkMainRegionAccess() {
        return accessMainRegion;
    }

    public boolean checkMainRegion() {
        return isMainRegion;
    }

    public void setMainRegionAccess(boolean access) {
        accessMainRegion = access;
    }

    public void setMainRegionStatus(boolean status) {
        isMainRegion = status;
    }

    /*
     * Set the edge cells of the region
     * 
     * @param map - Map of cells
     */
    public void setEdgeCells(MapCell[][] map) {
        for (MapCell cell : regionCells) {
            int cellX = cell.getX();
            int cellY = cell.getY();

            for (int x = cellX - 1; x <= cellX + 1; x++) {
                for (int y = cellY - 1; y <= cellY + 1; y++) {

                    if (x >= 0 && x < map.length && y >= 0 && y < map[x].length) {
                        if (!map[x][y].getState().toString().startsWith("TILE") && (x == cellX || y == cellY)) {
                            edgeCells.add(cell);
                        }
                    } else if (!edgeCells.contains(cell)) {
                        edgeCells.add(cell);
                    }
                }
            }
        }
    }

    /*
     * Join two regions together
     * 
     * @param otherRegion - Region to join
     */
    public void joinRegions(Region otherRegion) {
        if (accessMainRegion) {
            otherRegion.setMainRegionAccess(true);

            for (Region region : otherRegion.getConnectedRegions()) {
                region.setMainRegionAccess(true);
            }
        } else if (otherRegion.checkMainRegionAccess()) {
            setMainRegionAccess(true);
            for (Region region : connectedRegions) {
                region.setMainRegionAccess(true);
            }
        }

        connectedRegions.add(otherRegion);
        otherRegion.addConnection(this);
    }

    /*
     * Compare regions by size
     * 
     * @param otherRegion - Region to compare
     */
    @Override
    public int compareTo(Region otherRegion) {
        return Integer.compare(regionSize, otherRegion.regionSize);
    }
}