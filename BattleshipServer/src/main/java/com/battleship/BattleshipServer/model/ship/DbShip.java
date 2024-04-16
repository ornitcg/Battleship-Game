package com.battleship.BattleshipServer.model.ship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbShip {

    private String id;

    private String boardId;

    private Integer position;

    private String type;

    private String orientation;

    private Integer size;

    private Integer numHits;
}
