package com.clientapp.battleshipclient.logic;

import android.graphics.drawable.GradientDrawable;

public class Ship {
    private int size;
    private int hits;
    private boolean isSunk;
    private boolean isPlaced;
    private int edgePosition;
    private Orientation orientation;

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public Ship(int size, int edgePosition, Orientation orientation) {
        this.size = size;
        this.hits = 0;
        this.isSunk = false;
        this.isPlaced = false;
        this.edgePosition = edgePosition;
        this.orientation = orientation;
    }

    public void hit() {
        hits++;
        if (hits == size) {
            isSunk = true;
        }
    }

    public boolean isSunk() {
        return isSunk;
    }
}
