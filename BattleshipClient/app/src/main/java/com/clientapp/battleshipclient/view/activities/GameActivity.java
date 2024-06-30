package com.clientapp.battleshipclient.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.AttackResultEnum;
import com.clientapp.battleshipclient.logic.GameLogic;
import com.clientapp.battleshipclient.model.Game.GameStateEnum;
import com.clientapp.battleshipclient.model.GameBoard;
import com.clientapp.battleshipclient.model.Ship.OrientationEnum;
import com.clientapp.battleshipclient.model.Ship.Ship;
import com.clientapp.battleshipclient.model.Ship.ShipTypeEnum;
import com.clientapp.battleshipclient.model.Tile.TileStateEnum;
import com.clientapp.battleshipclient.model.User;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.view_utils.ClientMessages;
import com.clientapp.battleshipclient.view.view_utils.ExtrasEnum;
import com.clientapp.battleshipclient.view.view_utils.GameGridLayoutAdapter;
import com.clientapp.battleshipclient.view.view_utils.OnSwipeTouchListener;
import com.clientapp.battleshipclient.view.view_utils.PlacementUtils;
import com.clientapp.battleshipclient.view.view_utils.ShipsConverter;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;

/**
 * This class represents the GameActivity activity
 * It is responsible for the game play screen
 * Handles all the view parts of the game
 */
public class GameActivity extends BaseActivity {

    private GridView currPlayerGridView;
    private GridView opponentGridView;
    GameGridLayoutAdapter currPlayerGridLayoutAdapter;
    GameGridLayoutAdapter opponentGridLayoutAdapter;
    private GameBoard currPlayerGameBoard;  // contains the data of the tiles
    @Getter
    private GameBoard opponentGameBoard;  // contains the data of the tiles
    private String gameId;
    private GameLogic gameLogic;
    private TextView topMessageTextView;
    @Getter
    private TextView autoAttackMsgView;
    private FrameLayout opponentFrameLayout;
    private FrameLayout currPlayerFrameLayout;
    public CountDownTimer countDownTimer;
    private HashMap<ShipTypeEnum, ImageView> mapShipTypeToView = new HashMap<>();
    private boolean isOpponentGridClickable = true;

    private enum Location {BOTTOM, TOP}

    /*
     *  Overrides the onCreate method from the BaseActivity class
     *  initializes the game activity
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //helps reduce listening on swipes
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_game);
        Intent intent = this.getIntent();
        replaceMusic(AudioEnum.GAME_MUSIC);
        topMessageTextView = findViewById(R.id.turnMessageTextViewId);
        autoAttackMsgView = findViewById(R.id.msgTextViewId);
        setSwipeListener();
        setCountDownTimer();

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
         *
         */ //note to self

        // Method 2: Know when it is ready, and then use the view for whatever.
        currPlayerGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                currPlayerGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setAllShipViewsOnBoard();
            }
        });
        // use the same method for the other grid view too.
        opponentGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                opponentGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                View child = opponentGridView.getChildAt(0);
                if (child == null || child.isAttachedToWindow()){
                    return;
                }

                child.setVisibility(View.GONE);
                child.setVisibility(View.VISIBLE);
            }
        });

        startGame();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTag), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }// onCreate


    /*
     *  Sets the swipe listener on the main view
     * to enable the swipe gesture detection
     * */
    private void setSwipeListener() {
        findViewById(R.id.mainTag).setOnTouchListener(new OnSwipeTouchListener(this));
    }


    /**
     * Initializes the ships maps on the resource class
     * to be used for displaying the right ship on the grid
     */
    private void setShipResources() {//initialize the ships maps on the resources
        ShipsConverter.initNameToIdForBottomViews();
        ShipsConverter.initNameToIdForTopViews();
    }



    /**
     * Sets the click listeners on the opponent grid tiles
     */
    public void setupOpponentGridClickListeners() {
        opponentGridLayoutAdapter.setOnTileClickListener(new GameGridLayoutAdapter.OnTileClickListener() {
            @Override
            public void onTileClick(int position, View v) {
                if (!isOpponentGridClickable)
                    return;

                if (opponentGameBoard.getBoard().get(position).getState() != TileStateEnum.SEA)
                    return;

                //log tile view
                Log.d("myDEBUG GameActivity", "setOnClickListener attackOpponent: tileView: " + v);

                Log.d("myDEBUG GameActivity", "setOnClickListener attackOpponent: position: " + position);
                gameLogic.attackOpponent(position);
            }
        });
    }


    /**
     * Start point of the game
     * calls the gameLogic.getGame method via the runGameStatusChecks method
     */
    private void startGame() {
        gameLogic = new GameLogic(this, currPlayerGameBoard);
        Log.d("myDEBUG GameActivity", "in playGame");
        gameLogic.getGame();
    }


    /**
     * Stops the board from being clickable
     */
    public void disableGameboard() {
        isOpponentGridClickable = false;
    }


    /**
     * Shows the game over message
     *
     * @param isCurrPlayerWinner - true if the current player is the winner
     */
    public void displayFinalMessage(GameStateEnum gameState, Boolean isCurrPlayerWinner) {
        View messageVIew = findViewById(R.id.messageWrapperLayoutId);
        setDragListener(messageVIew);
        Button goToMenuButton = findViewById(R.id.backToMenuBtnId);
        String endGameMsg;
        switch (gameState) {
            case FINISHED:
                if (isCurrPlayerWinner) {
                    displayTopMessage(ClientMessages.SCORE_MESSAGE + gameLogic.getAttacksCounter());
                    endGameMsg = ClientMessages.WIN_MESSAGE;
                } else {
                    displayTopMessage(ClientMessages.GOOD_GAME_MESSAGE);
                    endGameMsg = ClientMessages.LOST_MESSAGE;
                }
                break;
            case ENDED:
                Log.d("myDEBUG ShowFinalMessage", "gameState: " + gameState);
                endGameMsg = ClientMessages.GAME_ABORTED;
                break;
            default:
                endGameMsg = ClientMessages.GAME_ENDED_CONNECTION;
        }
        Log.d("myDEBUG ShowFinalMessage", "endGameMsg: " + endGameMsg);
        ((TextView) messageVIew.findViewById(R.id.msgTopTextId)).setText(endGameMsg);
        messageVIew.setVisibility(View.VISIBLE);
        setBackToMenuButton(goToMenuButton);
    }

    private void setDragListener(View messageVIew) {
        messageVIew.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // When the user touches the view, we might want to do something here
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // When the user drags the view, we update the view's position
                        v.setTranslationX(event.getRawX() - (float) v.getWidth() / 2 - (float) v.getWidth() / 4);
                        v.setTranslationY(event.getRawY() - (float) v.getHeight() - (float) v.getHeight() / 2);
                        break;
                    case MotionEvent.ACTION_UP:
                        // When the user lifts their finger, we might want to do something here
                        break;
                }
                return true;
            }
        });
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


    /**
     * Overrides the goToMenuActivity method from the BaseActivity class
     * calls the super method and stops the game
     *
     * @param currentPlayer      - the current player
     * @param shouldReplaceMusic - true if the music should be replaced
     */
    @Override
    public void goToMenuActivity(User currentPlayer, Boolean shouldReplaceMusic) {
        super.goToMenuActivity(currentPlayer, shouldReplaceMusic);
        setBrutalDestroy(false);
        GameLogic.gameRunnablesHandler.removeCallbacksAndMessages(null);
    }


    /**
     * Sets the gameId from the game board
     */
    private void setGameId() {
        this.gameId = currPlayerGameBoard.getGameId();
        TextView gameIdDisplay = findViewById(R.id.gameId);
        gameIdDisplay.setText(gameId);
    }


    /**
     * Sets the current player game board from the intent
     *
     * @param intent - the intent that started the activity
     */
    private void setCurrentPlayerGameBoard(Intent intent) {
        Log.d("myDEBUG GameActivity", "setCurrentPlayerGameBoard: " + ExtrasEnum.GAME_BOARD.getName());
        currPlayerGameBoard = (GameBoard) intent.getSerializableExtra(ExtrasEnum.GAME_BOARD.getName());
        if (currPlayerGameBoard == null)
            Log.d("myDEBUG setCurrentPlayerGameBoard", "Error: currPlayerGameBoard is null");

    }


    /*
     *  Sets the ship view on the grid
     * */
    public void setAllShipViewsOnBoard() {
        ArrayList<Ship> shipList = currPlayerGameBoard.getShips();
        for (int i = 0; i < shipList.size(); i++) {
            ImageView shipView = mapShipTypeToView.get(shipList.get(i).getType());
            PlacementUtils.setShipViewOnGrid(shipList.get(i), shipView, currPlayerGridView);
            //log ship position
            Log.d("DEBUG GameGridLayoutAdapter", "setAllShipViewsOnBoard: ship position: " + shipList.get(i).getEdgePosition());
        }
    }


    /**
     * Sets the opponent game board - newly created board with 100 SEA tiles (top grid)
     * that through the game will be updated with the attacks results and ships.
     */
    private void setOpponentGameBoard() {
        opponentGameBoard = new GameBoard(null, gameId); // constructs a board with 100 SEA tiles
    }


    /**
     * Sets the board for wait. fogging its background and disabling the click listeners
     *
     * @param message - the message to be displayed at the top of the screen
     */
    public void disableBoardForAttack(String message) {
        stopCountdown();  // Stop any existing countdown first
        displayTopMessage(message);
        setOpponentFrameBackgroundOnAttack(false);
        disableGameboard();
    }


    /**
     * Sets the board for attack. clearing its background and setting the click listeners
     */
    public void enableBoardForAttack() {
//        startCountDownTimer();
        stopCountdown();
        if (countDownTimer == null) {
            setCountDownTimer();
        }
        countDownTimer.start();
        setOpponentFrameBackgroundOnAttack(true);
        isOpponentGridClickable = true;
    }

    /*
     * Starts the countdown timer for the current player
     * displays a message at the top of the screen
     */
    public void setCountDownTimer() {
        countDownTimer = new CountDownTimer(GameLogic.RANDOM_ATTACK_DELAY_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                topMessageTextView.setText(ClientMessages.YOUR_TURN + secondsLeft);
            }

            @Override
            public void onFinish() {
                Log.d("myDEBUG GameActivity", "attackOpponent on finish countdown");
                stopCountdown();
            }
        };
    }


    /*
     *  Stops the countdown timer
     */
    public void stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
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
//        Log.d("setOpponentGridView myDEBUG", "setupGridViews: opponentTilesData size: " + opponentGameBoard.getBoard().size());
        opponentGridLayoutAdapter = new GameGridLayoutAdapter(this, opponentGameBoard, opponentGridView);
        opponentGridView.setAdapter(opponentGridLayoutAdapter);

        setupOpponentGridClickListeners();
    }


    /**
     * Sets the current player board (the bottom grid)
     * using the currPlayerGameBoard data and a GameGridLayoutAdapter
     */
    private void setCurrentPlayerGridView() { //set grid from my board
        currPlayerGridView = findViewById(R.id.currPlayerBoardGridId);
        currPlayerFrameLayout = findViewById(R.id.shipsCurrPlayerFrameLayoutId);
        currPlayerGridLayoutAdapter = new GameGridLayoutAdapter(this, currPlayerGameBoard, currPlayerGridView);
        currPlayerGridView.setAdapter(currPlayerGridLayoutAdapter);
        setMapShipTypeToView(Location.BOTTOM);
//        Log.d("setCurrentPlayerGridView myDEBUG", "setupGridViews: currPlayerTilesData size: " + currPlayerGameBoard.getBoard().size());
    }


    /*
     *  Sets the map data structure that maps the ship name to the ship view
     * @param gridLocation - the location of the grid where the ship is placed
     * */
    private void setMapShipTypeToView(Location gridLocation) {
        ArrayList<Ship> shipList;
        if (gridLocation.equals(Location.BOTTOM))
            shipList = currPlayerGameBoard.getShips();
        else
            shipList = opponentGameBoard.getShips();
        for (int i = 0; i < shipList.size(); i++) {
            Ship ship = shipList.get(i);
            addShipViewToMap(ship, gridLocation);//
        }
    }


    /**
     * Adds the ship view to the map data structure
     *
     * @param ship         - the ship to be added
     * @param gridLocation - the location of the grid where the ship is placed
     */
    public void addShipViewToMap(Ship ship, Location gridLocation) {
        ShipTypeEnum shipType = ship.getType();
        int shipDrawableId;
        ImageView shipView;
        if (gridLocation.equals(Location.TOP)) {
            shipDrawableId = ShipsConverter.getTopShipIdByType(ShipTypeEnum.valueOf(shipType.toString()));
            shipView = opponentFrameLayout.findViewById(shipDrawableId);

        } else {
            shipDrawableId = ShipsConverter.getBottomShipIdByType(ShipTypeEnum.valueOf(shipType.toString()));
            shipView = currPlayerFrameLayout.findViewById(shipDrawableId);

        }
        PlacementUtils.shipViewSetOrientationAccordingToData(shipView, ship);
        shipView.setTag(ship);
        shipView.setVisibility(View.VISIBLE);
        mapShipTypeToView.put(shipType, shipView);
        Log.d("GameGridLayoutAdapter", "addShipViewToMap: shipName: " + shipType + " shipView: " + shipView);
    }


    /*
     *  Hides the ship view on the current player board
     *  this method is called when a ship is sunk (on the current player board)
     * */
    public void hideShip(String shipId) {
        View shipView = currPlayerFrameLayout.findViewById(Integer.parseInt(shipId));
        shipView.setVisibility(View.INVISIBLE);
    }


    /**
     * Displays the turn message at the top of the game screen
     *
     * @param turnMessage - the message to be displayed
     */
    public void displayTopMessage(String turnMessage) {
        Log.d("myDEBUG GameActivity", "displayTopMessage: " + turnMessage);
        topMessageTextView.setText(turnMessage);
    }


    /**
     * Updates the opponent board with the attack result
     *
     * @param attackPosition - the position of the attack
     * @param attackResult   - the result of the attack (hit, miss, sunk)
     * @param shipType       - the name of the ship that was sunk
     * @param orientation    - the orientation of the ship that was sunk
     * @param shipPosition   - the position of the ship that was sunk
     * @param state          - the state of the tile after the attack
     */
    public void updateOpponentBoard(int attackPosition, AttackResultEnum
            attackResult, ShipTypeEnum shipType, OrientationEnum orientation, Integer
                                            shipPosition, TileStateEnum state) {
        opponentGameBoard.updateTile(attackPosition, state);
        opponentGridLayoutAdapter.notifyDataSetChanged();
        if (attackResult.equals(AttackResultEnum.SUNK)) {
            showShipViewOnOpponentBoard(shipPosition, orientation, shipType);
        }
    }


    /**
     * updates the board view with the attack result
     */
    public void updateCurrentPlayerBoardView() {
//        Log.d("myDEBUG GameActivity", "updateCurrentPlayerBoard: ");
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
    public void showShipViewOnOpponentBoard(int position, OrientationEnum
            orientation, ShipTypeEnum shipType) {
        int shipViewId = ShipsConverter.getTopShipIdByType(shipType);
        int shipSize = ShipsConverter.getShipSizeByType(shipType);
        Ship tempShip = new Ship(shipViewId, shipSize, shipType, orientation);
        tempShip.setEdgePosition(position);
//        Log.d("myDEBUG GameActivity", "showShipViewOnBoard: tempShip: " + tempShip);
        addShipViewToMap(tempShip, Location.TOP);  // create a ship and add its view to the map that fits the top grid
        ImageView shipView = mapShipTypeToView.get(tempShip.getType());
        PlacementUtils.setShipViewOnGrid(tempShip, shipView, opponentGridView);
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
        GameLogic.isGameInProgress = false;
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
        if (GameLogic.isGameInProgress)
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
        Log.d("myDEBUG GameActivity", "onDestroy ");
        if (isBrutalDestroy()) {
            GameLogic.notifyGameEnd(this, gameId);
        }
        finishAffinity();
        disableGameboard();
    }
}
