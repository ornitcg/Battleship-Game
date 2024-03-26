package com.clientapp.battleshipclient.logic;

public class GameBoard {
    private String userName;
    private Tile[][] board;
    private int totalTilesOccupied;
    private int totalTilesMissed;
    private int totalTilesHit;

    public GameBoard(String userName){
        this.userName = userName;
        this.totalTilesOccupied = 0;
        this.totalTilesMissed = 0;
        this.totalTilesHit = 0;

        for (int i = 0; i < 10; i++){
            for (int j = 0; j < 10; j++){
                this.board[i][j] = new Tile(i*10 + j);
            }
        }

    }

    public void setTile(int Position, Tile tile){
        int x = Position / 10;
        int y = Position % 10;
        this.board[x][y] = tile;
    }

    public Tile getTile(int Position){
        int x = Position / 10;
        int y = Position % 10;
        return this.board[x][y];
    }

    public void setTotalTilesOccupied(int totalTilesOccupied){
        this.totalTilesOccupied = totalTilesOccupied;
    }

    public int getTotalTilesOccupied(){
        return this.totalTilesOccupied;
    }

    public void setTotalTilesMissed(int totalTilesMissed){
        this.totalTilesMissed = totalTilesMissed;
    }

    public int getTotalTilesMissed(){
        return this.totalTilesMissed;
    }

    public void setTotalTilesHit(int totalTilesHit){
        this.totalTilesHit = totalTilesHit;
    }

    public int getTotalTilesHit(){
        return this.totalTilesHit;
    }




}
