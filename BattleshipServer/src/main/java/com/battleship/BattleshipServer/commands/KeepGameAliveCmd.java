package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.GameResource;

import java.time.LocalDateTime;

public class KeepGameAliveCmd {
    private String gameId;

    public KeepGameAliveCmd(String gameId) {
        this.gameId = gameId;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal = ApiResponse.createSucceededResponse("OK");

        synchronized (GameResource.aliveGamesLock) {
            GameResource.aliveGames.put(gameId, LocalDateTime.now());
        }

        return retVal;
    }
}
