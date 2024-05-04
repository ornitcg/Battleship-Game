package com.battleship.BattleshipServer.logic;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAttackResponse {
    private String attackResult;

    private String shipType;

    private Integer position;

    private String orientation;

    public static CreateAttackResponse createSunkResponse(String attackResult, String shipType, Integer position, String orientation) {
        return new CreateAttackResponse(attackResult, shipType, position, orientation);
    }

    public static CreateAttackResponse createResponse(String attackResult) {
        return new CreateAttackResponse(attackResult, null, null, null);
    }
}
