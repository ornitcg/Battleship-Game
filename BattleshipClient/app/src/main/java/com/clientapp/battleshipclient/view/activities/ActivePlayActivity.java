package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.Game;
import com.clientapp.battleshipclient.logic.GameBoard;
import com.clientapp.battleshipclient.logic.Tile;
import com.clientapp.battleshipclient.networking.GameNW;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.UI_utils.GameGridLayoutAdapter;
import com.clientapp.battleshipclient.view.UI_utils.PlacementGridLayoutAdapter;

import java.util.ArrayList;

public class ActivePlayActivity extends BaseActivity{

    private GridView currPlayerGridView;
    private GridView opponentGridView;
    private GameBoard currPlayerGameBoard;  // contains the data of the tiles
    private GameBoard opponentGameBoard;  // contains the data of the tiles
    private String currPlayerUserId;
    private String opponentUserId;
    private boolean gameOver = false;

    ArrayList<Tile> opponentTilesDataList = new ArrayList<Tile>();


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_play);
        Intent intent = this.getIntent();
        currPlayerUserId = intent.getStringExtra("currPlayerUserId");
        opponentUserId = intent.getStringExtra("opponentUserId");
        ArrayList<Tile> currPlayerTilesData = (ArrayList<Tile>) intent.getSerializableExtra("tilesList");
//        setCurrentPlayerGameBoard(currPlayerTilesData);

        if (currPlayerTilesData == null) {
            Log.d("ActivePlayActivity", "Error: tilesList is null");
            currPlayerTilesData = new ArrayList<>(); // You can optionally initialize to prevent further null pointer exceptions
        }

        ArrayList<Tile> opponentTilesDataList = new ArrayList<Tile>();
        setupMusicToggleButton(this);
        setupGridViews();
        Game game = new Game(currPlayerUserId, opponentUserId, currPlayerGameBoard, opponentGameBoard);

        //TODO START GAME LOOP
//        while(!gameOver) {
//            boolean isTurn = false;
//            // get the turnUser from the server (loop listener)
//
//            // display user's turn on top text view
//            // if turnUser is currPlayerUser, then currPlayerGridView is enabled and opponentGridView is disabled
//            // if turnUser is opponentUser, then currPlayerGridView is disabled and opponentGridView is enabled
//            // if the game is over, then gameOver = true
//        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

//    private void setCurrentPlayerGameBoard( ArrayList<Tile> currPlayerTilesData) {
//        currPlayerGameBoard = new GameBoard(currPlayerUserId, currPlayerTilesData);
//    }

    private void setupGridViews() {
        setCurrentPlayerGridView();
        setOpponentGridView();
        Log.d("ActivePlayActivity", "setupGridViews: opponentGameBoard: " + opponentGameBoard);
        // get the board of the current player from the server
//        fetchData(); // calls parseJSON that fills the tilesList with data
    }

    private void setOpponentGridView() { //set empty grid view
        opponentGridView = findViewById(R.id.opponentBoardGridId);
        FrameLayout opponentFrameLayout = findViewById(R.id.shipsOpponentFrameLayoutId);
        for (int i = 0; i < 100; i++) { //initialize the list of tiles with 100 SEA tiles
            opponentTilesDataList.add(new Tile(i));
        }
        Log.d("ActivePlayActivity", "setupGridViews: opponentTilesData size: " + opponentTilesDataList.size());

        GameGridLayoutAdapter opponentGridLayoutAdapter = new GameGridLayoutAdapter(this, currPlayerUserId, opponentUserId ,false,  opponentTilesDataList , opponentFrameLayout);
        opponentGridView.setAdapter(opponentGridLayoutAdapter);
    }

    private void setCurrentPlayerGridView() { //set grid from my board
        currPlayerGridView = findViewById(R.id.currPlayerBoardGridId);
        FrameLayout currPlayerFrameLayout = findViewById(R.id.shipsCurrPlayerFrameLayoutId);
        //log list size
//        Log.d("ActivePlayActivity", "setupGridViews: currPlayerTilesData size: " + currPlayerTilesData.size());
//        GameGridLayoutAdapter currPlayerGridLayoutAdapter = new GameGridLayoutAdapter(this, currPlayerUserId, opponentUserId,true ,currPlayerTilesData ,  currPlayerFrameLayout );
//        currPlayerGridView.setAdapter(currPlayerGridLayoutAdapter);
    }

    private void updateMyFleet() {
    }




    public void parseJSON(String response  ){ //in the future, this will be replaced with a call to the server
        //set adapter to the grid view with the list of tiles
//        PlacementGridLayoutAdapter currPlayerSquareTileAdapter = new PlacementGridLayoutAdapter(this, currPlayerGameBoard.getTilesList(), null, null);
//        PlacementGridLayoutAdapter opponentSquareTileAdapter = new PlacementGridLayoutAdapter(this, opponentGameBoard.getTilesList(), null,   null);

//        currPlayerGridView.setAdapter(currPlayerSquareTileAdapter); // set the adapter to the grid view
       // opponentGridVie   w.setAdapter(opponentSquareTileAdapter); // set the adapter to the grid view
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
