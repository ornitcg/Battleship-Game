package com.clientapp.battleshipclient.view.UI_utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.Tile;

import java.util.ArrayList;

public class GameGridLayoutAdapter extends BaseAdapter {
    private ArrayList<Tile> tilesDataList = new ArrayList<>();
    private FrameLayout frameLayout;
    private Context context;
    private String currPlayerUserId ;
    private String opponentUserId ;
    private boolean thisGridBelongsToCurrUserId = true;


    public GameGridLayoutAdapter(Context  context,String currPlayerUserId, String opponentUserId, boolean thisGridBelongsToCurrUserId, ArrayList<Tile> tilesDataList, FrameLayout frameLayout) {
        this.tilesDataList = tilesDataList;
        //log list size
        Log.d("GameGridLayoutAdapter", "GameGridLayoutAdapter: tilesDataList size: " + tilesDataList.size());
        this.frameLayout = frameLayout;
        this.context = context;
        this.currPlayerUserId = currPlayerUserId;
        this.opponentUserId = opponentUserId;
        this.thisGridBelongsToCurrUserId = thisGridBelongsToCurrUserId;
        //TODO maybe need to add ship data and ship views
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
    public boolean hasStableIds() {
        return true;
    }

    @Override
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
                if (thisGridBelongsToCurrUserId) {
                    tileView = squareInflater.inflate(R.layout.item_square_tile_sea, parentContainer, false);
                } else { //TODO set only after given ship is sunk
                    tileView = squareInflater.inflate(R.layout.item_square_tile_near_ship, parentContainer, false);
                }
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

        int width = parentContainer.getWidth();
        int tileSize = width / 10;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(tileSize, tileSize);
        tileView.setLayoutParams(params);
        tileView.setTag(position);

//      setTileOnclickListener(tileView, position);//TODO

        return tileView;
    }
}
