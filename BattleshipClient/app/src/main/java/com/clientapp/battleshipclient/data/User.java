package com.clientapp.battleshipclient.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private String id;
    private String name;
    private String password;
    private int score;




    public User(String username, String password) { // create a new user
        this.name = username;
        this.password = password;
    }

    public User(String userId, String username, int score) { // create a new user
        this.name = username;
        this.id = userId;
        this.score = score;

    }

}
