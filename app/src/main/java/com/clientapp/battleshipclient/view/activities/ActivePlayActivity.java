package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.GameBoard;
import com.clientapp.battleshipclient.logic.Tile;
import com.clientapp.battleshipclient.logic.User;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.adapters.GridLayoutAdapter;

public class ActivePlayActivity extends BaseActivity{

    private GridView currPlayerGridView;
    private GridView opponentGridView;
    private GameBoard currPlayerGameBoard;  // contains the data of the tiles
    private GameBoard opponentGameBoard;  // contains the data of the tiles
    private String currPlayerUserId;
    private String opponentUserId;
    private boolean gameOver = false;




    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_play);
        Intent intent = this.getIntent();
        currPlayerUserId = intent.getStringExtra("currPlayerUserId");
        opponentUserId = intent.getStringExtra("opponentUserId");
        setupMusicToggleButton(this);
        setupGridViews();

        while(!gameOver) {
            // get the turnUser from the server
            // display user's turn on top text view
            // if turnUser is currPlayerUser, then currPlayerGridView is enabled and opponentGridView is disabled
            // if turnUser is opponentUser, then currPlayerGridView is disabled and opponentGridView is enabled
            // if the game is over, then gameOver = true
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupGridViews() {
        currPlayerGridView = findViewById(R.id.currPlayerBoardGridId);
        opponentGridView = findViewById(R.id.opponentBoardGridId);

        currPlayerGameBoard = new GameBoard(currPlayerUserId);
        opponentGameBoard = new GameBoard(opponentUserId);

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
        GridLayoutAdapter currPlayerSquareTileAdapter = new GridLayoutAdapter(this, currPlayerGameBoard.getTilesList());
        GridLayoutAdapter opponentSquareTileAdapter = new GridLayoutAdapter(this, opponentGameBoard.getTilesList());

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
