package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.model.game.Game;
import com.battleship.BattleshipServer.resources.ApiResponse;

public class UpdateGameCmd {
    private Game gameToUpdate;
    private String gameId;
    private GameDao gameDao;

    public UpdateGameCmd(GameDao gameDao, Game gameToUpdate, String gameId) {
        this.gameToUpdate = gameToUpdate;
        this.gameId = gameId;
        this.gameDao = gameDao;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal;

        retVal = gameDao.update(gameToUpdate, gameId);

        return retVal;
    }
}
