package com.battleship.BattleshipServer.commands.leaderboard;

import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.model.User;
import com.battleship.BattleshipServer.model.UserStatistics;
import com.battleship.BattleshipServer.model.game.Game;
import com.battleship.BattleshipServer.model.game.GameStateEnum;
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
            ApiResponse<List<User>> usersResponse = userDao.getAll();

            if (usersResponse.isSucceeded()) {
                List<User> users = usersResponse.getValue();
                List<User> allBestScoreUsers = getBestScoreUsers(users);
                List<User> limitedBestScoreUsers = allBestScoreUsers.stream().limit(numBestScores).collect(Collectors.toList());
                int rank = calcRank(allBestScoreUsers);
                List<Game> allUserGames = allUserGamesResponse.getValue();
                List<Game> allUserGamesThatFinished = allUserGames.stream().filter(e->e.getGameState() == GameStateEnum.FINISHED).collect(Collectors.toList());

                User user = getUser(users);

                int totalGames = allUserGamesThatFinished.size();
                int numWins = allUserGamesThatFinished.stream().filter(e -> userId.equals(e.getWinnerUserId())).toList().size();

                UserStatistics userStatistics = new UserStatistics(limitedBestScoreUsers, user.getBestScore(), rank, totalGames,
                        numWins);

                retVal = ApiResponse.createSucceededResponse(userStatistics);
            } else {
                retVal = ApiResponse.createFailedResponse(usersResponse.getMsg());
            }
        } else {
            retVal = ApiResponse.createFailedResponse(allUserGamesResponse.getMsg());
        }

        return retVal;
    }

    private int calcRank(List<User> allBestScoreUsers) {
        int retVal = 0;

        for (User user : allBestScoreUsers) {
            String currenUserId = user.getId();
            retVal++;

            if (userId.equals(currenUserId)) {
                break;
            }
        }

        return retVal;
    }

    private User getUser(List<User> users) {
        User retVal = users.stream().filter(e->e.getId().equals(userId)).toList().get(0);
        return retVal;
    }

    private List<User> getBestScoreUsers(List<User> users) {
        List<User> retVal;
        /*
        0 is default value for new player. best score is the lowest score,
        so we want to filter users that didn't played yet.
        */
        retVal = users.stream().filter(e -> e.getBestScore() != 0).sorted(Comparator.comparingInt(User::getBestScore)).collect(Collectors.toList());
        return retVal;
    }
}
