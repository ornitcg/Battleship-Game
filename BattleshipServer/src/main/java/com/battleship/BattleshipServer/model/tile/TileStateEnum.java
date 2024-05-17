package com.battleship.BattleshipServer.model.tile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TileStateEnum {
    SEA("sea"),
    SHIP("ship"),
    HIT("hit"),
    MISS("miss");

    private final String name;

    TileStateEnum(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
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
