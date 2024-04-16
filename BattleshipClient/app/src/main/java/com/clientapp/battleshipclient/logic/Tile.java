package com.clientapp.battleshipclient.logic;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tile implements Serializable {
    private State tileState;
    private int position;
    private int shipId;
    private Ship.Orientation orientation
           ;


    public Tile(int position){
        this.tileState = State.SEA;
        this.position = position;
        this.shipId = -1;
    }

    public void setFired(boolean isFired){

        if (isFired){
            if (tileState == State.SHIP){
                tileState = State.HIT;
            }
            else if (tileState == State.SEA){
                tileState = State.MISS;
            }
        }
        if (tileState == State.SHIP){
            tileState = State.HIT;
        }
        else if (tileState == State.SEA){
            tileState = State.MISS;
        }
    }

    public void resetSingleTileData() {
        this.setTileState(State.SEA);
        this.shipId=-1;
    }

//    @Override
//    public int describeContents() { //TODO: remove this
//        return 0;
//    }

//    @Override
//    public void writeToParcel(@NonNull Parcel dest, int flags) { //TODO: remove this
//
//    }


    public enum State {
        SEA, HIT, MISS, SHIP, NEAR_SHIP, VALID_FOR_DROP, INVALID_FOR_DROP
    }

    public void setTileState(State state) {
        this.tileState = state;
    }

}
