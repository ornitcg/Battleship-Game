package com.clientapp.battleshipclient.view.UI_utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.data.GameBoard;
import com.clientapp.battleshipclient.data.Ship.OrientationEnum;
import com.clientapp.battleshipclient.data.Ship.Ship;
import com.clientapp.battleshipclient.data.Ship.ShipTypeEnum;
import com.clientapp.battleshipclient.data.Ship.ShipsResources;
import com.clientapp.battleshipclient.data.Tile.Tile;
import com.clientapp.battleshipclient.data.Tile.TileStateEnum;

import java.util.ArrayList;
import java.util.HashMap;

public class GameGridLayoutAdapter extends BaseAdapter {
    GameBoard gameBoard;
    GridView gridView;
    private ArrayList<Tile> tilesList = new ArrayList<>();
    private FrameLayout frameLayout;
    private Context context;
    private boolean thisGridBelongsToCurrUserId = true;
    private HashMap<ShipTypeEnum, ImageView> mapShipNameToView = new HashMap<>();


    public GameGridLayoutAdapter(Context  context, boolean thisGridBelongsToCurrUserId, GameBoard gameBoard,  GridView gridView, FrameLayout frameLayout) {
        this.context = context;
        this.thisGridBelongsToCurrUserId = thisGridBelongsToCurrUserId;
        this.gameBoard = gameBoard;
        this.gridView = gridView;
        this.tilesList = gameBoard.getBoard();
        this.frameLayout = frameLayout;
        if (thisGridBelongsToCurrUserId)
            setMapShipNameToView("bottom");
        //log mapShipNameToView
        Log.d("GameGridLayoutAdapter", "GameGridLayoutAdapter: mapShipNameToView: " + mapShipNameToView);
    }

    private void setMapShipNameToView(String gridLocation) {
        ArrayList<Ship> shipList = gameBoard.getShips();
        for (int i = 0; i < shipList.size() ; i++) {
            Ship ship = shipList.get(i);
            addShipViewToMap(ship, gridLocation);//
        }
    }

    public void addShipViewToMap(Ship ship, String gridLocation) {
        ShipTypeEnum shipType = ship.getType();
        int shipDrawableId;
        if (gridLocation.equals("top"))
             shipDrawableId = ShipsResources.getTopShipIdByType(ShipTypeEnum.valueOf(shipType.toString()));
        else  shipDrawableId = ShipsResources.getBottomShipIdByType(ShipTypeEnum.valueOf(shipType.toString()));
        ImageView shipView = (ImageView) frameLayout.findViewById(shipDrawableId);
        setShipViewOrientation(shipView, ship);
        shipView.setTag(ship);
        shipView.setVisibility(View.VISIBLE);
        mapShipNameToView.put(shipType, shipView);
        Log.d("GameGridLayoutAdapter", "addShipViewToMap: shipName: " + shipType + " shipView: " + shipView);
    }

    public boolean setShipViewOnGrid(Ship ship, ViewGroup gridView) {
        int edgePosition = ship.getEdgePosition();
        View tileView = gridView.getChildAt(edgePosition); //get the view of the tile
        Log.d("DEBUG GameGridLayoutAdapter", "position: " + edgePosition);
        Log.d("DEBUG GameGridLayoutAdapter", "tileView: " + tileView);
        ImageView shipView = mapShipNameToView.get(ship.getType());
        //log dhipview
        Log.d("GameGridLayoutAdapter", "setShipViewOnGrid: shipView: " + shipView);
        //log shipdata
        Log.d("GameGridLayoutAdapter", "setShipViewOnGrid: shipData: " + ship);
        //log tileView
        Log.d("GameGridLayoutAdapter", "setShipViewOnGrid: tileView: " + tileView);

        try {
            int tileX = (int) tileView.getLeft();
            int tileY = (int) tileView.getTop();
            int tileWidth = tileView.getWidth();
            int tileHeight = tileView.getHeight();
            int shipsize = ship.getSize();
            boolean isShipEvenSize = shipsize % 2 == 0;
            OrientationEnum orientation = ship.getOrientation();
            int[] location = new int[2]; //for getting the XY of the grid
            ((View) tileView.getParent()).getLocationOnScreen(location);

            if (orientation == OrientationEnum.VERTICAL) {
                shipView.setX(tileX);
                shipView.setY(tileY);
            } else if (orientation == OrientationEnum.HORIZONTAL){
                if (isShipEvenSize) {
                    shipView.setX(tileX+(tileHeight/2)*(shipsize*3/4));
                    shipView.setY(tileY-(tileHeight/2)*(shipsize*3/4));
                }
                else {
                    shipView.setX(tileX+tileHeight*((int)(shipsize/2)));
                    shipView.setY(tileY-tileHeight*((int)(shipsize/2)));
                }
            }
            ViewGroup.LayoutParams params = shipView.getLayoutParams(); //size of view
            params.width = tileWidth;
            params.height = tileHeight * shipsize;
            shipView.setLayoutParams(params);
            shipView.setVisibility(View.VISIBLE);
        } //end try
        catch (NullPointerException e) {
            shipView.setVisibility(View.INVISIBLE);

            return false;
        }//end catch
        return true;
    }


    private void setShipViewOrientation(ImageView shipView, Ship ship) {
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
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View tileView, ViewGroup parentContainer) {
        if (tileView == null) {
            tileView = new View(context); // if the tileView is null, create a new tileView
        }

        //log new view
        LayoutInflater squareInflater = LayoutInflater.from(parentContainer.getContext());
        TileStateEnum tileState = tilesList.get(position).getState();
        //log state
//        Log.d("GameGridLayoutAdapter", "getView: tileState: " + tileTileStateEnum);
        switch (tileState) {
            case SEA:
                tileView = squareInflater.inflate(R.layout.item_square_tile_sea, parentContainer, false);
                break;
            case SHIP:
                tileView = squareInflater.inflate(R.layout.item_square_tile_ship, parentContainer, false);
                break;
            case NEAR_SHIP:
                tileView = squareInflater.inflate(R.layout.item_square_tile_sea, parentContainer, false);

//                if (thisGridBelongsToCurrUserId) {
//                    tileView = squareInflater.inflate(R.layout.item_square_tile_sea, parentContainer, false);
//                } else { //TODO set only after given ship is sunk
//                    tileView = squareInflater.inflate(R.layout.item_square_tile_near_ship, parentContainer, false);
//                }
                break;
            case HIT:
                tileView = squareInflater.inflate(R.layout.item_square_tile_hit, parentContainer, false);
                break;
            case MISS:
                tileView = squareInflater.inflate(R.layout.item_square_tile_miss, parentContainer, false);
                break;
            default:
                break;
        }//end switch
//        Log.d("DEBUG GameGridLayoutAdapter", "getView: new view: " + tileView.getResources());
        //log chilgren of gridview
//        Log.d("DEBUG GameGridLayoutAdapter", "getView: children of gridview: " + parentContainer.getChildCount());
        int width = parentContainer.getWidth();
        int tileSize = width / 10;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(tileSize, tileSize);
        tileView.setLayoutParams(params);
        tileView.setTag(position);

//      setTileOnclickListener(tileView, position);//TODO
        return tileView;
    }

    public void setAllShipViewsOnBoard() {
        ArrayList<Ship> shipList = gameBoard.getShips();
        for (int i = 0; i < shipList.size(); i++) {
            setShipViewOnGrid(shipList.get(i), gridView );
            //log ship position
            Log.d("DEBUG GameGridLayoutAdapter", "setAllShipViewsOnBoard: ship position: " + shipList.get(i).getEdgePosition());
        }
    }

//    public View getShipViewByType(ShipsResources.ShipTypeEnum shipType) {
////        return mapShipNameToView.get(shipTypef);  //TODO AHAH
//    }
}
