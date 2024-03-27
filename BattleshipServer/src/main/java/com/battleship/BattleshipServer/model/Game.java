package com.battleship.BattleshipServer.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    private String id;

    private Date startTime;

    private Date endTime;

    private GameStateEnum gameState;

    private String winnerUserId;

    private String looserUserId;
}