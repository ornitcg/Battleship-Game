package com.clientapp.battleshipclient.view.view_utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.model.GameBoard;
import com.clientapp.battleshipclient.model.Tile.Tile;
import com.clientapp.battleshipclient.model.Tile.TileStateEnum;

import java.util.ArrayList;

public class GameGridLayoutAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Tile> tilesList = new ArrayList<>();
    GameBoard gameBoard;
    GridView gridView;


    public GameGridLayoutAdapter(Context context, GameBoard gameBoard, GridView gridView) {
        this.context = context;
        this.gameBoard = gameBoard;
        this.gridView = gridView;
        this.tilesList = gameBoard.getBoard();
    }


    /*
     *  Gets the number of tiles in the grid
     *  @return the number of tiles in the grid
     * */
    @Override
    public int getCount() {
        return tilesList.size();
    }

    /*
     *  Gets the tile at the given position
     *  @param position - the position of the tile
     * */
    @Override
    public Object getItem(int position) {
        return tilesList.get(position);
    }

    /*
     *  Gets the id of the tile at the given position
     *  @param position - the position of the tile
     * @return the id of the tile
     * */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /*
    *  Checks if the ids of the tiles are stable
    * */
    public boolean hasStableIds() {
        return true;
    }


    /*
    *  Gets the view of the tile at the given position
    * */
    @Override
    public View getView(int position, View tileView, ViewGroup parentContainer) {
        LayoutInflater tileInflater = LayoutInflater.from(parentContainer.getContext());

        if (tileView == null) {
//            tileView = new View(context); // if the tileView is null, create a new tileView
            tileView = tileInflater.inflate(R.layout.item_square_tile_sea, parentContainer, false);
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
                tileView.setBackgroundResource(R.drawable.tile_status_sea);
                break;
            case HIT:
                tileView = tileInflater.inflate(R.layout.item_square_tile_hit, parentContainer, false);
                break;
            case MISS:
                tileView = tileInflater.inflate(R.layout.item_square_tile_miss, parentContainer, false);
                break;
            default:
                break;
        }
        int width = parentContainer.getWidth();
        int tileSize = width / 10;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(tileSize, tileSize);
        tileView.setLayoutParams(params);
        tileView.setTag(position);
        return tileView;
    }


}
