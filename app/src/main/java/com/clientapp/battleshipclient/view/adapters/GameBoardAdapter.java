//package com.clientapp.battleshipclient.view.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.FrameLayout;
//
//import androidx.core.content.ContextCompat;
//
//public class GameBoardAdapter extends BaseAdapter {
//    private Context context;
//    private final int[][] gridArray;
//
//    // Constructor
//    public SeaBattleGridAdapter(Context context, int[][] gridArray) {
//        this.context = context;
//        this.gridArray = gridArray;
//    }
//
//    @Override
//    public int getCount() {
//        return 100; // 10x10 grid
//    }
//
//    @Override
//    public Object getItem(int position) {
//        int row = position / 10;
//        int col = position % 10;
//        return gridArray[row][col];
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View gridView;
//
//        if (convertView == null) {
//            gridView = new View(context);
//            // Inflate your custom layout for each item
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            gridView = inflater.inflate(R.layout.grid_item, null);
//        } else {
//            gridView = convertView;
//        }
//
//        // Style your cell and set data here
//        // Example: setting a fine stroke for each cell
//        FrameLayout cellLayout = gridView.findViewById(R.id.cellLayout);
//        cellLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
//
//        return gridView;
//    }
//}
