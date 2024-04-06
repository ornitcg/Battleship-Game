package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.GameResource;

public class GetOutOfWaitingListCmd {
    private final String userId;

    public GetOutOfWaitingListCmd(String userId) {
        this.userId = userId;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal;

        synchronized (GameResource.waitingUsersLock) {
//            System.out.printf("Player %s stopped waiting%n", userId);
            GameResource.waitingUsers.remove(userId);
            GameResource.waitingUsersLock.notifyAll();
        }

        retVal = ApiResponse.createSucceededResponse(String.format("Player %s stopped waiting", userId));
        return retVal;
    }
}
