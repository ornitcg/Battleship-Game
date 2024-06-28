package com.clientapp.battleshipclient.view.view_utils;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.clientapp.battleshipclient.model.Ship.OrientationEnum;
import com.clientapp.battleshipclient.model.Ship.Ship;
import com.clientapp.battleshipclient.model.Tile.Tile;

import java.util.ArrayList;

public class PlacementUtils {
    /*
     *  This method changes the orientation of the ship view according to the ship data
     */
    public static void shipViewSetOrientationAccordingToData(ImageView shipView, Ship ship) {
        OrientationEnum orientation = ship.getOrientation();
        switch (orientation) { // chnage the orientation of the ship view
            case HORIZONTAL:
                shipView.setRotation(90);
                break;
            case VERTICAL:
                shipView.setRotation(0);
                break;
        }
    }


    /**
     * This method is used to check if the ship can be placed at the given position     *
     */
    public static boolean isValidShift(int position, int shift) {
        int aroundTilePosition = position + shift;
        if ((aroundTilePosition < 0 || aroundTilePosition > 100) ||
                (position % 10 == 0 && (shift == -1 || shift == -11 || shift == 9)) ||
                (position % 10 == 9 && (shift == 1 || shift == 11 || shift == -9)) ||
                (position / 10 == 0 && (shift == -9 || shift == -10 || shift == -11)) ||
                (position / 10 == 9 && (shift == 9 || shift == 10 || shift == 11)))
            return false;
        return true;
    }


    /**
     * This method is used to populate the gameboard with 100 SEA tiles     *
     *
     * @return ArrayList<Tile> - a list of 100 SEA tiles
     */
    public static ArrayList<Tile> populateWithTiles() {
        ArrayList<Tile> tilesList = new ArrayList<>();
        for (int i = 0; i < 100; i++) { //initialize the list of tiles with 100 SEA tiles
            tilesList.add(new Tile(i));
        }
        return tilesList;
    }


    public static void fillShipPositionsArray(int position, Ship ship, String location) {
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        int shipsSize = ship.getSize();
        int tileCol = position % 10;  //get the column of the tile
        int tileRow = position / 10;   //get the row of the tile
        int shift = (shipsSize / 2) * (-1);
        if (location != null) {
            if (location.equals(OrientationEnum.EDGE.getName())) {
                shift = 0;
            }
        }
        int row;
        int col;
        ship.forgetPositions();
        boolean isVertical = ship.getOrientation() == OrientationEnum.VERTICAL;
        for (int i = 0; i < shipsSize; i++) {
            if (isVertical) {
                row = tileRow + shift;
                if (row < 0 || row > 9) {
                    shipTilesPositions.add(-1);
                } else {
                    shipTilesPositions.add(row * 10 + tileCol);
                }
            } else { //HORIZONTAL
                col = tileCol + shift;
                if (col < 0 || col > 9) {
                    shipTilesPositions.add(-1);
                } else shipTilesPositions.add(tileRow * 10 + col);
            }//end else
            shift += 1;
        }//end for
    }


    public static void setShipViewOnGrid(Ship ship, ImageView shipView, ViewGroup gridView) {
        if (ship.getEdgePosition() == -1)
            ship.setEdgePosition();
        int edgePosition = ship.getEdgePosition();
        View tileView = gridView.getChildAt(edgePosition); //get the view of the tile

        try {
            int tileX = tileView.getLeft();
            int tileY = tileView.getTop();
            int tileWidth = tileView.getWidth();
            int tileHeight = tileView.getHeight();
            int shipSize = ship.getSize();
            boolean isShipEvenSize = shipSize % 2 == 0;
            OrientationEnum orientation = ship.getOrientation();
            int[] location = new int[2]; //for getting the XY of the grid
            ((View) tileView.getParent()).getLocationOnScreen(location);
            shipViewSetOrientationAccordingToData(shipView, ship);
            if (orientation == OrientationEnum.VERTICAL) {
                shipView.setX(tileX);
                shipView.setY(tileY);
            } else if (orientation == OrientationEnum.HORIZONTAL) {
                if (isShipEvenSize) {
                    shipView.setX(tileX + (tileHeight / 2) * (shipSize * 3 / 4));
                    shipView.setY(tileY - (tileHeight / 2) * (shipSize * 3 / 4));
                } else {
                    shipView.setX(tileX + tileHeight * (shipSize / 2));
                    shipView.setY(tileY - tileHeight * (shipSize / 2));
                }
            }
            ViewGroup.LayoutParams params = shipView.getLayoutParams(); //size of view
            params.width = tileWidth;
            params.height = tileHeight * shipSize;
            shipView.setLayoutParams(params);
            shipView.setVisibility(View.VISIBLE);
        } //end try
        catch (NullPointerException e) {
            Log.d("myDEBUG GameActivity", "setShipViewOnGrid: " + e);
            if (shipView != null)
                shipView.setVisibility(View.INVISIBLE);


        }//end catch

    }
}
