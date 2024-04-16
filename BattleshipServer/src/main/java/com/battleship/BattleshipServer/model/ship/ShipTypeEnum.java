package com.battleship.BattleshipServer.model.ship;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ShipTypeEnum {
    //todo yuvalg - add types when had them
   TYPE1("type1");

    private final String name;

    ShipTypeEnum(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static ShipTypeEnum fromString(String name) {
        for (ShipTypeEnum state : ShipTypeEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + name);
    }
}
