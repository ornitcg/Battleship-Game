package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.BoardDao;
import com.battleship.BattleshipServer.dao.ShipDao;
import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.model.Board;
import com.battleship.BattleshipServer.model.GameBoard;
import com.battleship.BattleshipServer.model.ship.Ship;
import com.battleship.BattleshipServer.model.tile.Tile;
import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.BoardResource;
import com.battleship.BattleshipServer.resources.GameResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateBoardCmd {

    private final BoardDao boardDao;
    private final TileDao tileDao;
    private final ShipDao shipDao;
    private final GameBoard gameBoardToCreate;

    public CreateBoardCmd(BoardDao boardDao, TileDao tileDao, ShipDao shipDao, GameBoard gameBoardToCreate) {
        this.boardDao = boardDao;
        this.tileDao = tileDao;
        this.shipDao = shipDao;
        this.gameBoardToCreate = gameBoardToCreate;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal = null;

        String gameId = gameBoardToCreate.getGameId();
        String userId = gameBoardToCreate.getUserId();
        ArrayList<Tile> board = gameBoardToCreate.getBoard();
        ArrayList<Ship> ships = gameBoardToCreate.getShips();

        ApiResponse<String> createBoardResponse = createBoard(gameId, userId);

        if (createBoardResponse.isSucceeded()) {
            String boardId = createBoardResponse.getValue();
            ApiResponse<List<Ship>> createShipsResponse = createShips(ships, boardId);

            if (createShipsResponse.isSucceeded()) {
                enrichTilesWithShipId(board, createShipsResponse.getValue());
                ApiResponse<String> createTailsResponse = createTiles(board, boardId);

                if (createTailsResponse.isSucceeded()) {
                    insertOrExtractFromCreateBoardsSet(gameId);
                    boolean succeeded = checkOnCreateBoardsSet(gameId);

                    if (succeeded) {
                        retVal = ApiResponse.createSucceededResponse(boardId);
                    }
                } else {
                    // we failed to create ships, so we want to delete board since it not represent them
                    boardDao.delete(boardId);
                    retVal = ApiResponse.createFailedResponse(createShipsResponse.getMsg());
                }
            } else {
                // we failed to create tails, so we want to delete board since it not represent them
                boardDao.delete(boardId);
            }
        }

        if (retVal == null) {
            retVal = ApiResponse.createFailedResponse("Failed to create game board");
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
        tiles.forEach(e -> e.setBoardId(boardId));

        retVal = tileDao.createBoardTails(tiles, boardId);
        return retVal;
    }

    private ApiResponse<List<Ship>> createShips(ArrayList<Ship> ships, String boardId) {
        ApiResponse<List<Ship>> retVal;

        retVal = shipDao.createBoardShips(ships, boardId);
        return retVal;
    }

    private void enrichTilesWithShipId(ArrayList<Tile> board, List<Ship> ships) {
        Map<Integer, String> shipIdByPosition = ships.stream().collect(Collectors.toMap(Ship::getPosition, Ship::getId));

        for (Tile tile : board) {
            String shipId = shipIdByPosition.get(tile.getPosition());
            tile.setShipId(shipId);
        }
    }

    private void insertOrExtractFromCreateBoardsSet(String gameId) {
        synchronized (BoardResource.createGameBoardsLock) {
            //this is the second user to create the board, so we want to extract from set
            if (BoardResource.createGameBoards.contains(gameId)) {
                BoardResource.createGameBoards.remove(gameId);
                BoardResource.createGameBoardsLock.notifyAll(); // so second user can stop waiting
            }
            //this is the first user to create the board, so we want him to wait second user
            else {
                BoardResource.createGameBoards.add(gameId);
            }
        }
    }

    /*
    First user will insert gameId to the set, only when both finished to build,
    we want the game to start.
     */
    private boolean checkOnCreateBoardsSet(String gameId) {
        boolean retVal = true;

        synchronized (BoardResource.createGameBoardsLock) {
            while (BoardResource.createGameBoards.contains(gameId)) {
                try {
                    BoardResource.createGameBoardsLock.wait();
                } catch (InterruptedException e) {
                    retVal = false;
                }
            }
        }

        return retVal;
    }
}
