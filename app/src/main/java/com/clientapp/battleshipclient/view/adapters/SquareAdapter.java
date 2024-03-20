package com.clientapp.battleshipclient.view.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import com.clientapp.battleshipclient.R;


public class SquareAdapter extends BaseAdapter {
    private final int ROWS = 10;
    private final int COLUMNS = 10;
    private final int GRID_ITEMS_NUM = 100;
    private Context context;


    private int[][] board; // This represents your game board

    public SquareAdapter(Context context) {
        this.context = context;
        this.board = new int[ROWS][COLUMNS]; // Initialize your board with default values
        // Fill your board as necessary, or pass it in as a parameter
    }

    @Override
    public int getCount() {
        return GRID_ITEMS_NUM; // 10x10 grid
    }

    @Override
    public Object getItem(int position) {
        int row = position / ROWS;
        int col = position % COLUMNS;
        return board[row][col];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            // If the view is not recycled, this creates a new View to hold the grid item.
//            LayoutInflater inflater = LayoutInflater.from(context);
//            convertView = inflater.inflate(R.layout.grid_cell, parent, false);
//        }
//        // Here, you don't need to set the background resource again if it's already defined in the XML.
//
//        // If you want to customize this view based on the position (for example, if you want to hide the first cell), you can do so here.
//
//        return convertView;
//    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.grid_cell, parent, false);
//        }
//        // Optionally customize the view based on the item's state
//        int row = position / 10;
//        int col = position % 10;
//        // Use board[row][col] to adjust the view as necessary, e.g., marking hit or miss
//
//        return convertView;
//
        View squareView;
        if (convertView == null) {
            // Create a new square View programmatically
            squareView = new View(context);
            // Calculate the size of the View based on the screen width and desired grid size
            int size = parent.getWidth() / COLUMNS;
            GridView.LayoutParams params = new GridView.LayoutParams(size, size);
            squareView.setLayoutParams(params);
            squareView.setBackgroundResource(R.drawable.grid_cell_empty); // Example color
        } else {
            squareView = convertView;
        }
        return squareView;

    }
}


