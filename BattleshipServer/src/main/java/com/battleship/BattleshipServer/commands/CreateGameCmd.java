package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.model.game.Game;
import com.battleship.BattleshipServer.model.game.GameStateEnum;
import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.GameResource;

import java.time.LocalDateTime;

public class CreateGameCmd {

    private String userId;
    private GameDao gameDao;

    private static final String FAILED_MSG = "Failed to create game";

    public CreateGameCmd(GameDao gameDao, String userId) {
        this.userId = userId;
        this.gameDao = gameDao;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal;

        startWaiting();
        ApiResponse<String> matchPlayersResponse = matchPlayers();

        if (matchPlayersResponse.isSucceeded()) {
            String maybeOpponentUserId = matchPlayersResponse.getValue();

            // if this is the first user that found the match from this couple, he will create the game for both
            if (maybeOpponentUserId.startsWith("user")) {
                Game game = newGame(maybeOpponentUserId);
                retVal = gameDao.create(game);

                synchronized (GameResource.gameCreatedByUserIdsLock) {
                    GameResource.gameCreatedByUserIds.put(maybeOpponentUserId, retVal.getValue());
                    GameResource.gameCreatedByUserIds.put(userId, retVal.getValue()); // if the request arrived more
                    // then once, so the correct thread will get the game id.
                    GameResource.gameCreatedByUserIdsLock.notifyAll();
                }
            }
            else {
                boolean failedToWait = false;

                synchronized (GameResource.gameCreatedByUserIdsLock) {
                    while (GameResource.gameCreatedByUserIds.containsKey(userId) == false) {
                        try {
                            GameResource.gameCreatedByUserIdsLock.wait();
                        } catch (InterruptedException e) {
                            failedToWait = true;
                        }
                    }

                    if (failedToWait) {
                        retVal = ApiResponse.createFailedResponse(FAILED_MSG);
                    }
                    else {
                        String gameId = GameResource.gameCreatedByUserIds.remove(userId);
                        retVal = ApiResponse.createSucceededResponse(gameId);
                    }
                }
            }
        }
        else {
            retVal = matchPlayersResponse;
        }

        return retVal;
    }

    private void startWaiting() {
        synchronized (GameResource.waitingUsersLock) {
            //            System.out.printf("Player %s start waiting%n", userId);
            if (GameResource.waitingUsers.contains(userId) == false) {
                GameResource.waitingUsers.add(userId);
                GameResource.waitingUsersLock.notifyAll();
            }
        }
    }

    private ApiResponse<String> matchPlayers() {
        ApiResponse<String> retVal;

        synchronized (GameResource.waitingUsersLock) {
            boolean failedToWait = false;

            while (GameResource.waitingUsers.size() < 2 && GameResource.waitingUsers.contains(userId)) {
                //                System.out.printf("Player %s waiting%n", userId);
                // Wait until another player is available
                try {
                    GameResource.waitingUsersLock.wait();
                } catch (InterruptedException e) {
                /*
                Failed to wait, so we want to extract this user from waiting queue.
                We are here if waitingUsers contain less than 2 users.
                Since we first insert user to queue and only after start waiting, there
                is exactly one player, this user request.
                 */
                    GameResource.waitingUsers.remove(userId);
                    failedToWait = true;
                }
            }

            if (failedToWait) {
                retVal = ApiResponse.createFailedResponse(FAILED_MSG);
            }
            else if (GameResource.waitingUsers.contains(userId) == false) {
                // when the opponent found me
                String msg = "Found opponent and created the game";
                //                System.out.printf(msg + " for %s%n", userId);
                retVal = ApiResponse.createSucceededResponse(msg);
            } else {
            /*
             We get here if there are 2 players in queue.
             We extract the user entered with the request, and its opponent.
             We want to return the opponent id.
             */
                String user1 = GameResource.waitingUsers.poll();
                String user2 = GameResource.waitingUsers.poll();
                String opponent = user1.equals(userId) ? user2 : user1;

                //                System.out.printf("In player %s cmd%n", userId);
                //                System.out.printf("user1=%s%n", user1);
                //                System.out.printf("user2=%s%n", user2);
                //                System.out.printf("opponent=%s%n", opponent);
                retVal = ApiResponse.createSucceededResponse(opponent);
            }

            GameResource.waitingUsersLock.notifyAll();
        }

        return retVal;
    }

    private Game newGame(String opponent) {
        Game retVal = new Game();

        LocalDateTime currentTime = LocalDateTime.now();
        retVal.setStartTime(currentTime);
        retVal.setGameState(GameStateEnum.IN_PROGRESS);
        retVal.setTurnUserId(opponent);

        return retVal;
    }
}
