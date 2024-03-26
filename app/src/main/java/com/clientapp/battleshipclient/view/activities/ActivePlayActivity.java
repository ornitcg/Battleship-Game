package com.clientapp.battleshipclient.view.activities;

import android.os.Bundle;
import android.widget.GridView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.Tile;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.adapters.GridLayoutAdapter;
import java.util.ArrayList;

public class ActivePlayActivity extends BaseActivity{

    private GridView currPlayerGridView;
    private GridView opponentGridView;
    private ArrayList<Tile> currPlayerTilesList;  // contains the data of the tiles
    private ArrayList<Tile> opponentTilesList;  // contains the data of the tiles
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_play);
        setupMusicToggleButton(this);
        setupGridViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupGridViews() {
        currPlayerGridView = findViewById(R.id.currPlayerBoardGridId);
        opponentGridView = findViewById(R.id.opponentBoardGridId);
        currPlayerTilesList = new ArrayList<Tile>();
        opponentTilesList = new ArrayList<Tile>();
        for (int i = 0; i < 100; i++) { //initialize the list of tiles with 100 empty tiles
            currPlayerTilesList.add(new Tile(i));
            opponentTilesList.add(new Tile(i));
        }
        fetchData(); // calls parseJSON that fills the tilesList with data
    }

    private void updateMyFleet() {
    }


    private void fetchData() {  //in the future, this will be replaced with a call to the server
        parseJSON("response1");
        parseJSON("response2");

    }

    public void parseJSON(String response  ){ //in the future, this will be replaced with a call to the server
        //set adapter to the grid view with the list of tiles
        GridLayoutAdapter currPlayerSquareTileAdapter = new GridLayoutAdapter(this, currPlayerTilesList);
        GridLayoutAdapter opponentSquareTileAdapter = new GridLayoutAdapter(this, opponentTilesList);

        currPlayerGridView.setAdapter(currPlayerSquareTileAdapter); // set the adapter to the grid view
        opponentGridView.setAdapter(opponentSquareTileAdapter); // set the adapter to the grid view
    }

    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic( ActivePlayActivity.this); // mute the music
    }
    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState( ActivePlayActivity.this); // mute the music
    }
}
