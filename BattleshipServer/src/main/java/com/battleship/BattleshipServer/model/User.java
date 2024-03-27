package com.battleship.BattleshipServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;

    private String name;

    private String password;

    private Integer bestScore;
}
