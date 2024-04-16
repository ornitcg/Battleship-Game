package com.battleship.BattleshipServer.model.ship;

import com.battleship.BattleshipServer.model.tile.TileStateEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrientationEnum {
    VERTICAL("vertical"),
    HORIZONTAL("horizontal");

    private final String name;

    OrientationEnum(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static OrientationEnum fromString(String name) {
        for (OrientationEnum state : OrientationEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + name);
    }
}
