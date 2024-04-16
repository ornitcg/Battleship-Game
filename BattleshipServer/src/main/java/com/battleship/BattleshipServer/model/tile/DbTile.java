package com.battleship.BattleshipServer.model.tile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbTile {
    private Integer position;

    private String boardId;

    private String state;

    private String shipId; //can be null
}
