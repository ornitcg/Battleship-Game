package com.battleship.BattleshipServer.model.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GameStateEnum {
    IN_PROGRESS("inProgress"),
    ACTIVE("active"),
    FINISHED("finished");

    private final String name;

    GameStateEnum(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static GameStateEnum fromString(String name) {
        for (GameStateEnum state : GameStateEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + name);
    }
}
