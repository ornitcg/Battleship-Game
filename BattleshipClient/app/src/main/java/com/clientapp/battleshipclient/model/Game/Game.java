package com.clientapp.battleshipclient.model.Game;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game implements Serializable {
    private String id;

    private String turnUserId;

    private GameStateEnum gameState;

    private String winnerUserId;

}