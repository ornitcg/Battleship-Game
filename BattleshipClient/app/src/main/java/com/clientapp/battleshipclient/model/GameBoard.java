package com.clientapp.battleshipclient.model;

import android.util.Log;

import com.clientapp.battleshipclient.model.Ship.Ship;
import com.clientapp.battleshipclient.model.Tile.Tile;
import com.clientapp.battleshipclient.model.Tile.TileStateEnum;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;



/*
 * This class represents the whole game board of the player.
 * It contains the user, the game id, the board id, the ships and the board itself
 * The board is represented by an array of tiles.
 *
 * The class contains methods to update the state of the tiles and the ships on the board.
 *
 * */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameBoard implements Serializable {
    private User user;
    private String gameId;
    /**
     *  Sets the board id
     **/
    @Setter
    private String boardId;

    private ArrayList<Ship> ships = new ArrayList<>();
    private ArrayList<Tile> board = new ArrayList<>();


    /**
     *  Constructor for the GameBoard class
     *  It initializes the board with sea tiles
     *  used for the opponent board, that initially has no data
     *  @param user: the user that the board belongs to
     *  @param gameId: the id of the game
     * */
    public GameBoard(User user, String gameId) {
        this.user = user;
        this.gameId = gameId;
        for (int i = 0; i < 100; i++) { //fill the board with sea tiles
            this.board.add(new Tile(i));
        }
    }


    /**
     *  Get a ship object by its id
     *  @param shipId: the id of the ship
     * */
    public Ship getShipById(String shipId) {
        for (Ship ship : ships) {
            if (String.valueOf(ship.getBottomViewId()).equals(shipId)) {
                return ship;
            }
        }
        return null;
    }


    /**
     *  Update the state of the tile at the given position (as HIT or MISS)
     *  @param position: the position of the tile
     * @param attackState: the state of the tile (HIT or MISS)
     * */
    public void updateTile(int position, TileStateEnum attackState) {
        Tile tile = board.get(position);
        tile.setState(attackState);
    }


    /**
     *  Promotes the ship HIT counter
     *  updates the ship state to sunk if the ship is sunk
     *  @param shipId: the id of the ship
     * */
    public void updateShipHits(String shipId) {
        Log.d("DEBUG GameBoard", "updateShipHits: " + shipId);
        Ship ship = getShipById(shipId);
        ship.setHits(ship.getHits() + 1);
        if (ship.getHits() == ship.getSize()) {
            ship.setSunk(true);
        }
    }


}
