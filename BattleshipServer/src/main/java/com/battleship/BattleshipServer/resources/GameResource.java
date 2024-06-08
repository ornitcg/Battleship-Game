package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.*;
import com.battleship.BattleshipServer.dao.*;
import com.battleship.BattleshipServer.logic.CreateAttackResponse;
import com.battleship.BattleshipServer.logic.GameCreated;
import com.battleship.BattleshipServer.model.GameBoard;
import com.battleship.BattleshipServer.model.game.Game;
import com.battleship.BattleshipServer.model.game.GameStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @Autowired
    private UserDao userDao;

    public static Queue<String> waitingUsers = new LinkedList<>();

    public static final Object waitingUsersLock = new Object();

    public static Map<String, GameCreated> gameCreatedByUserIds = new HashMap<>();

    public static final Object gameCreatedByUserIdsLock = new Object();

    public static Map<String, Integer> attacks = new HashMap<>();

    public static final Object attacksLock = new Object();

    public static Map<String, Integer> numMovesByUserId = new HashMap<>();

    /*
    gameId -> last time said he is alive.
    If something unexpected happened, as if the phone brakes (something we can't know or
    predict),  we want to notify the other player. so if one of the players wasn't notified that the game is still
    alive, the game will end.
     */
    public static Map<String, LocalDateTime> aliveGames = new HashMap<>();

    public static final Object aliveGamesLock = new Object();

    @DeleteMapping("/opponent/{userId}")
    private ApiResponse<String> getOutOfWaitingList(@PathVariable String userId) {
        ApiResponse<String> retVal;

        GetOutOfWaitingListCmd cmd = new GetOutOfWaitingListCmd(gameDao, userId);
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

    @PutMapping("/endGame/{gameId}")
    private ApiResponse<String> endGame(@PathVariable String gameId) {
        ApiResponse<String> retVal;

        ChangeGameStateCmd cmd = new ChangeGameStateCmd(gameDao, gameId, GameStateEnum.ENDED);
        retVal = cmd.execute();

        return retVal;
    }

    @PutMapping("/pauseGame/{gameId}")
    private ApiResponse<String> pauseGame(@PathVariable String gameId) {
        ApiResponse<String> retVal;

        ChangeGameStateCmd cmd = new ChangeGameStateCmd(gameDao, gameId, GameStateEnum.PAUSED);
        retVal = cmd.execute();

        return retVal;
    }

    @PutMapping("/resumeGame/{gameId}")
    private ApiResponse<String> resumeGame(@PathVariable String gameId) {
        ApiResponse<String> retVal;

        ChangeGameStateCmd cmd = new ChangeGameStateCmd(gameDao, gameId, GameStateEnum.IN_PROGRESS);
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
    private ApiResponse<Integer> getAttack(@RequestParam String userId) {
        ApiResponse<Integer> retVal;

        GetAttackCmd cmd = new GetAttackCmd(userId);
        retVal = cmd.execute();

        return retVal;
    }

    /*
    Use this endpoint to attack opponent.
    Send userId + gameId + position to attack, in response one od [hit, miss, sunk]
     */
    @PostMapping("/attack")
    private ApiResponse<CreateAttackResponse> createAttack(@RequestParam String userId,
                                                           @RequestParam String gameId,
                                                           @RequestParam Integer position) {

        ApiResponse<CreateAttackResponse> retVal;

        CreateAttackCmd cmd = new CreateAttackCmd(gameDao, boardDao, tileDao, shipDao, userDao, userId, gameId,
                position);
        retVal = cmd.execute();

        return retVal;
    }

    @PutMapping("keepGameAlive/{gameId}")
    private ApiResponse<String> keepGameAlive(@PathVariable String gameId) {
        ApiResponse<String> retVal;

        KeepGameAliveCmd cmd = new KeepGameAliveCmd(gameId);
        retVal = cmd.execute();

        return retVal;
    }
}
