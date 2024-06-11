package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.logic.GameCreated;
import com.battleship.BattleshipServer.model.game.Game;
import com.battleship.BattleshipServer.model.game.GameStateEnum;
import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.GameResource;

import java.time.LocalDateTime;

public class CreateGameCmd {

    private String userId;
    private GameDao gameDao;

    private static final String FAILED_MSG = "Failed to create game";
    private static final int MINUTES_AGO_TO_CONSIDER_AS_CURRENT_GAME = 1;

    public CreateGameCmd(GameDao gameDao, String userId) {
        this.userId = userId;
        this.gameDao = gameDao;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal;

        retVal = getGameIfAlreadyCreated();

        if (retVal == null) {
            startWaiting();
            ApiResponse<String> matchPlayersResponse = matchPlayers();

            if (matchPlayersResponse.isSucceeded()) {
                String maybeOpponentUserId = matchPlayersResponse.getValue();

                // if this is the first user that found the match from this couple, he will create the game for both
                if (maybeOpponentUserId.startsWith("user")) {
                    Game game = newGame(maybeOpponentUserId);
                    retVal = gameDao.create(game);

                    synchronized (GameResource.gameCreatedByUserIdsLock) {
                        GameCreated gameCreated = new GameCreated(retVal.getValue(), LocalDateTime.now());
                        GameResource.gameCreatedByUserIds.put(maybeOpponentUserId, gameCreated);
                        GameResource.gameCreatedByUserIds.put(userId, gameCreated); // if the request arrived more
                        // then once, so the correct thread will get the game id.
                        GameResource.gameCreatedByUserIdsLock.notifyAll();
                    }

                    //reset moves counter for new game
                    GameResource.numMovesByUserId.put(userId, 0);
                    GameResource.numMovesByUserId.put(maybeOpponentUserId, 0);
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
                            GameCreated gameCreated = GameResource.gameCreatedByUserIds.get(userId);
                            retVal = ApiResponse.createSucceededResponse(gameCreated.getGameId());
                        }
                    }
                }
            } else {
                retVal = matchPlayersResponse;
            }
        }

        return retVal;
    }

    /*
    We want to remove user from the map if is here from old game.
    Also, if one call for match has failed for one of the users, we want to get the game if
    already created for the other user.
    The scenario: user 1 call was succeeded, so he extracted user 2 from waiting list.
                  user 2 call has failed, so he tried again. he is not in the list so if we
                  won't check if the game already created, he will enter the waiting list again.
                  we will only return the game if is in progress.
        */
    private ApiResponse<String> getGameIfAlreadyCreated() {
        ApiResponse<String> retVal = null;

        synchronized (GameResource.gameCreatedByUserIdsLock) {
            GameCreated gameCreated = GameResource.gameCreatedByUserIds.remove(userId);

            if (gameCreated != null) {
                LocalDateTime creationTime = gameCreated.getCreationTime();
                LocalDateTime minTimeToConsiderAsCurrentGame = LocalDateTime.now().minusMinutes(MINUTES_AGO_TO_CONSIDER_AS_CURRENT_GAME);

                if (creationTime.isAfter(minTimeToConsiderAsCurrentGame)) {
                    String gameId = gameCreated.getGameId();
                    ApiResponse<Game> getGameResponse = gameDao.get(gameId);

                    if (getGameResponse.isSucceeded()) {
                        Game game = getGameResponse.getValue();

                        retVal = switch (game.getGameState()) {
                            case IN_PROGRESS -> ApiResponse.createSucceededResponse(gameId);
                            case FINISHED, ENDED, PAUSED -> null;
                        };
                    }
                    else {
                        retVal = ApiResponse.createFailedResponse(getGameResponse.getMsg());
                    }
                }
            }
        }

        return retVal;
    }

    private void startWaiting() {
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
