package com.clientapp.battleshipclient.data.Ship;
//this class represents the basic data of a ship

import java.io.Serializable;

import lombok.Data;

@Data
public class BasicShip implements Serializable {
    private String id ; // ship id
    private String boardId = "";
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
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", boardId=" + boardId +
                ", position=" + edgePosition +
                ", type=" + type +
                ", orientation=" + orientation +
                ", size=" + size +"}";
    }
}
