package com.clientapp.battleshipclient.view.activities;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.AutoData;
import com.clientapp.battleshipclient.logic.GameLogic;
import com.clientapp.battleshipclient.logic.PlacementLogic;
import com.clientapp.battleshipclient.model.GameBoard;
import com.clientapp.battleshipclient.model.Ship.OrientationEnum;
import com.clientapp.battleshipclient.model.Ship.Ship;
import com.clientapp.battleshipclient.model.Ship.ShipTypeEnum;
import com.clientapp.battleshipclient.model.Tile.Tile;
import com.clientapp.battleshipclient.networking.NWutils.RequestEnum;
import com.clientapp.battleshipclient.networking.Netcom;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.view_utils.ClientMessages;
import com.clientapp.battleshipclient.view.view_utils.CustomDragShadowBuilder;
import com.clientapp.battleshipclient.view.view_utils.ExtrasEnum;
import com.clientapp.battleshipclient.view.view_utils.PlacementAdapter;
import com.clientapp.battleshipclient.view.view_utils.PlacementUtils;
import com.clientapp.battleshipclient.view.view_utils.ShipsConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/*
 *  This class is responsible for the activity in which the user arranges the ships on the gameboard.
 *  The user can drag and drop the ships on the grid, change their orientation and reset them.
 *  The user can also set the ships automatically.
 *  When the user is done, pressing the ready button sends the gameboard data to the server.
 * */
public class PlacementActivity extends BaseActivity {

    public final int MSG_DISPLAY_MILLIS = 2000;
    public final int TIME_UNTILL_COUNTDOWN = 30000;
    public final int TIME_OF_COUNTDOWN = 15500;
    public final long START_GAME_TIMEOUT_MILLIS = 50000;
    private GridView gridView;
    private PlacementLogic placementLogic;
    private PlacementAdapter gridLayoutAdapter;
    private FrameLayout shipsFrameLayout;
    private LinearLayout shipsInventoryLayout;
    private ConstraintLayout mainLayout;
    protected ArrayList<Tile> tilesList;  // contains the gameboard tiles data
    protected HashMap<Integer, Ship> shipCollection = new HashMap<>(); // contains the data of the ships
    @Getter
    protected HashMap<Integer, ImageView> shipViewsCollection = new HashMap<>(); // contains the views of the ships
    private ImageButton resetShipsButton;
    private ImageButton setShipsButton;
    private String gameId;
    private Button readyBtn;
    public static Runnable startGameTimeoutRunnableTask;
    public static Runnable finishPlacementWaitRunnable;

    public static Handler startGameTimeoutRunnablehandler = new Handler();
    TextView msgTextView;
    private boolean isDraggingShip = false;
    TextView gameIdDisplay;
    private CountDownTimer countDownTimer;


    public enum Target {
        GRID, INVENTORY
    }

    /*
     *  This method is called when the activity is created.
     *  It sets the activity's fields , the data and the views and listeners of activity's components
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);
        replaceMusic(AudioEnum.PLACE_SHIPS);

        mainLayout = findViewById(R.id.mainTag);
        msgTextView = findViewById(R.id.msgTextViewId);
        Intent intent = this.getIntent();
        gameId = intent.getStringExtra(ExtrasEnum.GAME_ID.getName());
//        gameIdDisplay = findViewById(R.id.gameId);
//        gameIdDisplay.setText(gameId);
        setFinishPlacementWaitTask();
        setUserFromIntent(intent);
        setShipsDataAndViews();  // set data and listeners for the ships
        setupGridView();   // set the adapter to the grid view
        setShipsLongClickListeners();
        setShipsOnClickOrientationChange();
        setInventoryDragListeners();
        setBackgroundDragListener();
        setResetShipsButtonOnClickListener();
        setCreateBoardTimeoutRunnable();
        setAutomaticSetShipsButtonOnClickListener();
        setReadyButtonOnClickListener();
        setBackToMenuButton();
        placementLogic = new PlacementLogic(this, gameId, getCurrentPlayer(), shipCollection, tilesList, gridLayoutAdapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTag), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //workaround android bug with gridview tile number 0
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                View child = gridView.getChildAt(0);
                if (child == null || child.isAttachedToWindow())
                    return;
                child.setVisibility(View.GONE);
                child.setVisibility(View.VISIBLE);
            }
        });
    }




    /**
     * Sets the Back to Menu button onClickListener
     */
    private void setBackToMenuButton() {
        Button backToMenuButton = findViewById(R.id.backToMenuBtnId);
        backToMenuButton.setOnClickListener(v -> {
            goToMenuActivity(getCurrentPlayer(), true);
            finishAffinity();
        });
    }

    /*
     *  This method sets a runnable task for the timeout of the placement of the ships
     *  if the ships are not placed in time, the game is ended
     *  and the user is navigated back to the menu activity
     * */
    private void setFinishPlacementWaitTask() {
        finishPlacementWaitRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d("myDEBUG", "finishPlacementWaitRunnable: " + "TIMEOUT START");
                Netcom.getInstance(null).getRequestQueue().cancelAll(RequestEnum.CREATE_BOARD.getName());
                Log.d("myDEBUG", "cancelAll on CREATE_BOARD ");
                startCountdown(TIME_OF_COUNTDOWN);
                displayMessageForShortTime(ClientMessages.HURRY_UP_MESSAGE);
            }
        };
        startGameTimeoutRunnablehandler.postDelayed(finishPlacementWaitRunnable, TIME_UNTILL_COUNTDOWN);
    }

    /*
     *  This method starts the countdown timer
     *  @param timeOfCountdown - the time of the countdown
     * */
    private void startCountdown(int timeOfCountdown) {
        stopCountdown();
        countDownTimer = new CountDownTimer(TIME_OF_COUNTDOWN, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                Log.d("myDEBUG GameActivity", "attackOpponent on tick countdown" + secondsLeft);
                msgTextView.setVisibility(View.VISIBLE);
                msgTextView.setText(ClientMessages.HURRY_UP_MESSAGE + secondsLeft);
            }

            @Override
            public void onFinish() {
                Log.d("myDEBUG GameActivity", "attackOpponent on finish countdown");
                //display a message for 1 second
                stopCountdown();
                msgTextView.setText(ClientMessages.TIMEOUT_MESSAGE);
                Netcom.getInstance(null).getRequestQueue().cancelAll(RequestEnum.CREATE_BOARD.getName());
                goToMenuActivity(getCurrentPlayer(), true);
            }
        }.start();
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



    /*
     * Sets the onClickListener for the ready button.
     * Onclick, all the gameboard data is sent to the server
     * */
    private void setReadyButtonOnClickListener() {
        readyBtn = findViewById(R.id.ReadyBtnId);
        readyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!placementLogic.areAllShipsPlaced()) {
                    displayMessageForShortTime(ClientMessages.SHIPS_NOT_PLACED_MESSAGE);
                } else {
                    stopCountdown();
                    startGameTimeoutRunnablehandler.removeCallbacks(finishPlacementWaitRunnable);
                    disableShipsClickListeners();
                    placementLogic.prepareGameBoardForSending(getCurrentPlayer(), gameId, tilesList, shipCollection);
                    waitForStartGame();
                    disableButtons();
                }
            }
        });
    }

    /*
     *  This method disables the click listeners for the ships
     * */
    private void disableShipsClickListeners() {
        for (Map.Entry<Integer, ImageView> entry : shipViewsCollection.entrySet()) {
            ImageView shipView = entry.getValue();
            shipView.setLongClickable(false);
            shipView.setClickable(false);
        }
    }


    /*
     *  This method displays a message on the screen for a short time
     *  Used only for ships placement messages
     * @param msg - the message to be displayed
     * */
    public void displayMessageForShortTime(String msg) {
        msgTextView.setText(msg);
        msgTextView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                msgTextView.setVisibility(View.INVISIBLE);
            }
        }, MSG_DISPLAY_MILLIS);
    }



    /*
     *  This method disables the buttons on the screen
     * */
    private void disableButtons() {
        readyBtn.setClickable(false);
        resetShipsButton.setClickable(false);
        setShipsButton.setClickable(false);
    }


    /*
     * This method displays a message about the game starting
     * and sets a timeout for the game to start
     * if the game does not start in time, the game is ended
     * and the user is navigated back to the menu activity
     */
    private void waitForStartGame() {
        msgTextView.setText(ClientMessages.STARTING_GAME_MESSAGE);
        msgTextView.setVisibility(View.VISIBLE);
        Log.d("myDEBUG", "waitForStartGame message displayed: " + "TIMEOUT START");
        startGameTimeoutRunnablehandler.postDelayed(startGameTimeoutRunnableTask, START_GAME_TIMEOUT_MILLIS);
    }


    /*
     *  This method sets the runnable task for the timeout of the game start
     *  if the game does not start in time, the game is ended
     *  and the user is navigated back to the menu activity
     */
    public void setCreateBoardTimeoutRunnable() {
        startGameTimeoutRunnableTask = new Runnable() {
            @Override
            public void run() {
                Log.d("myDEBUG", "startGameTimeoutRunnableTask: " + "TIMEOUT START");
                Netcom.getInstance(null).getRequestQueue().cancelAll(RequestEnum.CREATE_BOARD.getName());
                Log.d("myDEBUG", "cancelAll on CREATE_BOARD ");
                GameLogic.notifyGameEnd(PlacementActivity.this, gameId);
                displayMessageForShortTime(ClientMessages.OPPONENT_LEFT_MESSAGE);
                goToMenuActivity(getCurrentPlayer(), true);
            }
        };
    }


    /*
     *  This method sets the onClickListener for the reset ships button
     *  if the button is clicked, all the ships are returned to their inventory
     */
    private void setResetShipsButtonOnClickListener() {
        resetShipsButton = findViewById(R.id.resetShipsID);
        resetShipsButton.setOnClickListener(v -> {
            resetInventory();
        });
    }


    /*
     *  This method sets the drag listeners for the background
     *  if the ship is dropped inside the grid in a legit position, it is placed on the grid
     *  otherwise it is returned to its place in the inventory
     */
    private void setBackgroundDragListener() {
        mainLayout.setOnDragListener((v, event) -> {
                    Ship ship = (Ship) event.getLocalState(); // state of the dragged view
                    int shipInventoryId = ship.getBottomViewId();
                    ImageView shipView = shipViewsCollection.get(shipInventoryId); // extract ship ID from ship;

            Log.d("myDEBUG PlacementActivity", "onDragEvent shipId: " + shipInventoryId + ", action: " + event.getAction());

                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            shipView.setVisibility(View.INVISIBLE);
                            return true;
                        case DragEvent.ACTION_DRAG_LOCATION:
                            return true;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            return true;
                        case DragEvent.ACTION_DROP:
                            boolean isInsideGrid = isInsideTargetArea(shipView, gridView, Target.GRID, event);
                            if (!isInsideGrid) {
                                returnShipToInventory(ship);
                                return true;
                            }
                            return false; //drag operation was not handled yet
                        case DragEvent.ACTION_DRAG_ENDED:
                            if (!event.getResult()) { // If drop was not successful Return the ship to its inventory position
                                returnShipToInventory(ship);
                            }
                            isDraggingShip = false;
                        case DragEvent.ACTION_DRAG_EXITED:
                            return true;
                        default:
                            break;
                    }//end of switch
                    return true;
                } //end of onDragListener
        ); //end of setOnDragListener
    }


    /*
     *  This method returns the ship to its inventory position
     *  if the ship is dropped outside the grid
     *  @param ship - the ship to be returned to the inventory
     */
    public void returnShipToInventory(Ship ship) {
        ship.resetShip();
        getShipViewsCollection().get(ship.getTopViewId()).setRotation(0);
        shipViewsCollection.get(ship.getTopViewId()).setVisibility(View.INVISIBLE);
        shipViewsCollection.get(ship.getBottomViewId()).setVisibility(View.VISIBLE);
    }


    /*
     *  This method sets the drag listeners for the inventory area
     *  if the ship is dropped inside this area, it is returned to its place
     */
    private void setInventoryDragListeners() {
        shipsInventoryLayout.setOnDragListener((v, event) -> {
                    Ship ship = (Ship) event.getLocalState(); // state of the dragged view
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            findViewById(ship.getBottomViewId()).setVisibility(View.INVISIBLE);
                            return true;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            return true;
                        case DragEvent.ACTION_DRAG_LOCATION:
                            return true;
                        case DragEvent.ACTION_DROP:
                            returnShipToInventory(ship);
                            return true;
                        case DragEvent.ACTION_DRAG_ENDED:
                            v.setVisibility(View.VISIBLE);
                            return true;
                        default:
                            break;
                    }//end of switch
                    return true;
                } //end of onDragListener
        ); //end of setOnDragListener
    }


    /*
     *  This method checks if the ship is inside the target view
     */
    private boolean isInsideTargetArea(ImageView shipView, View targetView, Target target, DragEvent event) {
        if (shipView == null) {
            return false;
        }
        if (target == Target.GRID) {
            targetView = gridView;
        } else if (target == Target.INVENTORY) {
            targetView = shipsInventoryLayout;
        }

        int[] targetViewLocation = new int[2]; // 2 for  x, y
        targetView.getLocationOnScreen(targetViewLocation);

        int shipViewX = (int) event.getX();
        int shipViewY = (int) event.getY();
        int targetViewX = targetViewLocation[0];
        int targetViewY = targetViewLocation[1];

        return shipViewX >= targetViewX && shipViewY >= targetViewY
                && shipViewX + shipView.getWidth() <= targetViewX + targetView.getWidth()
                && shipViewY + shipView.getHeight() <= targetViewY + targetView.getHeight();
    }


    /*
     *  This method sets the long click listeners for the ships
     *  if a ship is long clicked, it is draggable
     */
    public void setShipsLongClickListeners() {
        for (Map.Entry<Integer, ImageView> entry : shipViewsCollection.entrySet()) {
            ImageView shipView = entry.getValue();

            shipView.setOnLongClickListener(v -> {
                Log.d("myDEBUG PlacementActivity", "onLongClick shipId: " + entry.getKey() + ", mIsDraggingShip: " + isDraggingShip);

                if (isDraggingShip)
                    return false;

                int shipId = entry.getKey();
                Ship ship = shipCollection.get(shipId);
                shipView.setTag(ship); // Make sure the ship object is set as tag if not set elsewhere
                Log.d("myDEBUG PlacementActivity", "setShipsLongClickListeners shipId: " + shipId);
                Log.d("myDEBUG PlacementActivity", "setShipsLongClickListeners shipPositionsArray: " + ship.getShipPositionsArray());

                ClipData.Item item = new ClipData.Item(String.valueOf(shipId));
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData dragData = new ClipData("Ship ID", mimeTypes, item);
                if (dragData == null) {
                    return false;
                }
                CustomDragShadowBuilder shipShadow = new CustomDragShadowBuilder(v);
//                   Returning false means the system was unable to do a drag
//                  because of another ongoing operation
//                  or some other reasons.
                if (!v.startDragAndDrop(dragData, shipShadow, ship, 0))
                    return false;

                isDraggingShip = true;
                v.setVisibility(View.INVISIBLE); // Temporarily hide the view while dragging
                return true;
            });//end of setOnLongClickListener
        } //end of for loop
    }// end of setShipsLongClickListeners




    /*
     *  This method sets the onClickListeners for the ships
     *  if a ship is clicked, its orientation is changed
     */
    private void setShipsOnClickOrientationChange() {
        for (Map.Entry<Integer, ImageView> entry : shipViewsCollection.entrySet()) {
            int shipId = entry.getKey();
            if (idInInventory(shipId)) {
                continue; // do not change orientation of ships in inventory
            }
            ImageView shipView = entry.getValue();
            Ship ship = shipCollection.get(shipId);
            shipView.setOnClickListener(v -> {
                shipView.setTag(ship); // Make sure the ship object is set as tag if not set elsewhere

                boolean isChanged = placementLogic.orientationChangeLogic(ship);
                if (isChanged) {
                    PlacementUtils.shipViewSetOrientationAccordingToData(shipView, ship);
                    PlacementUtils.setShipViewOnGrid(ship, shipView, gridView);
                } else {
                    //log no orientation change
                    Log.d("myDEBUG", "setShipsOnClickOrientationChange: " + "no orientation change");
                    displayMessageForShortTime(ClientMessages.TRY_ANOTHER_PLACE);
                }

            });
        }
    }


    /*
     *  Checks if the ship is in the inventory according to its id
     */
    private boolean idInInventory(Integer key) {
        Ship ship = shipCollection.get(key);
        return ship.getBottomViewId() == key;
    }


    /*
     * This method is sets the empty gridview with tiles and adapter
     */
    private void setupGridView() {  //called by onCreate
        gridView = findViewById(R.id.currPlayerBoardGridId); // get the reference of grid view
        tilesList = PlacementUtils.populateWithTiles(); // populate the grid with tiles
        gridLayoutAdapter = new PlacementAdapter(this, tilesList);
        gridLayoutAdapter.setTileDragListener(new PlacementAdapter.OnTileDragListener() {
            @Override
            public boolean onTileDrag(View tileView, DragEvent event, int position) {
                Ship ship = (Ship) event.getLocalState();
                return placementLogic.handleTileDragEvents(PlacementActivity.this, event, ship, position);
            }
        });
        gridView.setAdapter(gridLayoutAdapter); // set the adapter to the grid view
    }


    /*
     * This method sets the initializes data of the ships and their views
     */
    private void setShipsDataAndViews() {
        shipsInventoryLayout = findViewById(R.id.shipsInventoryId);
        shipsFrameLayout = findViewById(R.id.shipsFrameLayoutId);
        ArrayList<Integer> idsOfShipsInInventory = setArrayOfIds(shipsInventoryLayout);
        ArrayList<Integer> idsOfShipsInFrame = setArrayOfIds(shipsFrameLayout);

        for (int i = 0; i < ShipsConverter.SHIP_COUNT; i++) {
            Integer idInInventory = idsOfShipsInInventory.get(i);
            Integer idInFrame = idsOfShipsInFrame.get(i);
            ShipTypeEnum shipName = ShipTypeEnum.values()[i];
            Ship ship = new Ship(idInInventory, idInFrame, ShipsConverter.getShipSizeByType(shipName), shipName);

//              put all ship data on one collection, on both inventoryId and frameId:
//              (double set for convenience) more comfortable than using a converter

            shipCollection.put(idInInventory, ship);
            shipCollection.put(idInFrame, ship);

            // add all shipViews to one collection
            shipViewsCollection.put(idInInventory, (ImageView) findViewById(idInInventory));
            shipViewsCollection.put(idInFrame, (ImageView) findViewById(idInFrame));
        }
    }


    /*
     * This method creates an arraylist of id's of all ships in the inventory
     */
    private ArrayList<Integer> setArrayOfIds(ViewGroup shipsInventoryLayout) {
        ArrayList<Integer> idsOfShipViews = new ArrayList<>();
        for (int i = 0; i < shipsInventoryLayout.getChildCount(); i++) { // to avoid the reset button
            View child = shipsInventoryLayout.getChildAt(i);
            if (child.getClass().equals(AppCompatImageView.class)) {
                idsOfShipViews.add(child.getId());
            }
        }
        return idsOfShipViews;
    }


    /**
     * This method resets the inventory of the ships and pull them all back down from the gameboard
     */
    public void resetInventory() {
        if (shipCollection.isEmpty()) {
            return;
        }
        for (Map.Entry<Integer, Ship> entry : shipCollection.entrySet()) {
            Ship ship = entry.getValue();
            int shipInventoryId = ship.getBottomViewId();
            int shipFrameId = ship.getTopViewId();
            ImageView shipViewInInventory = shipViewsCollection.get(shipInventoryId);
            ImageView shipViewInFrame = shipViewsCollection.get(shipFrameId);
            ship.resetShip();
            PlacementUtils.shipViewSetOrientationAccordingToData(shipViewInFrame, ship);
            shipViewInFrame.setVisibility(View.INVISIBLE);
            shipViewInInventory.setVisibility(View.VISIBLE);
        }
        placementLogic.resetAllTilesData();
        placementLogic.refreshTiles();
    }


    /*
     *  Sets the onClickListener for the automatic placement button
     * if the button is clicked, the ships are placed automatically
     * in one of the two possible placements
     * * */
    private void setAutomaticSetShipsButtonOnClickListener() {
        setShipsButton = findViewById(R.id.setShipsID);
        Log.d("myDEBUG automatic placement", "setShips: " + setShipsButton);
        setShipsButton.setOnClickListener(v -> {
            resetInventory();
            Log.d("myDEBUG automatic placement", "resetInventory");
            int random = (int) (Math.random() * 1000);
            GameBoard autoGameBoard;
            if (random % 2 == 0) {
                autoGameBoard = AutoData.createGameBoard(getCurrentPlayer(), currentPlayer.getId(), gameId, "board1", OrientationEnum.HORIZONTAL);
            } else {
                autoGameBoard = AutoData.createGameBoard(getCurrentPlayer(), currentPlayer.getId(), gameId, "board2", OrientationEnum.VERTICAL);
            }
            tilesList = autoGameBoard.getBoard();
            placementLogic.refreshTiles();
            shipCollection.clear();
            for (Ship ship : autoGameBoard.getShips()) {
                shipCollection.put(ship.getBottomViewId(), ship);
                shipCollection.put(ship.getTopViewId(), ship);
                PlacementUtils.setShipViewOnGrid(ship, shipViewsCollection.get(ship.getTopViewId()), gridView);
                View inventoryShipView = shipViewsCollection.get(ship.getBottomViewId());
                inventoryShipView.setVisibility(View.INVISIBLE);
            }
            Log.d("myDEBUG automatic placement", "setShips: " + shipCollection);
            placementLogic.setShipCollection(shipCollection); //update collection in the logic layer
            placementLogic.setTilesList(tilesList); //update collection in the logic layer
            placementLogic.setAllTilesData(shipCollection);
            gridLayoutAdapter.setTilesList(tilesList); //update tilesList in adapter
            placementLogic.refreshTiles();
            setShipsOnClickOrientationChange();
        });
    }


    /*
     * Navigates to the next activity in which the game is played
     */
    public void goToGameActivity(GameBoard gameBoard) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(ExtrasEnum.GAME_BOARD.getName(), gameBoard);
//        setBrutalDestroy(false);
        try {
            this.startActivity(intent);
        } catch (Exception e) {
            Log.d("PlaceYourShips DEBUG", "goToGameActivity: " + "Error: " + e.getMessage());
        }
    }


    /*
     *  Ends the game and navigates back to the menu activity
     * */
    @Override
    protected void exit() {
        stopCountdown();
        GameLogic.notifyGameEnd(this, gameId);
        GameLogic.isGameInProgress = false; //notify the game is aborted
        goToMenuActivity(getCurrentPlayer(), true);
        finishAffinity();
    }

    /*
     * Overrides the onPause method to pause the music when the activity is paused
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d("DEBUG BaseActivity", "onPause: ");
        AudioUtils.pauseMusic(this);
    }


    /*
     * Overrides the onResume method to pause the music when the activity is paused
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d("DEBUG placementActivity", "onResume: ");
        AudioUtils.resumeMusicState(PlacementActivity.this); // mute the music
    }


    /*
     *  Overrides the onDestroy method to log the activity's destruction
     *  and to cancel the runnable task for the game start timeout     *
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DEBUG placementActivity", "onDestroy: ");
        finishAffinity();

    }
}