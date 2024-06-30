package com.clientapp.battleshipclient.view.view_utils;
//REBASE

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.model.Tile.Tile;
import com.clientapp.battleshipclient.model.Tile.TileStateEnum;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Data;


/*
 *  This class is the adapter for the gridview in the PlacementActivity
 *  It is used to set the tiles in the gridview and to set the drag and drop listener for each tile
 *  It also sets the status of the tiles when a ship is dragged over them
 * */

@Data
public class PlacementAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Tile> tilesList;
    private HashMap<Integer, View> stableViews = new HashMap<>();
    private OnTileDragListener tileDragListener = null;


    /*
     *  Constructor for the PlacementAdapter class
     * */
    public PlacementAdapter(Context context, ArrayList<Tile> tilesList) {
        this.context = context;
        this.tilesList = tilesList;
    }


    /*
     *  Override methods for the BaseAdapter class
     *  returns the number of tiles in the gridview
     * */
    @Override
    public int getCount() {
        return tilesList.size();
    }


    /*
     *  Override methods for the BaseAdapter class
     *  returns the tile at the given position
     * */
    @Override
    public Object getItem(int position) {
        return tilesList.get(position);
    }


    /*
     *  Override methods for the BaseAdapter class
     *  returns the id of the tile at the given position
     * */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /*
     *  Override methods for the BaseAdapter class
     *  returns true if the ids are stable
     * */
    @Override
    public boolean hasStableIds() {
        return true;
    }


    /*
     *  Override methods for the BaseAdapter class
     *  returns the view of the tile at the given position
     * (position 0-99, squareView is the returned tile,
     * parentContainer is the gridview to which this adapter is set)
     * */
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View tileView, ViewGroup parentContainer) {
        if (tileView == null) {
            tileView = stableViews.get(position);
        }

        LayoutInflater tileInflater = LayoutInflater.from(context);
        if (tileView == null) {
            tileView = tileInflater.inflate(R.layout.item_square_tile_sea, parentContainer, false);
            stableViews.put(position, tileView);

            tileView.setTag(position);
            tileView.setOnDragListener((v, event) -> {
                if (tileDragListener != null)
                    return tileDragListener.onTileDrag(v, event, position);
                return false;
            });
        }

        TileStateEnum tileState = tilesList.get(position).getState();

        switch (tileState) {
            case SEA:
                tileView.setBackgroundResource(R.drawable.tile_status_sea);
                break;
            case SHIP:
                tileView.setBackgroundResource(R.drawable.tile_status_ship);
                break;
            case NEAR_SHIP:

                tileView.setBackgroundResource(R.drawable.tile_status_sea);//change background for debugging
                break;
            case VALID_FOR_DROP:

                tileView.setBackgroundResource(R.drawable.tile_status_ship);
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

        return tileView;
    } // end getView




    /*
     *  Sets the tile drag listener for the gridview
     * */
    public void setTileDragListener(OnTileDragListener listener) {
        tileDragListener = listener;
    }


    /*
     *  Returns the tile drag listener for the gridview
     * */
    public OnTileDragListener getTileDragListener() {
        return tileDragListener;
    }


    /*
     *  Returns the gridlayout of the gridview
     * */
    public ViewGroup getGridLayout() {
        return (ViewGroup) stableViews.get(0).getParent();
    }

    /*
     *  Interface for the tile drag listener
     * */
    public interface OnTileDragListener {
        boolean onTileDrag(View v, DragEvent event, int position);
    }
}//end class
