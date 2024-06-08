package com.battleship.BattleshipServer.logic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameCreated {
    private String gameId;
    private LocalDateTime creationTime;
}
