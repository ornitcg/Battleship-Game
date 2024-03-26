package com.clientapp.battleshipclient.logic;

import android.view.View;

public class Tile {

    private Status tileStatus;
    private int position;
    private boolean isFired;
    private boolean isSunk;


    public Tile(int position){
        this.tileStatus = Status.EMPTY;
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
        EMPTY,HIT,MISS,OCCUPIED,SUNK
    }

    public Status getTileStatus(){
        return tileStatus;
    }


}
