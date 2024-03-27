package com.battleship.BattleshipServer.model.tile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tile {

    private Integer position;

    private String boardId;

    private TileStateEnum state;

}
