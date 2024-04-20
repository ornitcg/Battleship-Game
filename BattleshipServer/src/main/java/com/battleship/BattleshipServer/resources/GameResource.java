package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.*;
import com.battleship.BattleshipServer.dao.BoardDao;
import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.dao.ShipDao;
import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.model.GameBoard;
import com.battleship.BattleshipServer.model.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class GameResource {

    @Autowired
    private GameDao gameDao;

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private TileDao tileDao;

    @Autowired
    private ShipDao shipDao;

    public static Queue<String> waitingUsers = new LinkedList<>();

    public static final Object waitingUsersLock = new Object();

    public static Set<String> gameCreatedForUserIds = new HashSet<>();

    public static final Object gameCreatedForUserIdsLock = new Object();

    public static Map<String, Integer> attacks = new HashMap<>();

    public static final Object attacksLock = new Object();

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

    @PostMapping("/game")
    private ApiResponse<String> createGame(@RequestParam String userId) {
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

    /*
    Use this endpoint to get the position that the opponent attacked.
    Send gameId, return the position.
     */
    @GetMapping("/attack")
    private ApiResponse<Integer> getAttack(@RequestParam String gameId) {
        ApiResponse<Integer> retVal;

        GetAttackCmd cmd = new GetAttackCmd(gameId);
        retVal = cmd.execute();

        return retVal;
    }

    /*
    Use this endpoint to attack opponent.
    Send userId + gameId + position to attack, in response one od [hit, miss, sunk]
     */
    @PostMapping("/attack")
    private ApiResponse<String> createAttack(@RequestParam String userId,
                                             @RequestParam String gameId,
                                             @RequestParam Integer position) {

        ApiResponse<String> retVal;

        CreateAttackCmd cmd = new CreateAttackCmd(gameDao, boardDao, tileDao, shipDao, userId, gameId, position);
        retVal = cmd.execute();

        return retVal;
    }
}
