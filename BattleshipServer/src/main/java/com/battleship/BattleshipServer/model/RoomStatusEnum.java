package com.battleship.BattleshipServer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RoomStatusEnum {
    @JsonProperty("empty") EMPTY,
    @JsonProperty("oneUser") ONE_USER,
    @JsonProperty("full") FULL,
}
