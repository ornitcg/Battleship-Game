package com.clientapp.battleshipclient.logic;

public class Tile {

    private Status tileStatus;
    private int position;
    private boolean isFired;
    private boolean isSunk;


    public Tile(int position){
        this.tileStatus = Status.SEA;
        setFired(false);
        setSunk(false);
        this.position = position;
    }



    public void setFired(boolean isFired){
        this.isFired = isFired;
    }
    private void setSunk(boolean isSunk ){
        this.isSunk = isSunk;
    }

    public enum Status {
        SEA, HIT, MISS, SHIP
    }

    public Status getTileStatus(){
        return tileStatus;
    }


}
