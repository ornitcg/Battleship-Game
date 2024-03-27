package com.battleship.BattleshipServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private String id;

    private String name;

    private RoomStatusEnum status;

    private String userId1;

    private String userId2;
}
