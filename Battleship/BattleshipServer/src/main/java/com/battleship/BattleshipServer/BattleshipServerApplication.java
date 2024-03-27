package com.battleship.BattleshipServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.battleship.BattleshipServer.model", "com.battleship.BattleshipServer.resources", "com.battleship.BattleshipServer.dao"})
public class BattleshipServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BattleshipServerApplication.class, args);
    }

}
