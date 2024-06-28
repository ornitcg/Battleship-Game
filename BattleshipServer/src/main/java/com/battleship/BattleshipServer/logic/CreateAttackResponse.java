package com.battleship.BattleshipServer.logic;

import lombok.AllArgsConstructor;
import lombok.Data;


/*
*  This class is used to create a response for the CreateAttackCmd class.
*/
@Data
@AllArgsConstructor
public class CreateAttackResponse {
    private String attackResult;

    private String shipType;

    private Integer position;

    private String orientation;

    /*
    *  This method is used to create a response for a sunk ship.
    *  It is used when a ship is sunk.
    * */
    public static CreateAttackResponse createSunkResponse(String attackResult, String shipType, Integer position, String orientation) {
        return new CreateAttackResponse(attackResult, shipType, position, orientation);
    }


    /*
    *  This method is used to create a response for a hit ship/miss
    * */
    public static CreateAttackResponse createResponse(String attackResult) {
        return new CreateAttackResponse(attackResult, null, null, null);
    }
}
