package com.clientapp.battleshipclient.utils;



/*
*  This enum is used to store the names of the audio files
*  It is used to play the audio files in the app
* */
public enum AudioEnum {

    PLACE_SHIPS("placeShips") ,
    GAME_MUSIC("game"),
    WIN_MUSIC("win"),
    LOSE_MUSIC( "lose"),
    LEADERBOARD_MUSIC( "leaderboard"),
    LOBBY_MUSIC("lobby"),
    BUTTON("button"),
    GAME_OVER("gameOver"),
    WIN("win"),
    LOSE_SOUND("lose"),
    HIT("hit"),
    MISS("miss"),
    SUNK("sunk");



    private final String name;

    AudioEnum(String name) {
        this.name = name;
    }

//    @JsonValue
    public String getName() {
        return name;
    }

//    @JsonCreator
    public static AudioEnum fromString(String name) {
        AudioEnum retVal = null;

        for (AudioEnum state : AudioEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                retVal = state;
            }
        }

        return retVal;
    }
}
