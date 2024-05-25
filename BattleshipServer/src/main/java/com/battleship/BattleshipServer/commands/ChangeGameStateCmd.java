package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.logic.Helper;
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
        ApiResponse<String> retVal = Helper.changeGameState(gameId, gameState, FAILED_MSG, gameDao);

        return retVal;
    }
}
