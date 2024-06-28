package com.battleship.BattleshipServer.commands.game;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.logic.Helper;
import com.battleship.BattleshipServer.model.game.Game;
import com.battleship.BattleshipServer.model.game.GameStateEnum;
import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.GameResource;

import java.time.LocalDateTime;

public class GetGameCmd {
    private static final String FAILED_MSG = "Failed to get game";
    private static final Integer MAX_TIME_BEFORE_ENDING_GAME = 30;

    private GameDao gameDao;

    private String gameId;

    public GetGameCmd(GameDao gameDao, String gameId) {
        this.gameDao = gameDao;
        this.gameId = gameId;
    }

    public ApiResponse<Game> execute() {
        ApiResponse<Game> retVal = null;
        ApiResponse<String> isGameUpdated = null;

        /*
        *  This is the logic to verify that the second user is still in the game.
         */
        synchronized (GameResource.aliveGamesLock) {
            if (GameResource.aliveGames.containsKey(gameId)) {
                LocalDateTime lastNotifiedThatGameAlive = GameResource.aliveGames.get(gameId);
                LocalDateTime maxTimeToConsiderAsAlive = LocalDateTime.now().minusSeconds(MAX_TIME_BEFORE_ENDING_GAME);

                if (lastNotifiedThatGameAlive.isBefore(maxTimeToConsiderAsAlive)) {
                    isGameUpdated = Helper.changeGameState(gameId, GameStateEnum.ENDED, FAILED_MSG, gameDao);
                }
            }
        }

        if (isGameUpdated != null) {
            if (isGameUpdated.isSucceeded() == false) {
                retVal = ApiResponse.createFailedResponse(FAILED_MSG);
            }
        }

        //only if no problem occur
        if (retVal == null) {
            retVal = gameDao.get(gameId);
        }

        return retVal;
    }
}
