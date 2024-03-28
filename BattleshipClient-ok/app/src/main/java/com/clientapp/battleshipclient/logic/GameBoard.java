package com.clientapp.battleshipclient.logic;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameBoard {
    private String userId;
    private ArrayList<Tile> tilesList;
    private int totalTilesOccupied;
    private int totalTilesMissed;
    private int totalTilesHit;



    public GameBoard(String userId){
        this.userId = userId;
        this.totalTilesOccupied = 0;
        this.totalTilesMissed = 0;
        this.totalTilesHit = 0;

        for (int i = 0; i < 100; i++){
            this.tilesList.add(new Tile(i));
        }

    }

    public void setTile(int Position, Tile tile){
        int x = Position / 10;
        int y = Position % 10;
    }

    public Tile getTile(int Position){
        int x = Position / 10;
        int y = Position % 10;
        return null;
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


    public ArrayList<Tile> getTilesList() {
        return tilesList;
    }
}
