package com.battleship.BattleshipServer.logic;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.model.game.Game;
import com.battleship.BattleshipServer.model.game.GameStateEnum;
import com.battleship.BattleshipServer.resources.ApiResponse;

public class Helper {

    public static ApiResponse<String> changeGameState(String gameId, GameStateEnum gameState, String failedMsg, GameDao gameDao) {
        ApiResponse<String> retVal;
        ApiResponse<Game> getResponse = gameDao.get(gameId);

        if (getResponse.isSucceeded()) {
            Game game = getResponse.getValue();
            game.setGameState(gameState);

            ApiResponse<String> updateResponse = gameDao.update(game, gameId);

            if (updateResponse.isSucceeded()) {
                retVal = updateResponse;
            } else {
                retVal = ApiResponse.createFailedResponse(failedMsg);
            }
        } else {
            retVal = ApiResponse.createFailedResponse(failedMsg);
        }

        return retVal;
    }
}
