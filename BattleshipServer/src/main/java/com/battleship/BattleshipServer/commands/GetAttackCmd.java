package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.GameResource;

public class GetAttackCmd {

    private String userId;

    public GetAttackCmd(String userId) {
        this.userId = userId;
    }

    public ApiResponse<Integer> execute() {
        ApiResponse<Integer> retVal = null;

        synchronized (GameResource.attacksLock) {
            while (GameResource.attacks.containsKey(userId) == false) {
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
            Integer position = GameResource.attacks.remove(userId);

            retVal = ApiResponse.createSucceededResponse(position);
        }

        return retVal;
    }
}
