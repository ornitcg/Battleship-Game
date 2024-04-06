package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.model.game.Game;
import com.battleship.BattleshipServer.resources.ApiResponse;

public class GetGameCmd {
    private GameDao gameDao;

    private String gameId;

    public GetGameCmd(GameDao gameDao, String gameId) {
        this.gameDao = gameDao;
        this.gameId = gameId;
    }

    public ApiResponse<Game> execute() {
        ApiResponse<Game> retVal;
        retVal = gameDao.get(gameId);

        return retVal;
    }
}
