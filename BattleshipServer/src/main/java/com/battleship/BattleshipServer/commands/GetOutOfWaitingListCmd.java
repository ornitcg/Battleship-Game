package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.logic.Helper;
import com.battleship.BattleshipServer.model.game.GameStateEnum;
import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.GameResource;

public class GetOutOfWaitingListCmd {
    private static final String FAILED_MSG = "Failed to extract user from waiting list";
    private final String userId;
    private final GameDao gameDao;

    public GetOutOfWaitingListCmd(GameDao gameDao, String userId) {
        this.userId = userId;
        this.gameDao = gameDao;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal = null;

        synchronized (GameResource.waitingUsersLock) {
            GameResource.waitingUsers.remove(userId);
            GameResource.waitingUsersLock.notifyAll();
        }

        //if someone matched with this user before he extracted himself from the waiting list
        synchronized (GameResource.gameCreatedByUserIdsLock) {
            if (GameResource.gameCreatedByUserIds.containsKey(userId)) {
                String gameId = GameResource.gameCreatedByUserIds.remove(userId);

                //we want to update the game status so the other user will be notified
                ApiResponse<String> response = Helper.changeGameState(gameId, GameStateEnum.ENDED, FAILED_MSG, gameDao);

                if (response.isSucceeded() == false) {
                    retVal = ApiResponse.createFailedResponse(FAILED_MSG);
                }
            }

        }

        if (retVal == null) { // no problem occur
            retVal = ApiResponse.createSucceededResponse(String.format("Player %s stopped waiting", userId));
        }

        return retVal;
    }
}
