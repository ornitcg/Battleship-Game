package com.battleship.BattleshipServer.model.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbGame {
    private String id;

    private String turnUserId;

    private LocalDateTime  startTime;

    private LocalDateTime endTime;

    private String gameState;

    private String winnerUserId;

    private String looserUserId;
}
