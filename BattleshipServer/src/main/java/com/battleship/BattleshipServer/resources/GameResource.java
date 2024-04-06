package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.*;
import com.battleship.BattleshipServer.dao.BoardDao;
import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.model.GameBoard;
import com.battleship.BattleshipServer.model.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

@RestController
public class GameResource {

    @Autowired
    private GameDao gameDao;

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private TileDao tileDao;

    public static final Queue<String> waitingUsers = new LinkedList<>();

    public static final Object waitingUsersLock = new Object();

    @GetMapping("/opponent/{userId}")
    private ApiResponse<String> getOpponent(@PathVariable String userId) {
        ApiResponse<String> retVal;

        GetOpponentCmd cmd = new GetOpponentCmd(userId);
        retVal = cmd.execute();

        return retVal;
    }

    @DeleteMapping("/opponent/{userId}")
    private ApiResponse<String> getOutOfWaitingList(@PathVariable String userId) {
        ApiResponse<String> retVal;

        GetOutOfWaitingListCmd cmd = new GetOutOfWaitingListCmd(userId);
        retVal = cmd.execute();

        return retVal;
    }

    @GetMapping("/game/{gameId}")
    private ApiResponse<Game> getGame(@PathVariable String gameId) {
        ApiResponse<Game> retVal;

        GetGameCmd cmd = new GetGameCmd(gameDao, gameId);
        retVal = cmd.execute();

        return retVal;
    }

    @PostMapping("/game/{userId}")
    private ApiResponse<String> createGame(@PathVariable String userId) {
        ApiResponse<String> retVal;

        CreateGameCmd cmd = new CreateGameCmd(gameDao, userId);
        retVal = cmd.execute();

        return retVal;
    }

    @GetMapping("/getGameBoards/{gameId}")
    private ApiResponse<ArrayList<GameBoard>> getGameBoards(@PathVariable String gameId) {
        ApiResponse<ArrayList<GameBoard>> retVal;

        GetGameBoardsCmd cmd = new GetGameBoardsCmd(boardDao, tileDao, gameId);
        retVal = cmd.execute();

        return retVal;
    }
}
