package com.clientapp.battleshipclient.model.Ship;

import lombok.Getter;

@Getter
public enum ShipTypeEnum {
    BATTLESHIP("battleship"),
    SUBMARINE("submarine"),
    CARRIER("carrier"),
    CRUISER("cruiser"),
    DESTROYER("destroyer");


    private final String name;

    ShipTypeEnum(String name) {
        this.name = name;
    }


    public static ShipTypeEnum fromString(String name) {
        ShipTypeEnum retVal = null;

        for (ShipTypeEnum state : ShipTypeEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                retVal = state;
            }
        }

        return retVal;
    }
}
