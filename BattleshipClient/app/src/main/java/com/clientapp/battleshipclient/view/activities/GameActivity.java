package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.data.GameBoard;
import com.clientapp.battleshipclient.data.Ship.OrientationEnum;
import com.clientapp.battleshipclient.data.Ship.Ship;
import com.clientapp.battleshipclient.data.Ship.ShipTypeEnum;
import com.clientapp.battleshipclient.data.Ship.ShipsResources;
import com.clientapp.battleshipclient.data.Tile.Tile;
import com.clientapp.battleshipclient.data.Tile.TileStateEnum;
import com.clientapp.battleshipclient.data.User;
import com.clientapp.battleshipclient.logic.GameLogic;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.UI_utils.GameGridLayoutAdapter;
import com.clientapp.battleshipclient.view.UI_utils.Messages;
import com.clientapp.battleshipclient.view.UI_utils.OnSwipeTouchListener;

import java.util.ArrayList;

import lombok.Getter;

/**
 * This class represents the GameActivity activity
 * It is responsible for the game play screen
 * Handles all the view parts of the game
 */
public class GameActivity extends BaseActivity {
    private static final String AUTOMATIC_MOVE_MSG = "AUTOMATIC MOVE";
    private static final String SCORE_MESSAGE = "YOUR SCORE: ";
    private static final String GOOD_GAME_MESSAGE = "GOOD GAME!";
    private static final String WIN_MESSAGE = "GAME OVER YOU WON!";
    private static final String LOST_MESSAGE = "GAME OVER YOU LOST!";
    private GridView currPlayerGridView;
    private GridView opponentGridView;
    GameGridLayoutAdapter currPlayerGridLayoutAdapter;
    GameGridLayoutAdapter opponentGridLayoutAdapter;
    private GameBoard currPlayerGameBoard;  // contains the data of the tiles
    @Getter
    private GameBoard opponentGameBoard;  // contains the data of the tiles
    private String currPlayerId;
    private String gameId;
    private GameLogic gameLogic;
    private TextView topMessageTextView;
    @Getter
    private TextView autoAttackMsgView;
    private FrameLayout opponentFrameLayout;
    private FrameLayout currPlayerFrameLayout;

    public CountDownTimer countDownTimer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_game);

        Intent intent = this.getIntent();
        replaceMusic(AudioEnum.GAME_MUSIC);

        topMessageTextView = findViewById(R.id.turnMessageTextViewId);
        autoAttackMsgView = findViewById(R.id.msgTextViewId);
        setSwipeListener();
        setCurrentPlayerGameBoard(intent); //initial board data comes from intent

        setGameId(); //for convenience
        setOpponentGameBoard(); //opponentId data
        setShipResources();
        setGridViews();

        /*note to self: the grid views are not ready yet, so we can't set the ships on them yet.
         * Method 1: Force the view to be laid out NOW, in onCreate. This is not ideal.
         *findViewById(R.id.main).measure(
         *        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
         *        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
         *);
         * currPlayerGridView.layout(0, 0, currPlayerGridView.getMeasuredWidth(), currPlayerGridView.getHeight());
         * opponentGridView.layout(0, 0, opponentGridView.getMeasuredWidth(), opponentGridView.getHeight());
         * setCurrentPlayerShipsViewsOnBoard();
         */ //note to self

        // Method 2: Know when it is ready, and then use the view for whatever.
        currPlayerGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                currPlayerGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setCurrentPlayerShipsViewsOnBoard();
            }
        });
        // use the same method for the other grid view too.
        opponentGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                opponentGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setOpponentGridClickListeners();
            }
        });

        gameLogic = new GameLogic(this, currPlayerGameBoard);
        gameLogic.setKeepAliveRunnable();
        startGame();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTag), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }// onCreate

    private void setSwipeListener() {
        findViewById(R.id.mainTag).setOnTouchListener(new OnSwipeTouchListener(this));
    }


    /**
     * Initializes the ships maps on the resource class
     * to be used for displaying the right ship on the grid
     */
    private void setShipResources() {//initialize the ships maps on the resources
        ShipsResources.initNameToIdForBottomViews();
        ShipsResources.initNameToIdForTopViews();
    }


    /**
     * Sets the click listeners on the opponent grid tiles
     * called every time the current player is attacking
     */
    public void setOpponentGridClickListeners() {
        //loop over opponentGridView children and set click listeners
        int tilesCount = opponentGridView.getChildCount();
        ArrayList<Tile> tiles = opponentGameBoard.getBoard();
        for (int i = 0; i < tilesCount; i++) {
            View tileView = opponentGridView.getChildAt(i);
            if (tiles.get(i).getState() != TileStateEnum.SEA) {
                continue;
            }
            tileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();

                    Log.d("myDEBUG GameActivity", "attackOpponent: stopCountdown");

                    gameLogic.attackOpponent(gameId, currPlayerId, position);
                }
            });
        }
    }


    /**
     * Start point of the game
     * calls the gameLogic.getGame method via the runGameStatusChecks method
     */
    private void startGame() {
        Log.d("mymyDEBUG GameActivity", "in playGame");
        gameLogic.getGame(gameId, currPlayerId);
    }


    /**
     * Stops the board from being clickable
     */
    public void disableGameboard() {
        disableClickListeners();
    }


    /**
     * Shows the game over message
     *
     * @param isCurrPlayerWinner - true if the current player is the winner
     */
    public void displayFinalMessage(String gameState, Boolean isCurrPlayerWinner) {
        View messageVIew = findViewById(R.id.messageWrapperLayoutId);
        Button goToMenuButton = findViewById(R.id.backToMenuBtnId);
        String endGameMsg = "";
        switch (gameState) {
            case "finished":
                if (isCurrPlayerWinner) {
                    displayTopMessage(SCORE_MESSAGE + gameLogic.getAttacksCounter());
                    endGameMsg = WIN_MESSAGE;

                } else {
                    displayTopMessage(GOOD_GAME_MESSAGE);
                    endGameMsg = LOST_MESSAGE;
                }
                break;
            case "ended":
                Log.d("myDEBUG ShowFinalMessage", "gameState: " + gameState);
                endGameMsg = Messages.GAME_ABANDONED;
                break;
            default:
                endGameMsg = Messages.GAME_ENDED_CONNECTION;
        }
        Log.d("myDEBUG ShowFinalMessage", "endGameMsg: " + endGameMsg);
        ((TextView) messageVIew.findViewById(R.id.msgTopTextId)).setText(endGameMsg);
        messageVIew.setVisibility(View.VISIBLE);
        setBackToMenuButton(goToMenuButton);
    }


    /**
     * Sets the 'back to menu' button click listener
     *
     * @param goToMenuButton - the button that goes back to the menu
     */
    private void setBackToMenuButton(Button goToMenuButton) {
        goToMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMenuActivity(currPlayerGameBoard.getUser(), true);
            }
        });
    }


    @Override
    public void goToMenuActivity(User currentPlayer, Boolean shouldReplaceMusic) {
        super.goToMenuActivity(currentPlayer, shouldReplaceMusic);
        GameLogic.gameRunnablesHandler.removeCallbacksAndMessages(null);
    }


    /**
     * Sets the gameId from the game board
     */
    private void setGameId() {
        this.gameId = currPlayerGameBoard.getGameId();
    }


    /**
     * Sets the current player game board from the intent
     *
     * @param intent - the intent that started the activity
     */
    private void setCurrentPlayerGameBoard(Intent intent) {
        currPlayerGameBoard = (GameBoard) intent.getSerializableExtra("currPlayerBoard");
        if (currPlayerGameBoard != null) {
            currPlayerId = currPlayerGameBoard.getUser().getId(); //for convenience
        } else {
            Log.d("myDEBUG setCurrentPlayerGameBoard", "Error: currPlayerGameBoard is null");
        }
    }


    /**
     * Places the current player (bottom grid) ships views on the board
     */
    private void setCurrentPlayerShipsViewsOnBoard() {
        currPlayerGridLayoutAdapter.setAllShipViewsOnBoard();
    }


    /**
     * Sets the opponent game board - newly created board with 100 SEA tiles (top grid)
     * that through the game will be updated with the attacks results and ships.
     */
    private void setOpponentGameBoard() {
        opponentGameBoard = new GameBoard(null, gameId); // contructs a board with 100 SEA tiles
    }


    /**
     * Sets the board for wait. fogging its background and disabling the click listeners
     */
    public void disableBoardForAttack(String message) {
        stopCountdown();  // Stop any existing countdown first
        displayTopMessage(message);
        setOpponentFrameBackgroundOnAttack(false);
        disableClickListeners();
    }


    /**
     * Sets the board for attack. clearing its background and setting the click listeners
     */
    public void enableBoardForAttack() {
        stopCountdown();  // Stop any existing countdown first
        startCountDownTimer();
//        showTurnMessage(" YOUR TURN!");
        setOpponentFrameBackgroundOnAttack(true);
        setOpponentGridClickListeners();
    }


    public void startCountDownTimer() {
        stopCountdown();
        countDownTimer = new CountDownTimer(GameLogic.RANDOM_ATTACK_DELAY_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                topMessageTextView.setText("YOUR TURN! " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                Log.d("myDEBUG GameActivity", "attackOpponent on finish countdown");
                //display a message for 1 second
            }
        }.start();
    }

    public void stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * Sets the grid views for the current player and the opponent
     * calls the setCurrentPlayerGridView and setOpponentGridView methods
     */
    private void setGridViews() {
        setCurrentPlayerGridView();
        setOpponentGridView();
    }


    /**
     * Sets the opponent board (the top grid)
     * using the opponentGameBoard data and a GameGridLayoutAdapter
     */
    private void setOpponentGridView() { //set empty grid view
        opponentGridView = findViewById(R.id.opponentBoardGridId);
        opponentFrameLayout = findViewById(R.id.shipsOpponentFrameLayoutId);
//        Log.d("setOpponentGridView mymyDEBUG", "setupGridViews: opponentTilesData size: " + opponentGameBoard.getBoard().size());
        opponentGridLayoutAdapter = new GameGridLayoutAdapter(this, false, opponentGameBoard, opponentGridView, opponentFrameLayout);
        opponentGridView.setAdapter(opponentGridLayoutAdapter);
    }


    /**
     * Sets the current player board (the bottom grid)
     * using the currPlayerGameBoard data and a GameGridLayoutAdapter
     */
    private void setCurrentPlayerGridView() { //set grid from my board
        currPlayerGridView = findViewById(R.id.currPlayerBoardGridId);
        currPlayerFrameLayout = findViewById(R.id.shipsCurrPlayerFrameLayoutId);
        currPlayerGridLayoutAdapter = new GameGridLayoutAdapter(this, true, currPlayerGameBoard, currPlayerGridView, currPlayerFrameLayout);
        currPlayerGridView.setAdapter(currPlayerGridLayoutAdapter);
//        Log.d("setCurrentPlayerGridView mymyDEBUG", "setupGridViews: currPlayerTilesData size: " + currPlayerGameBoard.getBoard().size());
    }


    /**
     * Disables the click listeners on the opponent grid
     * called when the current player is waiting for the opponent to attack
     */
    public void disableClickListeners() {
        //loop on opponentGridView children and enable/disable click listeners
        for (int i = 0; i < opponentGridView.getChildCount(); i++) {
            View tileView = opponentGridView.getChildAt(i);
            tileView.setOnClickListener(null);
        }
    }


    /*
     *  Hides the ship view on the current player board
     *  this method is called when a ship is sunk (on the current player board)
     * */
    public void hideShip(String shipId) {
        View shipView = currPlayerFrameLayout.findViewById(Integer.parseInt(shipId));
        shipView.setVisibility(View.INVISIBLE);
    }


    /* Displays the turn message at the top of the game screen
     * @param turnMessage - the message to be displayed
     */
    public void displayTopMessage(String turnMessage) {
        Log.d("myDEBUG GameActivity", "displayTopMessage: " + turnMessage);
        topMessageTextView.setText(turnMessage);
    }


    /**
     * Updates the opponent board with the attack result
     *
     * @param position          - the position of the attack
     * @param attackResult      - the result of the attack (hit, miss, sunk)
     * @param shipName          - the name of the ship that was sunk
     * @param orientationString - the orientation of the ship that was sunk
     * @param shipPosition      - the position of the ship that was sunk
     * @param state
     */
    public void updateOpponentBoard(int position, String attackResult, String shipName, String orientationString, Integer shipPosition, TileStateEnum state) {

        opponentGameBoard.updateTile(position, state);
        opponentGridLayoutAdapter.notifyDataSetChanged();
        if (attackResult.equals("sunk")) { //TODO maybe add  attckResult.equals("win")
            ShipTypeEnum shipType = ShipTypeEnum.valueOf(shipName.toUpperCase());
            OrientationEnum orientation = OrientationEnum.valueOf(orientationString.toUpperCase());
            showShipViewOnOpponentBoard(shipPosition, orientation, shipType);
        }
    }


    /**
     * updates the board view with the attack result
     */
    public void updateCurrentPlayerBoardView() {
//        Log.d("mymyDEBUG GameActivity", "updateCurrentPlayerBoard: ");
        currPlayerGridLayoutAdapter.notifyDataSetChanged();
    }


    /**
     * Shows the ship view on the opponent board
     * every time a ship gets sunk     *
     *
     * @param position    - the position of the ship
     * @param orientation - the orientation of the ship
     * @param shipType    - the type of the ship
     */
    public void showShipViewOnOpponentBoard(int position, OrientationEnum orientation, ShipTypeEnum shipType) {
        int shipViewId = ShipsResources.getTopShipIdByType(shipType);
        int shipSize = ShipsResources.getShipSizeByType(shipType);
        Ship tempShip = new Ship(shipViewId, shipSize, shipType, orientation);
        tempShip.setEdgePosition(position);
//        Log.d("mymyDEBUG GameActivity", "showShipViewOnBoard: tempShip: " + tempShip);
        opponentGridLayoutAdapter.addShipViewToMap(tempShip, "top");  // create a ship and add its view to the map that fits the top grid
        opponentGridLayoutAdapter.setShipViewOnGrid(tempShip, opponentGridView);
    }


    /**
     * Sets the layer of the frame layout of the current player to the right background
     * to indicate the current player state (attack or waiting)
     *
     * @param isAttack - true if the current player is attacking
     */
    public void setOpponentFrameBackgroundOnAttack(Boolean isAttack) {
        if (isAttack)
            opponentFrameLayout.setBackgroundResource(R.drawable.background_frame_under_attack);
        else
            opponentFrameLayout.setBackgroundResource(R.drawable.background_frame_under_attack2);
    }


    /*
     * Overrides the exit method from the BaseActivity class
     * calls methods to stop the game nicely, in the of the game
     */
    @Override
    public void exit() {
        disableGameboard();
        setBrutalDestroy(false);
        GameLogic.notifyGameEnd(this, gameId);
        goToMenuActivity(currPlayerGameBoard.getUser(), true);
        finishAffinity();
    }


    /**
     * Overrides the onPause (activity lifecycle method)  that is called when the activity is paused
     */
    @Override
    public void onStop() {
        super.onStop();
        AudioUtils.pauseMusic(GameActivity.this); // mute the music
        if (!gameLogic.isGameFinished)
            gameLogic.pauseGame(gameId);
        Log.d("myDEBUG GameActivity", "onStop ");
    }


    /**
     * Overrides the onResume (activity lifecycle method)  that is called when the activity is resumed
     */
    @Override
    public void onResume() {
        super.onResume();
        gameLogic.resumeGame(gameId);
        AudioUtils.resumeMusicState(GameActivity.this); // mute the music
        Log.d("myDEBUG GameActivity", "onResume ");
    }


    /**
     * Overrides the onDestroy (activity lifecycle method)  that is called when the activity is destroyed
     * closes the activity and stops the game
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("mymyDEBUG GameActivity", "onDestroy ");
        if (isBrutalDestroy()) {
            GameLogic.notifyGameEnd(this, gameId);
        }
        finishAffinity();
        disableGameboard();
    }


}
