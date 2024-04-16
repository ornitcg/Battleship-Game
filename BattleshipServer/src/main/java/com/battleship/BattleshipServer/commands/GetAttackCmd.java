package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.dao.ShipDao;
import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.GameResource;

public class GetAttackCmd {

    private String gameId;

    public GetAttackCmd(String gameId) {
        this.gameId = gameId;
    }

    public ApiResponse<Integer> execute() {
        ApiResponse<Integer> retVal = null;

        synchronized (GameResource.attacksLock) {
            while (GameResource.attacks.containsKey(gameId) == false) {
                try {
                    GameResource.attacksLock.wait();
                }
                catch (InterruptedException e) {
                    retVal = ApiResponse.createFailedResponse("Failed to get attack position");
                }
            }

        }

        //there was no problem with the wait
        if (retVal == null) {
            Integer position = GameResource.attacks.remove(gameId);

            retVal = ApiResponse.createSucceededResponse(position);
        }

        return retVal;
    }
}
