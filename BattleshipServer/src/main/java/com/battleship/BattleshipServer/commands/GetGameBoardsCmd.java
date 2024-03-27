package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.BoardDao;
import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.model.Board;
import com.battleship.BattleshipServer.model.GameBoard;
import com.battleship.BattleshipServer.model.tile.Tile;
import com.battleship.BattleshipServer.resources.ApiResponse;

import java.util.ArrayList;
import java.util.List;

public class GetGameBoardsCmd {

    private BoardDao boardDao;

    private TileDao tileDao;
    private String gameId;

    public GetGameBoardsCmd(BoardDao boardDao, TileDao tileDao, String gameId) {
        this.boardDao = boardDao;
        this.tileDao = tileDao;
        this.gameId = gameId;
    }

    public ApiResponse<ArrayList<GameBoard>> execute() {
        ApiResponse<ArrayList<GameBoard>> retVal;
        ArrayList<GameBoard> gameBoards = new ArrayList<>();
        boolean failedToFetch = false;
        String failMsg = null;

        ApiResponse<List<Board>> boardsResponse = getBoards(gameId);

        if (boardsResponse.isSucceeded()) {
            List<Board> boards = boardsResponse.getValue();

            for (Board board : boards) {
                ApiResponse<List<Tile>> tilesResponse = getTiles(board.getId());

                if (tilesResponse.isSucceeded() == false) {
                    failedToFetch = true;
                    failMsg = tilesResponse.getMsg();
                } else {
                    List<Tile> tiles = tilesResponse.getValue();
                    GameBoard gameBoard = new GameBoard(gameId, board.getUserId(), new ArrayList<>(tiles));
                    gameBoards.add(gameBoard);
                }
            }
        } else {
            failedToFetch = true;
            failMsg = boardsResponse.getMsg();
        }

        if (failedToFetch) {
            retVal = ApiResponse.createFailedResponse(failMsg);
        } else {
            retVal = ApiResponse.createSucceededResponse(gameBoards);
        }

        return retVal;
    }

    private ApiResponse<List<Board>> getBoards(String gameId) {
        ApiResponse<List<Board>> retVal;

        retVal = boardDao.getGameBoards(gameId);
        return retVal;
    }

    private ApiResponse<List<Tile>> getTiles(String boardId) {
        ApiResponse<List<Tile>> retVal;

        retVal = tileDao.getBoardTiles(boardId);
        return retVal;
    }
}
