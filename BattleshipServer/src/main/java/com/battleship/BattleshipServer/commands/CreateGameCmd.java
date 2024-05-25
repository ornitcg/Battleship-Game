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

                //reset moves counter for new game
                GameResource.numMovesByUserId.put(userId,0);
                GameResource.numMovesByUserId.put(maybeOpponentUserId,0);
            } else {
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
                    } else {
                        String gameId = GameResource.gameCreatedByUserIds.remove(userId);
                        retVal = ApiResponse.createSucceededResponse(gameId);
                    }
                }
            }
        } else {
            retVal = matchPlayersResponse;
        }

        return retVal;
    }

    private void startWaiting() {
         /*
        when a user creates a game, he inserts himself to the gameIdByUserId map
        as a protection mechanism in failures.
        But he will stay in the map if no problem occur, so we want to extract him if he is in it
        before the next matching
         */
        synchronized (GameResource.gameCreatedByUserIdsLock) {
            GameResource.gameCreatedByUserIds.remove(userId);
        }

        synchronized (GameResource.waitingUsersLock) {
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
            } else if (GameResource.waitingUsers.contains(userId) == false) {
                // when the opponent found me
                String msg = "Found opponent and created the game";
                retVal = ApiResponse.createSucceededResponse(msg);
            } else {
            /*
             We get here if there are 2 players in queue.
             We extract the user entered with the request, and its opponent.
             We want to return the opponent id.
             */
                GameResource.waitingUsers.remove(userId);
                String opponent = GameResource.waitingUsers.poll();
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
