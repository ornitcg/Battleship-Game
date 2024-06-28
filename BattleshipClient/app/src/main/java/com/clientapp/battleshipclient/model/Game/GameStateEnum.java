package com.clientapp.battleshipclient.model.Game;

import lombok.Getter;

@Getter
public enum GameStateEnum {
    IN_PROGRESS("inProgress"),
    FINISHED("finished"),
    ENDED("ended"),
    PAUSED("paused");

    private final String name;

    GameStateEnum(String name) {
        this.name = name;
    }

    public static GameStateEnum fromString(String name) {
        GameStateEnum retVal = null;

        for (GameStateEnum state : GameStateEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                retVal = state;
            }
        }

        return retVal;
    }
}
