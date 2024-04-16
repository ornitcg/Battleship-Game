package com.battleship.BattleshipServer.model.ship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ship {
    private String id;

    private String boardId;

    private Integer position;

    private ShipTypeEnum type;

    private OrientationEnum orientation;

    private Integer size;

    private Integer numHits;
}
