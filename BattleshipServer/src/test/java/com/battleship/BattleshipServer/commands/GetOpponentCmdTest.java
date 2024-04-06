package com.battleship.BattleshipServer.commands;

import org.junit.jupiter.api.Test;

class GetOpponentCmdTest {
    @Test
    public void testMatchmakingSystem() throws InterruptedException {
        GetOpponentCmd cmd1 = new GetOpponentCmd("user-123");

        // Simulate Player 1 joining and waiting for an opponent
        Thread player1Thread = new Thread(cmd1::execute);
        player1Thread.start();

        GetOpponentCmd cmd2 = new GetOpponentCmd("user-456");

        // Simulate Player 2 joining and being matched with Player 1
        Thread player2Thread = new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate delay before Player 2 joins
                cmd2.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        player2Thread.start();

        // Wait for both player threads to finish
        player1Thread.join();
        player2Thread.join();
    }

    @Test
    public void testMatchmakingSystem_2() throws InterruptedException {
        GetOpponentCmd cmd1 = new GetOpponentCmd("user-123");

        // Simulate Player 1 joining and waiting for an opponent
        Thread player1Thread = new Thread(cmd1::execute);
        player1Thread.start();

        GetOutOfWaitingListCmd stop = new GetOutOfWaitingListCmd("user-123");

        Thread player1Thread2 = new Thread(stop::execute);
        player1Thread2.start();

        GetOpponentCmd cmd2 = new GetOpponentCmd("user-456");

        // Simulate Player 2 joining and being matched with Player 1
        Thread player2Thread = new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate delay before Player 2 joins
                cmd2.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        player2Thread.start();

        GetOpponentCmd cmd3 = new GetOpponentCmd("user-789");

        // Simulate Player 2 joining and being matched with Player 1
        Thread player3Thread = new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate delay before Player 2 joins
                cmd3.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        player3Thread.start();

        // Wait for both player threads to finish
        player1Thread.join();
        player2Thread.join();
        player3Thread.join();
    }
}