package com.clientapp.battleshipclient.model.Ship;
//this class represents the basic data of a ship

import androidx.annotation.NonNull;

import java.io.Serializable;

import lombok.Data;

@Data
public class BasicShip implements Serializable {
    private String id ; // ship id
    private Integer edgePosition = -1;
    private ShipTypeEnum type;
    private OrientationEnum orientation;
    private Integer size;


    //changes the orientation data of  ship from horizontal to vertical and vice versa
    public void changeOrientation() {
        if (orientation == OrientationEnum.HORIZONTAL) {
            orientation = OrientationEnum.VERTICAL;
        } else {
            orientation = OrientationEnum.HORIZONTAL;
        }
    }

    //resets the orientation data of the ship to vertical
    public void resetOrientation() {
        this.orientation = OrientationEnum.VERTICAL;
    }


    //returns the ship's data as a string. used for debugging purposes
    @NonNull
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", position=" + edgePosition +
                ", type=" + type +
                ", orientation=" + orientation +
                ", size=" + size +"}";
    }
}
