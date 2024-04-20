package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.GetUserStatisticsCms;
import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.model.UserStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaderboardResource {

    @Autowired
    private GameDao gameDao;

    @Autowired
    private UserDao userDao;

    /*
    To get user statistics, you need to provide userId in path params.
    If you want a specific amount of best score users, add a query param numBestScores to the request.
    Default will return 10 best score users.
     */
    @GetMapping("/userStatistics")
    private ApiResponse<UserStatistics> getUserStatistics(@RequestParam String userId,
                                                          @RequestParam(value = "numBestScores", required = false) Integer numBestScores) {
        ApiResponse<UserStatistics> retVal;

        GetUserStatisticsCms cmd = new GetUserStatisticsCms(gameDao, userDao, userId, numBestScores);
        retVal = cmd.execute();

        return retVal;
    }
}
