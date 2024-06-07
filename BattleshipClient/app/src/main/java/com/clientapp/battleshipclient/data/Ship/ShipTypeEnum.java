package com.clientapp.battleshipclient.data.Ship;

public enum ShipTypeEnum {
    BATTLESHIP("battleship"),
    SUBMARINE("submarine"),
    CARRIER("carrier"),
    CRUISER("cruiser"),
    DESTROYER("destroyer"),
    VERTICAL("vertical"),
    HORIZONTAL("horizontal");

    private final String name;

    ShipTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
