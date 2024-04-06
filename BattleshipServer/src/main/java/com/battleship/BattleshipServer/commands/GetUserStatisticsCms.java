package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.model.User;
import com.battleship.BattleshipServer.model.UserStatistics;
import com.battleship.BattleshipServer.model.game.Game;
import com.battleship.BattleshipServer.resources.ApiResponse;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GetUserStatisticsCms {

    private static final int DEFAULT_NUM_BEST_SCORES = 10;

    private GameDao gameDao;

    private UserDao userDao;

    private String userId;

    private Integer numBestScores;

    public GetUserStatisticsCms(GameDao gameDao, UserDao userDao, String userId, Integer numBestScores) {
        this.gameDao = gameDao;
        this.userDao = userDao;
        this.userId = userId;
        this.numBestScores = numBestScores != null ? numBestScores : DEFAULT_NUM_BEST_SCORES;
    }

    public ApiResponse<UserStatistics> execute() {
        ApiResponse<UserStatistics> retVal;

        ApiResponse<List<Game>> allUserGamesResponse = gameDao.getAllUserGames(userId);

        if (allUserGamesResponse.isSucceeded()) {
            ApiResponse<List<User>> bestScoreUsersResponse = getBestScoreUsers();

            if (bestScoreUsersResponse.isSucceeded()) {
                List<Game> allUserGames = allUserGamesResponse.getValue();
                List<User> bestScoreUsers = bestScoreUsersResponse.getValue();

                int totalGames = allUserGames.size();
                int numWins =
                        allUserGames.stream().filter(e-> e.getWinnerUserId().equals(userId)).toList().size();

                UserStatistics userStatistics = new UserStatistics(bestScoreUsers, totalGames, numWins);

                retVal = ApiResponse.createSucceededResponse(userStatistics);
            }
            else {
                retVal = ApiResponse.createFailedResponse(bestScoreUsersResponse.getMsg());
            }
        }
        else {
            retVal = ApiResponse.createFailedResponse(allUserGamesResponse.getMsg());
        }

        return retVal;
    }

    private ApiResponse<List<User>> getBestScoreUsers() {
        ApiResponse<List<User>> retVal;
        ApiResponse<List<User>> usersResponse = userDao.getAll();

        if (usersResponse.isSucceeded()) {
            List<User> users = usersResponse.getValue();

            /*
            0 is default value for new player. best score is the lowest score,
            so we want to filter users that didn't played yet.
             */
            List<User> bestScoreUsers =
                    users.stream().filter(e->e.getBestScore() == 0).sorted(Comparator.comparingInt(User::getBestScore)).limit(numBestScores).collect(Collectors.toList());

            retVal = ApiResponse.createSucceededResponse(bestScoreUsers);
        } else {
            retVal = usersResponse;
        }

        return retVal;
    }
}
