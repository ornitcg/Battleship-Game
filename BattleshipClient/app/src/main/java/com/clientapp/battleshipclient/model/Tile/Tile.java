package com.clientapp.battleshipclient.model.Tile;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tile  implements Serializable {
    /*
    *  This class represents a single tile on the game board.
    *  It contains the position of the tile, the state of the tile and the id of the ship that is placed on the tile.
    *  The state of the tile can be SEA, SHIP, HIT, MISS
    *  The ship id is -1 if there is no ship on the tile
    *  The position is a number from 0 to 99
    * */

    private int position;
    private TileStateEnum state;
    private String shipId;


    /*
    *  Constructor for the Tile class
    * */
    public Tile(int position){
        this.state = TileStateEnum.SEA;
        this.setShipId("-1");
        this.setPosition(position);
    }

    /*
    *  Reset the data of a single tile
    * */
    public void resetSingleTileData() {
        this.setState(TileStateEnum.SEA);
        this.setShipId("-1");
    }


}
