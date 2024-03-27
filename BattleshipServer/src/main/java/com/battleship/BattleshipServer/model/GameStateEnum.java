package com.battleship.BattleshipServer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GameStateEnum {
    @JsonProperty("inProgress") IN_PROGRESS,
    @JsonProperty("active") ACTIVE,
    @JsonProperty("finished") FINISHED,
}
