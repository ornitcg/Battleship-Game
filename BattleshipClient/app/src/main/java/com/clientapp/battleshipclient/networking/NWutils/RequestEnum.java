package com.clientapp.battleshipclient.networking.NWutils;

public enum RequestEnum {
    CREATE_GAME("createGame"),
    POST_ATTACK("postAttack"),
    GET_GAME("getGame"),
    CREATE_BOARD("createBoard"),
    GET_BOARD("getBoard"),
    CANCEL_MATCH("cancelMatch"),
    PAUSE_GAME("pauseGame"),
    RESUME_GAME("resumeGame"),
    END_GAME("endGame"),
    KEEP_GAME_ALIVE("keepGameAlive");


    private final String name;

    RequestEnum(String name) {
        this.name = name;
    }

    public Object getName() {
        return name;
    }

    public static RequestEnum fromString(String name) {
        RequestEnum retVal = null;

        for (RequestEnum state : RequestEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                retVal = state;
            }
        }

        return retVal;
    }


}
