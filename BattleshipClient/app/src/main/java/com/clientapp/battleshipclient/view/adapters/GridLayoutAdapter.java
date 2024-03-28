package com.clientapp.battleshipclient.view.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.Tile;

import java.util.ArrayList;

public class GridLayoutAdapter extends BaseAdapter {

    private ArrayList<Tile> tilesList;
    private Context context;
    public GridLayoutAdapter(Context context, ArrayList<Tile> tilesList) {
        this.context = context;
        this.tilesList = tilesList;
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
        tileView = squareInflater.inflate(R.layout.item_square_tile_empty, parentContainer, false);
        //for responsive design of grid
        int width = parentContainer.getWidth();
        int tileSize = width / 10;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(tileSize, tileSize);
        tileView.setLayoutParams(params);

        tileView.setOnClickListener(new View.OnClickListener() {
            @Override   // when a tile is clicked, the tile is fired
            public void onClick(View v) {


            }
        });

        return tileView;
    }
}
