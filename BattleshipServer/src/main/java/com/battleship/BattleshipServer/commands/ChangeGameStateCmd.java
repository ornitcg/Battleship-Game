package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.model.game.Game;
import com.battleship.BattleshipServer.model.game.GameStateEnum;
import com.battleship.BattleshipServer.resources.ApiResponse;

public class ChangeGameStateCmd {

    private String gameId;
    private GameStateEnum gameState;
    private GameDao gameDao;

    private static final String FAILED_MSG = "Failed to change game state";

    public ChangeGameStateCmd(GameDao gameDao, String gameId, GameStateEnum gameState) {
        this.gameId = gameId;
        this.gameState = gameState;
        this.gameDao = gameDao;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal;
        ApiResponse<Game> getResponse = gameDao.get(gameId);

        if (getResponse.isSucceeded()) {
            Game game = getResponse.getValue();
            game.setGameState(gameState);

            ApiResponse<String> updateResponse = gameDao.update(game, gameId);

            if (updateResponse.isSucceeded()) {
                retVal = updateResponse;
            } else {
                retVal = ApiResponse.createFailedResponse(FAILED_MSG);
            }
        } else {
            retVal = ApiResponse.createFailedResponse(FAILED_MSG);
        }

        return retVal;
    }
}
