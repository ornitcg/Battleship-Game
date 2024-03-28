package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.Tile;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.adapters.GridLayoutAdapter;

import java.util.ArrayList;
//import com.clientapp.battleshipclient.view.adapters.TileAdapter;

public class PlaceYourShips extends BaseActivity {

    private GridView gridView;
    ArrayList<Tile> tilesList;  // contains the data of the tiles

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_ships);

        setupMusicToggleButton(this);
        setupGridView();    // set the adapter to the grid view


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void setupGridView() {  //called by onCreate
        gridView = findViewById(R.id.currPlayerBoardGridId); // get the reference of grid view
        tilesList = new ArrayList<Tile>();
        for (int i = 0; i < 100; i++) { //initialize the list of tiles with 100 empty tiles
            tilesList.add(new Tile(i));
        }
        fetchData(); // calls parseJSON that fills the tilesList with data
    }

    private void fetchData() {  //in the future, this will be replaced with a call to the server
        parseJSON("response");
    }

    public void parseJSON(String response){ //in the future, this will be replaced with a call to the server
        //set adapter to the grid view with the list of tiles
        GridLayoutAdapter squareTileAdapter = new GridLayoutAdapter(this, tilesList);
        gridView.setAdapter(squareTileAdapter); // set the adapter to the grid view

    }

    public void goToActivePlay(View v) {
        Intent intent = new Intent(PlaceYourShips.this, ActivePlayActivity.class);
//        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        startActivity(intent);
    }

    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(this);
    }

    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState( PlaceYourShips.this); // mute the music
    }
}