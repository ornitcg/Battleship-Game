package com.clientapp.battleshipclient.data;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentStats implements Serializable {
    private User user;
    private int rank;
    private int bestScore;
    private int numWins;
    private int numGames;

}
