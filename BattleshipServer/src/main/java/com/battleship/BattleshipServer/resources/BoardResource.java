package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.CreateBoardCmd;
import com.battleship.BattleshipServer.dao.BoardDao;
import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.model.GameBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BoardResource {

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private TileDao tileDao;

    @PostMapping("/createBoard")
    private ApiResponse<GameBoard> signUp(@RequestBody GameBoard gameBoardToCreate) {
        ApiResponse<GameBoard> retVal;

        CreateBoardCmd cmd = new CreateBoardCmd(boardDao, tileDao, gameBoardToCreate);
        retVal = cmd.execute();

        return retVal;
    }
}
