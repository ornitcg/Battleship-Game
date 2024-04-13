package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.GameBoard;
import com.clientapp.battleshipclient.logic.Ship;
import com.clientapp.battleshipclient.logic.Tile;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.UI_utils.PlacementGridLayoutAdapter;
import android.content.ClipData;
import android.content.ClipDescription;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PlaceYourShips extends BaseActivity {

    private GridView gridView;
    PlacementGridLayoutAdapter gridLayoutAdapter;
    private FrameLayout shipsFrameLayout;
    private boolean isPlacementApproved = false;
    private ConstraintLayout mainLayout;
    private LinearLayout shipsInventoryLayout;

    protected ArrayList<Tile> tilesList;  // contains the gameboard tiles data
    protected HashMap<Integer,Ship> shipDataCollection = new HashMap<>(); // contains the data of the ships
    protected HashMap<Integer,ImageView> shipViews = new HashMap<>(); // contains the views of the ships
    private ImageButton resetShipsButton;
    private String currPlayerUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_ships);
        mainLayout = findViewById(R.id.main);
        Intent intent = this.getIntent();
        currPlayerUserId = intent.getStringExtra("currPlayerUserId");

        setupMusicToggleButton(this);
        setResetShipsButtonOnClockListener();
        setupGridView();    // set the adapter to the grid view
        setShipsDataAndViews();  // set data and listeners for the ships
        setShipsLongClickListeners();
        setFrameShipsOnClickOrientationChange();
        setFrameDragListener();
        setInventoryDragListeners();
        setBackgroundDragListener();
        setResetShipsButtonOnClockListener();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setFrameDragListener() {
        shipsFrameLayout = findViewById(R.id.shipsFrameLayoutId);
    }

    private void setResetShipsButtonOnClockListener() {
        resetShipsButton = findViewById(R.id.resetShipsID);
        resetShipsButton.setOnClickListener(v -> {
            resetInventory();
        });
    }

    private void setBackgroundDragListener() {
        mainLayout.setOnDragListener((v, event) -> {
            Ship shipData = (Ship) event.getLocalState(); // state of the dragged view
            int shipDataInventoryId = shipData.getInventoryId();
            ImageView shipView = shipViews.get(shipDataInventoryId); // extract ship ID from shipData;
            int shipFrameId = shipData.getFrameId();

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;

                case DragEvent.ACTION_DROP:
                    boolean isInsideGrid = isInsideTargetView(shipView, gridView, Target.GRID, event);
                    if (!isInsideGrid) {
                        gridLayoutAdapter.returnShipToInventory(shipData);
                        return true;
                    }
                    return false;


                case DragEvent.ACTION_DRAG_ENDED:
                    if (!event.getResult()) { // If drop was not successful
                        // Return the ship to its inventory position
                        shipView.setVisibility(View.VISIBLE);
                        if(shipData.getOrientation() == Ship.Orientation.HORIZONTAL ) { // Reset orientation if necessary
                            shipViewChangeOrientation(shipView, shipData);
                            shipData.changeOrientation();
                        }
                        // TODO Additional logic to reset ship state...
                    }
                case DragEvent.ACTION_DRAG_EXITED:
                default:
                    break;
            }//end of switch
            return true;
        } //end of onDragListener
        ); //end of setOnDragListener
    }


    private void setInventoryDragListeners() {
        shipsInventoryLayout.setOnDragListener((v, event) -> {
            Ship shipData = (Ship) event.getLocalState(); // state of the dragged view
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DROP:
                    gridLayoutAdapter.returnShipToInventory(shipData);
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

    

    private boolean isInsideTargetView(ImageView shipView, View targetView, Target target, DragEvent event) {
        if (shipView == null) {
            return false;
        }
        if (target == Target.GRID) {
            targetView = gridView;
        }
        else if (target == Target.INVENTORY) {
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

    private void setShipsLongClickListeners() {

        // iterate on all image views of the ships (inventory and frame)
        for (Map.Entry<Integer, ImageView> entry : shipViews.entrySet()) {
            ImageView shipView = entry.getValue();

            shipView.setOnLongClickListener(v -> {
                int shipId = entry.getKey();
                Ship shipData = shipDataCollection.get(shipId);
                shipView.setTag(shipData); // Make sure the ship object is set as tag if not set elsewhere

                ClipData.Item item = new ClipData.Item(String.valueOf(shipId));
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData dragData = new ClipData("Ship ID", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                if (dragData == null) {
                    return false;
                }
                View.DragShadowBuilder shipShadow = new View.DragShadowBuilder(v); //TODO delete
//                CustomDragShadowBuilder shipShadow = new CustomDragShadowBuilder(v);

                v.startDragAndDrop(dragData, shipShadow, shipData, 0);
                v.setVisibility(View.INVISIBLE); // Temporarily hide the view while dragging
                return true;

            });//end of setOnLongClickListener
        } //end of for loop

    }// end of setShipsLongClickListeners

    private void setFrameShipsOnClickOrientationChange() {
        for (Map.Entry<Integer, ImageView> entry : shipViews.entrySet()) {
            int shipId = entry.getKey();
            if (idInInventory(shipId)){
                continue; // do not change orientation of ships in inventory
            }
            ImageView shipView = entry.getValue();
            Ship shipData = shipDataCollection.get(shipId);

            shipView.setOnClickListener(v -> {
                shipView.setTag(shipData); // Make sure the ship object is set as tag if not set elsewhere
                shipData.setPlaced(false);
                shipData.changeOrientation(); //change the orientation of the ship data
                shipViewChangeOrientation(shipView,shipData);
                int rotateAxisTilePosition = shipData.getShipPositionsArray().get(shipData.getSize()/2); // get position
                shipData.forgetPositions();

                gridLayoutAdapter.resetAllTilesData();
                gridLayoutAdapter.setAllTilesData(shipDataCollection); //TODO maybe change place
                gridLayoutAdapter.notifyDataSetChanged();
                gridLayoutAdapter.setShipDataNewPositions(shipData, rotateAxisTilePosition);
                //log ship positions
                Log.d("DEBUG", "shipPositionsArray: " + shipData.getShipPositionsArray());
//                gridLayoutAdapter.setTilesWithShipDataAndStatus(shipData);
                Log.d("DEBUG", "Orientation changed to: " + shipData.getOrientation());
                if (gridLayoutAdapter.isValidForDrop(shipData)) {
                    shipData.setPlaced(true);
                    //log ship orientation
                    Log.d("DEBUG", "Orientation changed to: " + shipData.getOrientation());
                    gridLayoutAdapter.setShipViewOnGrid(shipData, gridView);
                }
                else {
                    shipData.changeOrientation(); //change back
                    shipViewChangeOrientation(shipView,shipData);
                    gridLayoutAdapter.resetAllTilesData();
                    gridLayoutAdapter.setAllTilesData(shipDataCollection); //TODO maybe change place
                    gridLayoutAdapter.notifyDataSetChanged();
                    gridLayoutAdapter.setShipDataNewPositions(shipData, rotateAxisTilePosition);
                    shipData.setPlaced(true);
                }
                logTilesStatus("setFrameShipsOnClickOrientationChange");

            });
        }
    }

    private void shipViewChangeOrientation(ImageView shipView, Ship shipData) {
        Ship.Orientation orientation = shipData.getOrientation();
        switch (orientation) { // chnage the orientation of the ship view
            case HORIZONTAL:
                shipView.setRotation(90);
                break;
            case VERTICAL:
                shipView.setRotation(0);
                break;
        }
    }


    private boolean idInInventory(Integer key) {
        Ship shipData = shipDataCollection.get(key);
        return shipData.getInventoryId() == key;
    }
    public enum Target {
        GRID, INVENTORY
    }
    private void setupGridView() {  //called by onCreate
        gridView = findViewById(R.id.currPlayerBoardGridId); // get the reference of grid view
        tilesList = new ArrayList<>();
        for (int i = 0; i < 100; i++) { //initialize the list of tiles with 100 SEA tiles
            tilesList.add(new Tile(i));
        }
        gridLayoutAdapter = new PlacementGridLayoutAdapter(this, tilesList , shipViews , shipDataCollection,  shipsFrameLayout);
        gridView.setAdapter(gridLayoutAdapter); // set the adapter to the grid view
    }

    private void setShipsDataAndViews() {
        int[] shipLengths = {5, 4, 3, 3, 2}; // the lengths of the ships
        int[] shipIdsInInventory = {
                R.id.ship_length5_id,
                R.id.ship_length4_id,
                R.id.ship_length3_1_id,
                R.id.ship_length3_2_id,
                R.id.ship_length2_id
        };
        int[] shipIdsInFrame = {
                R.id.ship_length5_onGrid_id,
                R.id.ship_length4_onGrid_id,
                R.id.ship_length3_1_onGrid_id,
                R.id.ship_length3_2_onGrid_id,
                R.id.ship_length2_onGrid_id
        };
        shipsInventoryLayout = findViewById(R.id.shipsInventoryId);

        // connect ship view to its id,
        for(int i = 0; i < shipLengths.length; i++) {
            Ship shipData = new Ship(shipIdsInInventory[i],shipIdsInFrame[i] ,shipLengths[i]);
            // add shipdata on both inventoryId and frameId:
            shipDataCollection.put(shipIdsInInventory[i],shipData );
            shipDataCollection.put(shipIdsInFrame[i],shipData);
            // add all shipviews to one collection
            shipViews.put(shipIdsInInventory[i], (ImageView) findViewById(shipIdsInInventory[i]));
            shipViews.put(shipIdsInFrame[i], (ImageView) findViewById(shipIdsInFrame[i]));
            // collections for conversion between inventory and frame id's
        }
    }

    public void resetInventory(){
        if (shipDataCollection.isEmpty()){
            return;
        }
        for (Map.Entry<Integer, Ship> entry : shipDataCollection.entrySet()) {
            Ship shipData = entry.getValue();
            int shipInventoryId = shipData.getInventoryId();
            int shipFrameId = shipData.getFrameId();
            ImageView shipViewInInventory = shipViews.get(shipInventoryId);
            ImageView shipViewInFrame = shipViews.get(shipFrameId);
            shipData.resetShip();
            shipViewChangeOrientation(shipViewInFrame, shipData);
            gridLayoutAdapter.resetAllTilesData(); //resets them to SEA
            shipViewInFrame.setVisibility(View.INVISIBLE);
            shipViewInInventory.setVisibility(View.VISIBLE);
            gridLayoutAdapter.notifyDataSetChanged();
        }
    }

    public void goToActivePlayActivity(View v) {
        //loop on shipDataCollection and check if all ships are placed
        if (!areAllShipsPlaced())
            return;
        gridLayoutAdapter.setAllTilesData(shipDataCollection);
        logTilesStatus("goToActivePlayActivity"); // TODO  logs the status of the tiles
        sendPlacementToServer();
        Intent intent = new Intent(PlaceYourShips.this, ActivePlayActivity.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        intent.putExtra("currPlayerUserId", currPlayerUserId);

        if (tilesList != null) {
            intent.putExtra("tilesList", tilesList);
        }else if (tilesList.isEmpty()) {
            Log.d("PlaceYourShips", "Warning: tilesList is empty");
        }
        else {
            Log.d("PlaceYourShips", "Warning: tilesList is null");
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

            return;
        }
        //log start of activity
        Log.d("PlaceYourShips", "goToActivePlayActivity: " + "Starting ActivePlayActivity");
        try{
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPlacementToServer() {
        //send the tileList to the server
        GameBoard myBoard = new GameBoard(this, currPlayerUserId, tilesList);
        myBoard.sendTilesDataToServer(currPlayerUserId);
    }


    private boolean areAllShipsPlaced() {
        Ship shipData = null;
        for(Map.Entry<Integer, Ship> entry : shipDataCollection.entrySet()) {
            shipData = entry.getValue();
            if (!shipData.getPlaced()) {
                Toast.makeText(this, "Please place all ships on the board", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Toast.makeText(this, "All ships are placed", Toast.LENGTH_SHORT).show();
        return true;
    }
    private void logTilesStatus(String methodName) {
        for (Tile tile : tilesList) {
            Log.d("method" + methodName, "Tile: " + tile.getPosition() + " STATUS: : " + tile.getTileState());
        }
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