package com.clientapp.battleshipclient.model.Tile;


import lombok.Getter;

@Getter
public enum TileStateEnum {
    SEA("sea"),
    SHIP("ship"),
    HIT("hit"),
    MISS("miss"),
    NEAR_SHIP("near_ship"),
    VALID_FOR_DROP("valid_for_drop"),
    INVALID_FOR_DROP("invalid_for_drop");


    private final String name;

    TileStateEnum(String name) {
        this.name = name;
    }


    public static TileStateEnum fromString(String name) {
        TileStateEnum retVal = null;

        for (TileStateEnum state : TileStateEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                retVal = state;
            }
        }

        return retVal;
    }
}
