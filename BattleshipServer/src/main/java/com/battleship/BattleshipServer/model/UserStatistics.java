package com.battleship.BattleshipServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatistics {

    private List<User> bestScoreUsers;

    private Integer userBestScore;

    private Integer totalGames;

    private Integer numWins;
}
