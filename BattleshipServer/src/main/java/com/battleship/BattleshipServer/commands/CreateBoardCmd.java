package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.BoardDao;
import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.model.Board;
import com.battleship.BattleshipServer.model.GameBoard;
import com.battleship.BattleshipServer.model.tile.Tile;
import com.battleship.BattleshipServer.resources.ApiResponse;

import java.util.ArrayList;

public class CreateBoardCmd {

    private BoardDao boardDao;

    private TileDao tileDao;
    private GameBoard gameBoardToCreate;

    public CreateBoardCmd(BoardDao boardDao, TileDao tileDao, GameBoard gameBoardToCreate) {
        this.boardDao = boardDao;
        this.tileDao = tileDao;
        this.gameBoardToCreate = gameBoardToCreate;
    }

    public ApiResponse<GameBoard> execute() {
        ApiResponse<GameBoard> retVal;

        String gameId = gameBoardToCreate.getGameId();
        String userId = gameBoardToCreate.getUserId();
        ArrayList<Tile> board = gameBoardToCreate.getBoard();

        ApiResponse<String> createBoardResponse = createBoard(gameId, userId);

        if (createBoardResponse.isSucceeded()) {
            String boardId = createBoardResponse.getValue();
            ApiResponse<String> createTailsResponse = createTiles(board, boardId);

            if (createTailsResponse.isSucceeded()) {
                retVal = ApiResponse.createSucceededResponse(gameBoardToCreate);
            }
            else {
                // we failed to create tails, so we want to delete board since it not represent them
                boardDao.delete(boardId);
                retVal = ApiResponse.createFailedResponse(createTailsResponse.getMsg());
            }
        }
        else {
            retVal = ApiResponse.createFailedResponse(createBoardResponse.getMsg());
        }

        return retVal;
    }

    private ApiResponse<String> createBoard(String gameId, String userId) {
        ApiResponse<String> retVal;

        Board board = new Board();
        board.setGameId(gameId);
        board.setUserId(userId);

        retVal = boardDao.create(board);
        return retVal;
    }

    private ApiResponse<String> createTiles(ArrayList<Tile> tiles, String boardId) {
        ApiResponse<String> retVal;

        // update tiles board id
        tiles.forEach(e->e.setBoardId(boardId));

        retVal = tileDao.createBoardTails(tiles, boardId);
        return retVal;
    }
}
