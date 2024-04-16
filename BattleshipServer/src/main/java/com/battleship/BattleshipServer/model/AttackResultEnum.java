package com.battleship.BattleshipServer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AttackResultEnum {
    HIT("hit"),
    MISS("miss"),
    SUNK("sunk");

    private String name;

    AttackResultEnum(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static AttackResultEnum fromString(String name) {
        for (AttackResultEnum state : AttackResultEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + name);
    }
}
