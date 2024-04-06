package com.battleship.BattleshipServer.model.game;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    private String id;

    private String turnUserId;

    private LocalDateTime  startTime;

    private LocalDateTime endTime;

    private GameStateEnum gameState;

    private String winnerUserId;

    private String looserUserId;
}