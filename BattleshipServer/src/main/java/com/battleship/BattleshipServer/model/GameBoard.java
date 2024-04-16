package com.battleship.BattleshipServer.model;

import com.battleship.BattleshipServer.model.ship.Ship;
import com.battleship.BattleshipServer.model.tile.Tile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameBoard {

    private String gameId;

    private String userId;

    private ArrayList<Tile> board;

    private ArrayList<Ship> ships;
}
