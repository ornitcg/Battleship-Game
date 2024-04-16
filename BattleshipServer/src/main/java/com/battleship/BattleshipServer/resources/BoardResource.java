package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.CreateBoardCmd;
import com.battleship.BattleshipServer.commands.UpdateTileCmd;
import com.battleship.BattleshipServer.dao.BoardDao;
import com.battleship.BattleshipServer.dao.ShipDao;
import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.model.GameBoard;
import com.battleship.BattleshipServer.model.tile.Tile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@RestController
public class BoardResource {

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private TileDao tileDao;

    @Autowired
    private ShipDao shipDao;

    public static final Set<String> createGameBoards = new HashSet<>();

    public static final Object createGameBoardsLock = new Object();

    @PostMapping("/board")
    private ApiResponse<String> createBoard(@RequestBody GameBoard gameBoardToCreate) {
        ApiResponse<String> retVal;

        CreateBoardCmd cmd = new CreateBoardCmd(boardDao, tileDao, shipDao, gameBoardToCreate);
        retVal = cmd.execute();

        return retVal;
    }

    @PutMapping("/board/tile")
    private ApiResponse<String> updateTile(@RequestBody Tile tileToUpdate) {
        ApiResponse<String> retVal;

        UpdateTileCmd cmd = new UpdateTileCmd(tileDao, tileToUpdate);
        retVal = cmd.execute();

        return retVal;
    }
}
