package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.board.CreateBoardCmd;
import com.battleship.BattleshipServer.commands.board.GetBoardCmd;
import com.battleship.BattleshipServer.dao.BoardDao;
import com.battleship.BattleshipServer.dao.ShipDao;
import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.model.GameBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
public class BoardResource {

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private TileDao tileDao;

    @Autowired
    private ShipDao shipDao;

    public static Set<String> createGameBoards = new HashSet<>();

    public static final Object createGameBoardsLock = new Object();


    /*
    *  This endpoint is used to create a new board.
    *  To create a new board, you need to provide the board object in the request body.
    *  {
    *      "userId": "user-123",
    *      "board": [ // all tiles of board, size 100
    *          {
    *              "position": 0, //0-99
    *              "state": "ship" //or 'sea'
    *          }
    *      ],
    *      "ships": [ // one of each ship type, size 5
    *          {
    *              "position": 0,
    *              "type": "battleship",
    *              "orientation": "vertical",
    *              "size": 2
    *          }
    *      ]
    *  }
    * */
    @PostMapping("/board")
    private ApiResponse<String> createBoard(@RequestBody GameBoard gameBoardToCreate) {
        ApiResponse<String> retVal;

        CreateBoardCmd cmd = new CreateBoardCmd(boardDao, tileDao, shipDao, gameBoardToCreate);
        retVal = cmd.execute();

        return retVal;
    }


    /*
    *  This endpoint is used to get a board.
    *  To get a board, you need to provide the boardId in path params.
    * */
    @GetMapping("/board/{boardId}")
    private ApiResponse<GameBoard> getBoard(@PathVariable String boardId) {
        ApiResponse<GameBoard> retVal;

        GetBoardCmd cmd = new GetBoardCmd(tileDao, boardId);
        retVal = cmd.execute();

        return retVal;
    }
}
