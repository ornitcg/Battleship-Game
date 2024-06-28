package com.battleship.BattleshipServer.commands.board;

import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.model.GameBoard;
import com.battleship.BattleshipServer.model.tile.Tile;
import com.battleship.BattleshipServer.resources.ApiResponse;

import java.util.ArrayList;
import java.util.List;

public class GetBoardCmd {

    private static final String FAILED_MSG = "Failed to get board";
    private final TileDao tileDao;
    private final String boardId;

    public GetBoardCmd(TileDao tileDao, String boardId) {
        this.tileDao = tileDao;
        this.boardId = boardId;
    }

    public ApiResponse<GameBoard> execute() {
        ApiResponse<GameBoard> retVal;

        ApiResponse<List<Tile>> response = tileDao.getBoardTiles(boardId);

        if (response.isSucceeded()) {
            List<Tile> boardTiles = response.getValue();

            GameBoard gameBoard = new GameBoard();
            gameBoard.setBoard(new ArrayList<>(boardTiles));
            retVal = ApiResponse.createSucceededResponse(gameBoard);
        }
        else {
            retVal = ApiResponse.createFailedResponse(FAILED_MSG);
        }

        return retVal;
    }
}
