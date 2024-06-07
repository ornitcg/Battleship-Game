package com.clientapp.battleshipclient.view.activities;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.clientapp.battleshipclient.logic.ArrangeGameboardLogic;
import com.clientapp.battleshipclient.logic.GameLogic;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.utils.TestData;
import com.clientapp.battleshipclient.view.UI_utils.ArrangementGridLayoutAdapter;
import com.clientapp.battleshipclient.view.UI_utils.CustomDragShadowBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
*  This class is responsible for the activity in which the user arranges the ships on the gameboard.
*  The user can drag and drop the ships on the grid, change their orientation and reset them.
*  The user can also set the ships automatically.
*  When the user is done, pressing the ready button sends the gameboard data to the server.
* */
public class ArrangeGameBoardActivity extends BaseActivity {

    private final String OPPONENT_LEFT_MESSAGE = "OPONENT LEFT GAME";
    private final String SHIPS_NOT_PLACED_MESSAGE = "SOME SHIPS ARE NOT PLACED";
    private final String STARNING_GAME_MESSAGE = "STARTING GAME";
    private final int MSG_DISPLAY_MILLIS = 2000;
    private GridView gridView;
    private ArrangeGameboardLogic arrangeGameboardLogic;
    private ArrangementGridLayoutAdapter gridLayoutAdapter;
    private FrameLayout shipsFrameLayout;
    private LinearLayout shipsInventoryLayout;
    private ConstraintLayout mainLayout;
    protected ArrayList<Tile> tilesList;  // contains the gameboard tiles data
    protected HashMap<Integer, Ship> shipCollection = new HashMap<>(); // contains the data of the ships
    protected HashMap<Integer, ImageView> shipViewsCollection = new HashMap<>(); // contains the views of the ships
    private ImageButton resetShipsButton;
    private ImageButton setShipsButton;
    private String gameId;
    private Button readyBtn;
    private View messageView;

    public Runnable startGameTimeoutRunnableTask;
    public static Handler startGameTimeoutRunnablehandler = new Handler();
    TextView msgTextView;
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
        setContentView(R.layout.activity_arrange_board);

        mainLayout = findViewById(R.id.mainTag);
        msgTextView = findViewById(R.id.msgTextViewId);
        Intent intent = this.getIntent();
        gameId = intent.getStringExtra("gameId");
        setUserFromIntent(intent);
        replaceMusic(AudioEnum.ARRANGE_GAMEBOARD);
        setShipsDataAndViews();  // set data and listeners for the ships
        setupGridView();   // set the adapter to the grid view
        setShipsLongClickListeners();
        setShipsOnClickOrientationChange();
        setInventoryDragListeners();
        setBackgroundDragListener();
        setResetShipsButtonOnClickListener();
        setStartGameTimeoutRunnable();
        setAutomaticSetShipsButtonOnClickListener();
        setReadyButtonOnClickListener();
        setBackToMenuView();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTag), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    /**
     * Sets the Back to Menu button onClickListener
     */
    private void setBackToMenuView() {
        messageView = findViewById(R.id.messageWrapperLayoutId);
        Button backToMenuButton = findViewById(R.id.backToMenuBtnId);
        backToMenuButton.setOnClickListener(v -> {
            goToMenuActivity(getCurrentPlayer(), true);
            finishAffinity();
        });
    }


    /*
     *  Ends the game and navigates back to the menu activity
     * */
    @Override
    protected void exit() { //TODO carefaul calling this method
        GameLogic.notifyGameEnd(this, gameId);
        goToMenuActivity(getCurrentPlayer(), true);
        finishAffinity();
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
                if (!ArrangeGameboardLogic.areAllShipsPlaced(shipCollection)) {
                    displayMessageForShortTime(SHIPS_NOT_PLACED_MESSAGE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideMessage();
                        }
                    }, MSG_DISPLAY_MILLIS);
                } else {
                    arrangeGameboardLogic.prepareGameBoardForSending( ArrangeGameBoardActivity.this, getCurrentPlayer() , gameId , tilesList, shipCollection);
                    displayWaitingMessage();
                    disableButtons();
                }
            }
        });
    }

    private void hideMessage() {
        msgTextView.setVisibility(View.INVISIBLE);
    }

    private void disableButtons() {
        readyBtn.setClickable(false);
        resetShipsButton.setClickable(false);
        setShipsButton.setClickable(false);
    }


    /*
     * */
    private void displayWaitingMessage() {
        msgTextView.setText(STARNING_GAME_MESSAGE);
        msgTextView.setVisibility(View.VISIBLE);
        startGameTimeoutRunnablehandler.postDelayed(startGameTimeoutRunnableTask, ArrangeGameboardLogic.START_GAME_TIMEOUT_MILLIS);
    }

    public void setStartGameTimeoutRunnable() {
        startGameTimeoutRunnableTask = new Runnable() {
            @Override
            public void run() {
                //log in task
                Log.d("DEBUG", "startGameTimeoutRunnableTask: " + "TIMEOUT");
                ArrangeGameboardLogic.cancelRequests(ArrangeGameBoardActivity.this);
                GameLogic.notifyGameEnd(ArrangeGameBoardActivity.this, gameId);
                displayMessageForShortTime(OPPONENT_LEFT_MESSAGE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideMessage();
                        goToMenuActivity(getCurrentPlayer(), true);
                    }
                }, MSG_DISPLAY_MILLIS);
            }
        };
    }


    /*
     *  This method displays a message about the ships not being fully placed
     *  for 1 second
     * */
    private void displayMessageForShortTime(String message) {
        msgTextView.setText(message);
        msgTextView.setVisibility(View.VISIBLE);
        return;
    }


    /*
     *  This method sets the onClickListener for the reset ships button
     *  if the button is clicked, all the ships are returned to their inventory
     * */
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
     * */
    private void setBackgroundDragListener() {
        mainLayout.setOnDragListener((v, event) -> {
                    Ship ship = (Ship) event.getLocalState(); // state of the dragged view
                    int shipInventoryId = ship.getBottomViewId();
                    ImageView shipView = shipViewsCollection.get(shipInventoryId); // extract ship ID from ship;
                    int shipFrameId = ship.getTopViewId();

                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
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
//                        case DragEvent.ACTION_DRAG_EXITED:
                        default:
                            break;
                    }//end of switch
                    return true;
                } //end of onDragListener
        ); //end of setOnDragListener
    }


    /*
     *  This method returns the ship to its inventory position
     * */
    public void returnShipToInventory(Ship ship) {
        ship.resetShip();
        shipViewsCollection.get(ship.getTopViewId()).setVisibility(View.INVISIBLE);
        shipViewsCollection.get(ship.getBottomViewId()).setVisibility(View.VISIBLE);
    }


    /*
     *  This method sets the drag listeners for the inventory area
     *  if the ship is dropped inside this area, it is returned to its place
     * */
    private void setInventoryDragListeners() {
        shipsInventoryLayout.setOnDragListener((v, event) -> {
                    Ship ship = (Ship) event.getLocalState(); // state of the dragged view
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
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
     * */
    private boolean isInsideTargetArea(ImageView shipView, View targetView, Target target, DragEvent event) {
        if (shipView == null) {
            return false;
        }
        if (target == Target.GRID) {
            targetView = gridView;
        } else if (target == Target.INVENTORY) {
            targetView = shipsInventoryLayout;
        }

        int[] targetViewLocation = new int[2]; // x, y
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
     * */
    private void setShipsLongClickListeners() {
        // iterate on all image views of the ships (inventory and frame)
        for (Map.Entry<Integer, ImageView> entry : shipViewsCollection.entrySet()) {
            ImageView shipView = entry.getValue();

            shipView.setOnLongClickListener(v -> {
                int shipId = entry.getKey();
                Ship ship = shipCollection.get(shipId);
                shipView.setTag(ship); // Make sure the ship object is set as tag if not set elsewhere
                Log.d("myDEBUG ArrangeGameBoardActivity", "setShipsLongClickListeners shipId: " + shipId);
                //log ship positions array
                Log.d("myDEBUG ArrangeGameBoardActivity", "setShipsLongClickListeners shipPositionsArray: " + ship.getShipPositionsArray());


                ClipData.Item item = new ClipData.Item(String.valueOf(shipId));
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData dragData = new ClipData("Ship ID", mimeTypes, item);
                if (dragData == null) {
                    return false;
                }
                CustomDragShadowBuilder shipShadow = new CustomDragShadowBuilder(v);
                v.startDragAndDrop(dragData, shipShadow, ship, 0);
                v.setVisibility(View.INVISIBLE); // Temporarily hide the view while dragging
                return true;
            });//end of setOnLongClickListener
        } //end of for loop
    }// end of setShipsLongClickListeners


    /*
     *  This method sets the onClickListeners for the ships
     *  if a ship is clicked, its orientation is changed
     * */
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
                ship.setPlaced(false);
                ship.changeOrientation(); //change the orientation of the ship data

                Log.d("myDEBUG", "setShipsOnClickOrientationChange shipCollection before change: " + shipCollection);
                Log.d("myDEBUG", "setShipsOnClickOrientationChange shipPositionsArray  before orientation change: " + ship.getShipPositionsArray());


                shipViewSetOrientationAccordingToData(shipView, ship);

//                Log.d("myDEBUG", "setShipsOnClickOrientationChange shipSize: " + ship.getSize());
                Log.d("myDEBUG", "setShipsOnClickOrientationChange shipPositionsArray after orientation change:: " + ship.getShipPositionsArray());
//                Log.d("myDEBUG", "setShipsOnClickOrientationChange shipCollection after change: " + shipCollection);

                int rotateAxisTilePosition = ship.getShipPositionsArray().get(ship.getSize() / 2); // get position
                ship.forgetPositions();
                ArrangeGameboardLogic.resetAllTilesData(tilesList);
                arrangeGameboardLogic.setAllTilesData(tilesList, shipCollection);
                gridLayoutAdapter.notifyDataSetChanged();
                gridLayoutAdapter.setShipNewPositions(ship, rotateAxisTilePosition);

                //log ship positions
                Log.d("myDEBUG", "setShipsOnClickOrientationChange shipPositionsArray: " + ship.getShipPositionsArray());
                Log.d("myDEBUG", "setShipsOnClickOrientationChange Orientation changed to: " + ship.getOrientation());
                if (gridLayoutAdapter.isValidForDrop(ship)) {
                    ship.setPlaced(true);
                    //log ship orientation
                    Log.d("DEBUG", "Orientation changed to: " + ship.getOrientation());
                    ArrangeGameboardLogic.setTilesWithShipAndStatus(ship, tilesList);
                    gridLayoutAdapter.setShipViewOnGrid(ship, gridView);
                    ArrangeGameboardLogic.setNearShip(ship, tilesList);
                    gridLayoutAdapter.notifyDataSetChanged();
                } else { // if the ship is not valid for drop - returning to its previous data
                    ship.changeOrientation(); //change back
                    shipViewSetOrientationAccordingToData(shipView, ship);
                    ArrangeGameboardLogic.resetAllTilesData(tilesList);
                    gridLayoutAdapter.setShipNewPositions(ship, rotateAxisTilePosition);
                    arrangeGameboardLogic.setAllTilesData(tilesList, shipCollection);
                    ArrangeGameboardLogic.setNearShip(ship, tilesList);
                    gridLayoutAdapter.notifyDataSetChanged();
                    ship.setPlaced(true);
                }

            });
        }
    }


    /*
     *  This method changes the orientation of the ship view according to the ship data
     * */
    private void shipViewSetOrientationAccordingToData(ImageView shipView, Ship ship) {
        OrientationEnum orientation = ship.getOrientation();
        switch (orientation) { // chnage the orientation of the ship view
            case HORIZONTAL:
                shipView.setRotation(90);
                break;
            case VERTICAL:
                shipView.setRotation(0);
                break;
        }
    }


    /*
     *  Checks if the ship is in the inventory according to its id
     * */
    private boolean idInInventory(Integer key) {
        Ship ship = shipCollection.get(key);
        return ship.getBottomViewId() == key;
    }


    /**
     * This method is sets the empty gridview with tiles and adapter
     */
    private void setupGridView() {  //called by onCreate
        gridView = findViewById(R.id.currPlayerBoardGridId); // get the reference of grid view
        tilesList = ArrangeGameboardLogic.populateWithTiles(); // populate the grid with tiles
        gridLayoutAdapter = new ArrangementGridLayoutAdapter(this, tilesList, shipViewsCollection, shipCollection, shipsFrameLayout);
        gridView.setAdapter(gridLayoutAdapter); // set the adapter to the grid view
    }


    /*
     * This method sets the initializes data of the ships and their views
     * */
    private void setShipsDataAndViews() {
        shipsInventoryLayout = findViewById(R.id.shipsInventoryId);
        shipsFrameLayout = findViewById(R.id.shipsFrameLayoutId);
        ArrayList<Integer> idsOfShipsInInventory = setArrayOfIds(shipsInventoryLayout);
        ArrayList<Integer> idsOfShipsInFrame = setArrayOfIds(shipsFrameLayout);
        // log the size of the idsOfShipsInInventory

        for (int i = 0; i < ShipsResources.SHIP_COUNT; i++) {
            Integer idInInventory = idsOfShipsInInventory.get(i);
            Integer idInFrame = idsOfShipsInFrame.get(i);
            ShipTypeEnum shipName = ShipTypeEnum.values()[i];
            Ship ship = new Ship(idInInventory, idInFrame, ShipsResources.getShipSizeByType(shipName), shipName);
            /*
             * put all ship data on one collection, on both inventoryId and frameId:
             * (double set for convenience) more comfortable than using a converter
             */
            shipCollection.put(idInInventory, ship);
            shipCollection.put(idInFrame, ship);

            // add all shipviews to one collection
            shipViewsCollection.put(idInInventory, (ImageView) findViewById(idInInventory));
            shipViewsCollection.put(idInFrame, (ImageView) findViewById(idInFrame));
        }
    }


    /**
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
            shipViewSetOrientationAccordingToData(shipViewInFrame, ship);
            shipViewInFrame.setVisibility(View.INVISIBLE);
            shipViewInInventory.setVisibility(View.VISIBLE);
        }
        ArrangeGameboardLogic.resetAllTilesData(tilesList);
        gridLayoutAdapter.notifyDataSetChanged();
    }





    private void setAutomaticSetShipsButtonOnClickListener() {
        setShipsButton = findViewById(R.id.setShipsID);
        Log.d("myDEBUG automatic placement", "setShips: " + setShipsButton);
        setShipsButton.setOnClickListener(v -> {
            resetInventory();
            Log.d("myDEBUG automatic placement", "resetInventory");
            int random = (int) (Math.random() * 1000);
            GameBoard autoGameBoard;
            if (random % 2 == 0) {
                autoGameBoard = TestData.createGameBoard(getCurrentPlayer(), currentPlayer.getId(), gameId, "board1", OrientationEnum.HORIZONTAL);
            } else {
                autoGameBoard = TestData.createGameBoard(getCurrentPlayer(), currentPlayer.getId(), gameId, "board1", OrientationEnum.VERTICAL);
            }
//            Log.d("myDEBUG automatic placement", "autoGameBoard: " + autoGameBoard);
            tilesList = autoGameBoard.getBoard();
//            Log.d("myDEBUG automatic placement", "setShips: tilesList: " + tilesList);
            gridLayoutAdapter.notifyDataSetChanged();
            //loop on gameboard.getShips() and add them to shipCollection
            shipCollection.clear();
            for (Ship ship : autoGameBoard.getShips()) {
                shipCollection.put(ship.getBottomViewId(), ship);
                shipCollection.put(ship.getTopViewId(), ship);
//                Log.d("myDEBUG automatic placement", "setShips: ship positions array: " + ship.getShipPositionsArray());
                gridLayoutAdapter.setShipViewOnGrid(ship, gridView);
                View inventoryShipView = shipViewsCollection.get(ship.getBottomViewId());
                inventoryShipView.setVisibility(View.INVISIBLE);
            }
            gridLayoutAdapter.setShipCollection(shipCollection); //update collection in adapter
            ArrangeGameboardLogic.setAllTilesData(tilesList, shipCollection);
            ///log tiles
//            Log.d("myDEBUG automatic placement", "setShips: tilesList: " + tilesList);
            gridLayoutAdapter.setTilesList(tilesList); //update tilesList in adapter
            gridLayoutAdapter.notifyDataSetChanged();
            setShipsOnClickOrientationChange();
//            Log.d("myDEBUG automatic placement", "setShips: shipCollection: " + shipCollection);
        });
    }


    /*
     * Navigates to the next activity in which the game is played
     */
    public void goToGameActivity(String gameId, GameBoard gameBoard) {
        //loop on shipDataCollection and check if all ships are placed
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("currPlayerBoard", gameBoard);
//        setBrutalDestroy(false);
        //log start of activity
        Log.d("PlaceYourShips DEBUG", "goToGameActivity: " + "Starting GameActivity");
        try {
            this.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            Log.d("PlaceYourShips DEBUG", "goToGameActivity: " + "Error: " + e.getMessage());
        }
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
        Log.d("DEBUG ArrangeActivity", "onResume: ");
        AudioUtils.resumeMusicState(ArrangeGameBoardActivity.this); // mute the music
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DEBUG ArrangeActivity", "onDestroy: ");

    }
}