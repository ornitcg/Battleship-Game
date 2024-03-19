package com.clientapp.battleshipclient.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class SquareAdapter extends BaseAdapter {
    private Context context;
    private final int numItems = 100; // 10x10 Grid

    public SquareAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return numItems;
    }

    @Override
    public Object getItem(int position) {
        return null; // Not used in this context
    }

    @Override
    public long getItemId(int position) {
        return 0; // Not used in this context
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View squareView;
        if (convertView == null) {
            // Create a new square View programmatically
            squareView = new View(context);
            // Calculate the size of the View based on the screen width and desired grid size
            int size = parent.getWidth() / 10;
            GridView.LayoutParams params = new GridView.LayoutParams(size, size);
            squareView.setLayoutParams(params);
            squareView.setBackgroundColor(Color.parseColor("#CCCCCC")); // Example color
        } else {
            squareView = convertView;
        }
        return squareView;
    }
}
