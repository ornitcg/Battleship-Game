package com.clientapp.battleshipclient.view.view_utils;

/*
*  ExtrasEnum class
*  This class is used to define the extras that are passed between activities
* */
public enum ExtrasEnum {

    GAME_BOARD("gameBoard"),
    CURRENT_PLAYER("currentPlayer"),
    SHOULD_RESUME_MUSIC("shouldResumeMusic"),
    MUSIC_NAME("musicName"),
    CURRENT_STATS("currentStats"), GAME_ID("gameId");


    private final String name;

    ExtrasEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public static ExtrasEnum fromString(String name) {
        ExtrasEnum retVal = null;

        for (ExtrasEnum state : ExtrasEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                retVal = state;
            }
        }

        return retVal;
    }

}
