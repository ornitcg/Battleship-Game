package com.clientapp.battleshipclient.view.UI_utils;
//REBASE

import static android.view.DragEvent.ACTION_DRAG_ENTERED;
import static android.view.DragEvent.ACTION_DRAG_EXITED;
import static android.view.DragEvent.ACTION_DRAG_STARTED;
import static android.view.DragEvent.ACTION_DROP;

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
import com.clientapp.battleshipclient.data.Ship.OrientationEnum;
import com.clientapp.battleshipclient.data.Ship.Ship;
import com.clientapp.battleshipclient.data.Tile.Tile;
import com.clientapp.battleshipclient.data.Tile.TileStateEnum;
import com.clientapp.battleshipclient.logic.ArrangeGameboardLogic;
import com.clientapp.battleshipclient.view.activities.ArrangeGameBoardActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;


/*
 *  This class is the adapter for the gridview in the ArrangeGameBoardActivity
 *  It is used to set the tiles in the gridview and to set the drag and drop listener for each tile
 *  It also sets the status of the tiles when a ship is dragged over them
 *  and when a ship is dropped on them it handles the ship placement on the grid
 * */

@Data
public class ArrangementGridLayoutAdapter extends BaseAdapter {

    private ArrayList<Tile> tilesList;
    private Context context;
    private boolean inGridBounds = false;
    HashMap<Integer, ImageView> shipViewsCollection = null;
    FrameLayout frameLayout;
    HashMap<Integer, Ship> shipCollection = new HashMap<>();

    public ArrangementGridLayoutAdapter(Context context, ArrayList<Tile> tilesDataList, HashMap<Integer, ImageView> shipViews, HashMap<Integer, Ship> ShipCollection, FrameLayout frameLayout) {
        this.context = context;
        this.tilesList = tilesDataList;
        this.shipViewsCollection = shipViews;
        this.frameLayout = frameLayout;
        this.shipCollection = ShipCollection;
    }

    @Override
    public int getCount() {
        return tilesList.size();
    }

    @Override
    public Object getItem(int position) {
        return tilesList.get(position);
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
        TileStateEnum tileState = tilesList.get(position).getState();

        switch (tileState) {
            case SEA:
                tileView = squareInflater.inflate(R.layout.item_square_tile_sea, parentContainer, false);
                break;
            case SHIP:
                tileView = squareInflater.inflate(R.layout.item_square_tile_ship, parentContainer, false);
                break;
            case NEAR_SHIP:
                tileView = squareInflater.inflate(R.layout.item_square_tile_sea, parentContainer, false);
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

        setTileDragAndDropListener(tileView);
        return tileView;
    } // end getView


    /*
     * This method is used to set the drag and drop listener for each tile in the grid
     * @param tileView - the tile view to which the listener is set
     * */
    private void setTileDragAndDropListener(View tileView) {
        //add drag listener to each tile view to change its background color when dragged over
        int position = (int) tileView.getTag();
        tileView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View tileView, DragEvent event) {
                Ship ship = (Ship) event.getLocalState();

                switch (event.getAction()) {
                    case ACTION_DRAG_STARTED:
                        return true;
                    case ACTION_DRAG_ENTERED:
                        ArrangeGameboardLogic.resetAllTilesData(tilesList);
                        notifyDataSetChanged();
                        setShipNewPositions(ship, position);
//                        notifyDataSetChanged(); //TODO delete
                        return true;
                    case ACTION_DRAG_EXITED:
                        ArrangeGameboardLogic.resetAllTilesData(tilesList);
                        ship.setPlaced(false);
                        ship.forgetPositions();
                        ship.setEdgePosition();
                        notifyDataSetChanged();
                        return true;
                    case ACTION_DROP:
                        if (isValidForDrop(ship)) {
                            setShipViewOnGrid(ship, (ViewGroup) tileView.getParent());
                            ArrangeGameboardLogic.setTilesWithShipAndStatus(ship, tilesList);
                            ArrangeGameboardLogic.setNearShip(ship, tilesList);
                            notifyDataSetChanged();
                            ship.setPlaced(true);
                        } else {
                            Toast.makeText(context, "Invalid placement", Toast.LENGTH_SHORT).show();
                            ship.resetShip();
                            ArrangeGameboardLogic.resetAllTilesData(tilesList);
                            notifyDataSetChanged();
                            ((ArrangeGameBoardActivity) context).returnShipToInventory(ship);
                            shipViewsCollection.get(ship.getTopViewId()).setRotation(0);
                        }
                        return true;
                    default:
                        break;
                }//end switch
                return true;
            }//end onDrag
        }); //end setOnDragListener
    }// end setTileOnDragAndDropListener


    /*
     *  Sets the new positionsArray of the ship when it is dragged over the grid
     *  with the new positions of tiles the ship is dropped on
     * */
    public void setShipNewPositions(Ship ship, int position) {
        Log.d("DEBUG setShipNewPositions", "position: " + position);
        ArrangeGameboardLogic.setShipPositionsArray(position, ship);
        setTilesState(ship);
        notifyDataSetChanged();
    }


    private void setTilesState(Ship ship) {
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        int shipsSize = ship.getSize();
        boolean isValidForDrop = isValidForDrop(ship);
        if (isValidForDrop) {
            for (int i = 0; i < shipsSize; i++) {
                int position = shipTilesPositions.get(i);
                tilesList.get(position).setState(TileStateEnum.VALID_FOR_DROP);
                tilesList.get(position).setShipId(String.valueOf(ship.getBottomViewId())); //TODO delete
            }
//            logTilesStatus("setTilesStatusData"); // used for debugging
            return;
        }
        for (int i = 0; i < shipsSize; i++) {
            int position = shipTilesPositions.get(i);
            if (position != -1) {
                tilesList.get(position).setState(TileStateEnum.INVALID_FOR_DROP);
            }
        }
//        logTilesStatus("setTilesStatusData"); // used for debugging
    }


    public boolean setShipViewOnGrid(Ship ship, ViewGroup gridView) {
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        if (ship.getEdgePosition() == -1) ship.setEdgePosition();
//        View tileView = gridView.getChildAt(ship.getPosition());
        View tileView = gridView.getChildAt(shipTilesPositions.get(0));

        // log position and tileview
        ImageView shipViewInFrame = shipViewsCollection.get(ship.getTopViewId());
        Log.d("DEBUG setShipViewOnGrid", "ship.getPosition(): " + ship.getEdgePosition());

        try {
            int tileX = (int) tileView.getLeft();
            int tileY = (int) tileView.getTop();
            int tileWidth = tileView.getWidth();
            int tileHeight = tileView.getHeight();
            int shipsize = ship.getSize();
            boolean isShipEvenSize = shipsize % 2 == 0;
            OrientationEnum orientation = ship.getOrientation();

            //log orientation
            Log.d("DEBUG setShipViewOnGrid", "orientation: " + orientation);
            int[] location = new int[2]; //for getting the XY of the grid
            ((View) tileView.getParent()).getLocationOnScreen(location);

            if (orientation == OrientationEnum.VERTICAL) {
                shipViewInFrame.setRotation(0);
                shipViewInFrame.setX(tileX);
                shipViewInFrame.setY(tileY);
            } else if (orientation == OrientationEnum.HORIZONTAL) {
                shipViewInFrame.setRotation(90);
                if (isShipEvenSize) {
                    shipViewInFrame.setX(tileX + (tileHeight / 2) * (shipsize * 3 / 4));
                    shipViewInFrame.setY(tileY - (tileHeight / 2) * (shipsize * 3 / 4));
                } else {
                    shipViewInFrame.setX(tileX + tileHeight * ((int) (shipsize / 2)));
                    shipViewInFrame.setY(tileY - tileHeight * ((int) (shipsize / 2)));
                }
            }
            ship.setShipPositionsArray(shipTilesPositions);
            ViewGroup.LayoutParams params = shipViewInFrame.getLayoutParams(); //size of view
            params.width = tileWidth;
            params.height = tileHeight * shipsize;
            shipViewInFrame.setLayoutParams(params);
            shipViewInFrame.setVisibility(View.VISIBLE);
        } //end try
        catch (NullPointerException e) {
            shipViewInFrame.setVisibility(View.INVISIBLE);
            return false;
        }//end catch
        return true;
    }


    public boolean isValidForDrop(Ship ship) {
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        boolean isWithinBounds = ArrangeGameboardLogic.isWithinGridBounds(ship);
//        Log.d("DEBUG isValidForDrop", "isWithinBounds: " + isWithinBounds);
        if (!isWithinBounds) return false;
        setDroppedShipTilesState();
        for (int i = 0; i < ship.getSize(); i++) {
            int position = shipTilesPositions.get(i);
            if (position == -1) {
//                Log.d("DEBUG isValidForDrop", "position is -1");
                return false;
            }
            if (tilesList.get(position).getState() == TileStateEnum.NEAR_SHIP) {
//                Log.d("DEBUG isValidForDrop", "tile is near ship");
                return false;
            }
            if (tilesList.get(position).getState() != TileStateEnum.SEA && tilesList.get(position).getState() != TileStateEnum.VALID_FOR_DROP) {
//            if (tilesList.get(shipTilesPositions.get(i)).getTileStatus() != Tile.Status.SEA){

                Log.d("DEBUG isValidForDrop", "tile Status is " + tilesList.get(position).getState());
                return false;
            }
        }
        return true;
    }


    /*
     *   This method is used to set the status of the tiles to SHIP, when a ship is dropped on them
     * */
    private void setDroppedShipTilesState() {
        //loop on shipcollection and set the status of the tiles to SHIP
        for (Map.Entry<Integer, Ship> entry : shipCollection.entrySet()) {
            Ship ship = entry.getValue();
            if (ship.isPlaced() == false) continue;
            ArrangeGameboardLogic.setTilesWithShipAndStatus(ship, tilesList);
            ArrangeGameboardLogic.setNearShip(ship, tilesList);
            notifyDataSetChanged();
        }
    }


    // TODO DO NOT DELETE!!!!
    private void setTileOnclickListener(View tileView, int position) {
        tileView.setOnClickListener(new View.OnClickListener() {
            @Override   // when a tile is clicked, the tile is fired
            public void onClick(View v) { // ********* later move to game activity **********
                // implement attack logic
                int position = (int) v.getTag(); // use later
                TileStateEnum tileTileStateEnum = tilesList.get(position).getState();
                if (tileTileStateEnum == TileStateEnum.HIT || tileTileStateEnum == TileStateEnum.MISS)
                    Toast.makeText(context, "Tile already fired", Toast.LENGTH_SHORT).show();
                tilesList.get(position).setFired(true);
                notifyDataSetChanged();
            }
        });
    }


}//end class
