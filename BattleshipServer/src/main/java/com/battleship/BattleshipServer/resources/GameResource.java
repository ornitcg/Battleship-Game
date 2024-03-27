package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.CreateBoardCmd;
import com.battleship.BattleshipServer.commands.GetGameBoardsCmd;
import com.battleship.BattleshipServer.dao.BoardDao;
import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.model.GameBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class GameResource {

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private TileDao tileDao;

    @GetMapping("/getGameBoards/{gameId}")
    private ApiResponse<ArrayList<GameBoard>> signUp(@PathVariable String gameId) {
        ApiResponse<ArrayList<GameBoard>> retVal;

        GetGameBoardsCmd cmd = new GetGameBoardsCmd(boardDao, tileDao, gameId);
        retVal = cmd.execute();

        return retVal;
    }

}
