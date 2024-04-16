package com.clientapp.battleshipclient.view.UI_utils;
//REBASE

import static android.view.DragEvent.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.Ship;
import com.clientapp.battleshipclient.logic.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class PlacementGridLayoutAdapter extends BaseAdapter {

    private ArrayList<Tile> tilesDataList;
    private Context context;
    private boolean inGridBounds = false;
    HashMap<Integer, ImageView> shipViews = null;
    FrameLayout frameLayout;
    HashMap<Integer,Ship> ShipDataCollection = new HashMap<>();

    public PlacementGridLayoutAdapter(Context context, ArrayList<Tile> tilesDataList, HashMap<Integer, ImageView> shipViews, HashMap<Integer,Ship> ShipDataCollection , FrameLayout frameLayout) {
        this.context = context;
        this.tilesDataList = tilesDataList;
        this.shipViews = shipViews;
        this.frameLayout = frameLayout;
        this.ShipDataCollection = ShipDataCollection;
    }

    @Override
    public int getCount() {
        return tilesDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return tilesDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("ViewHolder")
    @Override
    // position 0-99, squareView is the returned tile, parentContainer is the gridview to which this adapter is set
    public View getView(int position, View tileView, ViewGroup parentContainer) {
        if (tileView == null) {
            tileView = new View(context); // if the tileView is null, create a new tileView
        }
        LayoutInflater squareInflater = LayoutInflater.from(parentContainer.getContext());

        switch (tilesDataList.get(position).getTileState()) {
            case SEA:
                tileView = squareInflater.inflate(R.layout.item_square_tile_sea, parentContainer, false);
                break;

            case SHIP:
                tileView = squareInflater.inflate(R.layout.item_square_tile_ship, parentContainer, false);
                break;
            case NEAR_SHIP:
                tileView = squareInflater.inflate(R.layout.item_square_tile_near_ship, parentContainer, false);
                break;
            case VALID_FOR_DROP:
                tileView = squareInflater.inflate(R.layout.item_square_tile_ship, parentContainer, false);
//                tileView.setBackgroundResource(R.drawable.tile_status_highlighted);
                break;
            case INVALID_FOR_DROP:
                tileView.setBackgroundResource(R.drawable.tile_status_forbidden);
                break;

            default:
                break;
        }//end switch

        //for responsive design of grid
        int width = parentContainer.getWidth();
        int tileSize = width / 10;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(tileSize, tileSize);
        tileView.setLayoutParams(params);
        tileView.setTag(position);

//        setTileOnclickListener(tileView, position);
        setTileDragAndDropListener(tileView);
        return tileView;
    } // end getView

    private void setTileDragAndDropListener(View tileView) {
        //add drag listener to each tile view to change its background color when dragged over
        int position = (int) tileView.getTag();
        tileView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View tileView, DragEvent event) {
                Ship shipData = (Ship) event.getLocalState();

                switch (event.getAction()) {
                    case ACTION_DRAG_STARTED:
                        return true;
                    case ACTION_DRAG_ENTERED:
                        resetAllTilesData();
                        notifyDataSetChanged();
                        setShipDataNewPositions(shipData, position); //checks if valid for drop
                        notifyDataSetChanged();
                        return true;
                    case ACTION_DRAG_EXITED:
                        resetAllTilesData();
                        shipData.resetShip();
                        notifyDataSetChanged();
                        return true;
                    case ACTION_DROP:
                        logTilesStatus("setTileDragAndDropListener");
                        Log.d("DEBUG setTileDragAndDropListener", "shipTilesPositions on ACTION_DROP: " + shipData.getShipPositionsArray());
                        if (isValidForDrop(shipData)) {
                            Log.d("DEBUG setTileDragAndDropListener", "ACTION_DROP approved");
                            setShipViewOnGrid( shipData, (ViewGroup) tileView.getParent());
                            setTilesWithShipDataAndStatus(shipData);
                            setNearShip(shipData);
                            notifyDataSetChanged();
                            shipData.setPlaced(true);
                            logTilesStatus("setTileDragAndDropListener");
                        }
                        else {
                            Log.d("DEBUG setTileDragAndDropListener", "ACTION_DROP Refused");
                            Toast.makeText(context, "Invalid placement", Toast.LENGTH_SHORT).show();
                            shipData.resetShip();
                            resetAllTilesData();
                            notifyDataSetChanged();
                            returnShipToInventory(shipData);
                            shipViews.get(shipData.getFrameId()).setRotation(0);

                        }
                        Log.d("DEBUG setTileDragAndDropListener", "shipTilesPositions on ACTION_DROP: " + shipData.getShipPositionsArray());
                        Log.d("DEBUG setTileDragAndDropListener", "tilesStatusData on ACTION_DROP: " + tilesDataList.get(position).getTileState());

                        // TODO take care of dropping on edge of grid (on -1)
                        return true;
                    default:
                        break;
            }//end switch
                return true;
            }//end onDrag
        }); //end setOnDragListener
    }// end setTileOnDragAndDropListener

    public void returnShipToInventory(Ship shipData) {
        shipData.resetShip();
        shipViews.get(shipData.getFrameId()).setVisibility(View.INVISIBLE);
        shipViews.get(shipData.getInventoryId()).setVisibility(View.VISIBLE);
    }
    public void setShipDataNewPositions(Ship shipData, int position) {
        setShipPositionsArray(position, shipData);
        setTilesStatusData(shipData);
        notifyDataSetChanged();
    }

    public void setTilesWithShipDataAndStatus(Ship shipData) {
        ArrayList<Integer> shipTilesPositions = shipData.getShipPositionsArray();
        if (shipTilesPositions.isEmpty()) return;
        int shipsSize = shipData.getSize();
        Log.d("DEBUG setTilesWithShipDataAndStatus", "shipTilesPositions: " + shipTilesPositions);
        for (int i = 0; i < shipsSize; i++) {
            int position = shipTilesPositions.get(i);
            if (position == -1) continue;
            tilesDataList.get(position).setTileState(Tile.State.SHIP);
            tilesDataList.get(position).setShipId(shipData.getInventoryId());
        }
        //TODO set NEAR_SHIP status for the tiles around the ship

    }

    private void setTilesStatusData(Ship shipData) {
        ArrayList<Integer> shipTilesPositions = shipData.getShipPositionsArray();
        int shipsSize = shipData.getSize();
        boolean isValidForDrop = isValidForDrop(shipData);
        if (isValidForDrop) {
            for (int i = 0; i < shipsSize; i++) {
                int position = shipTilesPositions.get(i);
                tilesDataList.get(position).setTileState(Tile.State.VALID_FOR_DROP);
                tilesDataList.get(position).setShipId(shipData.getInventoryId()); //TODO delete
            }
            logTilesStatus("setTilesStatusData");
            return;
        }
        for (int i = 0; i < shipsSize; i++) {
            int position = shipTilesPositions.get(i);
            if (position != -1) {
                tilesDataList.get(position).setTileState(Tile.State.INVALID_FOR_DROP);
            }
        }
        logTilesStatus("setTilesStatusData");
    }

    // TODO create a neibourghood method to set near ship status for the tiles around the ship

    public boolean setShipViewOnGrid(Ship shipData, ViewGroup gridView) {
        //TODO fix bug for  ship size 4- huge offset
        ArrayList<Integer> shipTilesPositions = shipData.getShipPositionsArray();
        View tileView = gridView.getChildAt(shipTilesPositions.get(0));
        ImageView frameShipView = shipViews.get(shipData.getFrameId());

        try {
            int tileX = (int) tileView.getLeft();
            int tileY = (int) tileView.getTop();
            int tileWidth = tileView.getWidth();
            int tileHeight = tileView.getHeight();
            int shipsize = shipData.getSize();
            boolean isShipEvenSize = shipsize % 2 == 0;
            Ship.Orientation orientation = shipData.getOrientation();
            int[] location = new int[2]; //for getting the XY of the grid
            ((View) tileView.getParent()).getLocationOnScreen(location);

            if (orientation == Ship.Orientation.VERTICAL) {
                frameShipView.setX(tileX);
                frameShipView.setY(tileY);
            } else if (orientation == Ship.Orientation.HORIZONTAL){
                if (isShipEvenSize) {
                    frameShipView.setX(tileX+(tileHeight/2)*(shipsize*3/4));
                    frameShipView.setY(tileY-(tileHeight/2)*(shipsize*3/4));
                }
                else {
                    frameShipView.setX(tileX+tileHeight*((int)(shipsize/2)));
                    frameShipView.setY(tileY-tileHeight*((int)(shipsize/2)));
                }
            }
            shipData.setShipPositionsArray(shipTilesPositions);
            ViewGroup.LayoutParams params = frameShipView.getLayoutParams(); //size of view
            params.width = tileWidth;
            params.height = tileHeight*shipsize;
            frameShipView.setLayoutParams(params);
            frameShipView.setVisibility(View.VISIBLE);
        } //end try
        catch (NullPointerException e) {
            frameShipView.setVisibility(View.INVISIBLE);

            return false;
        }//end catch
        return true;
    }



    public void setShipPositionsArray(int position, Ship shipData) {
        ArrayList<Integer> shipTilesPositions = shipData.getShipPositionsArray();
        int shipsSize = shipData.getSize();
        int tileCol = position % 10;  //get the column of the tile
        int tileRow = position / 10;   //get the row of the tile
        int shift = (shipsSize / 2) * (-1);
        int row=0;
        int col=0;
        shipData.forgetPositions();
        boolean isVertical = shipData.getOrientation() == Ship.Orientation.VERTICAL;
        for (int i = 0; i < shipsSize; i++) {
            if (isVertical) {
                row = tileRow + shift;
                if (row < 0 || row > 9) {
                    shipTilesPositions.add(-1);
                }
                else  {
                    shipTilesPositions.add(row*10 + tileCol);
                }
            } else { //HORIZONTAL
                col = tileCol + shift;
                if (col < 0 || col > 9) {
                    shipTilesPositions.add(-1);
                }
                else shipTilesPositions.add(tileRow*10 + col);
            }//end else
            shift += 1;
        }//end for
    }


    public void setAllTilesData(HashMap<Integer, Ship> shipDataCollection) {
        ArrayList<Ship> updatedShipData = new ArrayList<>();
        for (Map.Entry<Integer, Ship> entry : shipDataCollection.entrySet()) {
            Ship shipData = entry.getValue();
            if (updatedShipData.contains(shipData)) continue;
            updatedShipData.add(shipData);
            setTilesWithShipDataAndStatus(shipData); // notifies the adapter
            setNearShip(shipData);
        }
    }

//    public void clearTilesFromShipDataAndStatus(Ship shipData) {
//        ArrayList<Integer> shipTilesPositions = shipData.getShipPositionsArray();
//        if (shipTilesPositions.isEmpty()) return;
//        // log shipTilesPositions
//        Log.d("DEBUG clearTilesFromShipDataAndStatus", "shipTilesPositions to be cleaned: " + shipTilesPositions);
//        int shipsSize = shipData.getSize();
//        for (int i = 0; i < shipsSize; i++) {
//            int position = shipTilesPositions.get(i);
//            if ( position != -1) {  //case the position is within the grid it's ok to get the tile view
//                resetTilesStatusData( position, shipData);
//            }
//        }
//    }


    public boolean isValidForDrop( Ship shipData) {
        ArrayList<Integer> shipTilesPositions = shipData.getShipPositionsArray();
        boolean isWithinBounds = isWithinGridBounds(shipData);
        Log.d("DEBUG isValidForDrop", "isWithinBounds: " + isWithinBounds);
        if (!isWithinBounds) return false;
        setDroppedShipTilesStatus(shipData);
        for (int i = 0; i < shipData.getSize(); i++) {
            int position = shipTilesPositions.get(i);
            if (position == -1) {
                Log.d("DEBUG isValidForDrop", "position is -1");
                return false;
            }
            if (tilesDataList.get(position).getTileState() == Tile.State.NEAR_SHIP) {
                Log.d("DEBUG isValidForDrop", "tile is near ship");
                return false;
            }
            if (tilesDataList.get(position).getTileState() != Tile.State.SEA && tilesDataList.get(position).getTileState() != Tile.State.VALID_FOR_DROP){
//            if (tilesList.get(shipTilesPositions.get(i)).getTileStatus() != Tile.Status.SEA){

                    Log.d("DEBUG isValidForDrop", "tile Status is " + tilesDataList.get(position).getTileState());
                return false;
            }
        }
        return true;
    }

    private void setDroppedShipTilesStatus(Ship shipData) {
        //loop on shipcollection and set the status of the tiles to SHIP
        for(Map.Entry<Integer, Ship> entry : ShipDataCollection.entrySet()) {
            Ship ship = entry.getValue();
            if (ship.getPlaced() == false) continue;
            setTilesWithShipDataAndStatus(ship);
            setNearShip(ship);
            notifyDataSetChanged();
        }
    }

    private boolean isNearShip(int position) {
        int[] aroundTilePositionshifts = new int[]{-9,-10,-11,+9,+10,+11,-1,+1};
        for (int shift : aroundTilePositionshifts) { // revert 4 tiles around the tile
            int aroundTilePosition = position + shift;
            if (!isValidShift(position , shift)) continue;
            Tile tileAroundShip = tilesDataList.get(aroundTilePosition);
            if (tileAroundShip.getTileState() == Tile.State.SHIP) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidShift(int position, int shift) {
        int aroundTilePosition = position + shift;
        if ((aroundTilePosition < 0 || aroundTilePosition > 100) ||
        (position % 10 == 0 && (shift == -1 || shift == -11 || shift == 9)) ||
        (position % 10 == 9 && (shift == 1 || shift == 11 || shift == -9)) ||
        (position / 10 == 0 && (shift == -9 || shift == -10 || shift == -11)) ||
        (position / 10 == 9 && (shift == 9 || shift == 10 || shift == 11)) )
                return false;
        return true;
    }

    public void setNearShip(Ship shipData) {//TODO: requires changes
        ArrayList<Integer> shipTilesPositions = shipData.getShipPositionsArray();
        int[] aroundTilePositionShifts = new int[]{-9,-10,-11,+9,+10,+11,-1,+1};
        Log.d("DEBUG setNearShip", "setNearShip shipTilesPositions: " + shipTilesPositions);
        if (shipTilesPositions.isEmpty()) return;
        for (int i = 0; i < shipData.getSize(); i++) {
            int position = shipTilesPositions.get(i);

            for (int shift : aroundTilePositionShifts) { // revert 4 tiles around the tile
                int aroundTilePosition = position + shift;
                if (!isValidShift(position , shift)) continue;
                Tile tileAroundShip = tilesDataList.get(aroundTilePosition);
                if (tileAroundShip.getTileState() == Tile.State.SHIP) {
                    continue;
                } else tileAroundShip.setTileState(Tile.State.NEAR_SHIP);

            }//end inner for
        }//end outer for
    }//end setNearShip

    private boolean isWithinGridBounds(Ship shipData) {
        ArrayList<Integer> shipTilesPositions = shipData.getShipPositionsArray();
        for (int i = 0; i < shipTilesPositions.size(); i++) {
            if (shipTilesPositions.get(i) == -1) {
                return false;
            }
        }
        return true;
    }

    // TODO DO NOT DELETE!!!!
    private void setTileOnclickListener(View tileView, int position) {
        tileView.setOnClickListener(new View.OnClickListener() {
            @Override   // when a tile is clicked, the tile is fired
            public void onClick(View v) { // ********* later move to game activity **********
                // implement attack logic
                int position = (int) v.getTag(); // use later
                Tile.State tileState = tilesDataList.get(position).getTileState();
                if (tileState == Tile.State.HIT || tileState == Tile.State.MISS)
                    Toast.makeText(context, "Tile already fired", Toast.LENGTH_SHORT).show();
                tilesDataList.get(position).setFired(true);
                notifyDataSetChanged();

            }
        });
    }


    private void logTilesStatus(String methodName) {
        for (Tile tile : tilesDataList) {
            Log.d("method" + methodName, "Tile: " + tile.getPosition() + " STATUS: : " + tile.getTileState());
        }
    }


    public void resetAllTilesData() {
        // loop on tilesDataList and reset all tiles to SEA
        for (Tile tile : tilesDataList) {
            tile.resetSingleTileData();
        }
    }
}//end class
