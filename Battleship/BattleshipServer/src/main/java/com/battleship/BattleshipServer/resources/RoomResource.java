package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.dao.RoomDao;
import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.model.Room;
import com.battleship.BattleshipServer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoomResource {
    @Autowired
    private RoomDao rDao;

    @GetMapping("/room")
    public List<Room> getRooms() {

//        return rDao.getAll();

        return null;
    }

    @PutMapping("/room/{roomId}")
    public String updateUser(@RequestBody Room room, @PathVariable String roomId) {
        return rDao.update(room, roomId) + " No. of raws updated in db";
    }

    @PostMapping("/room")
    public String createUser(@RequestBody Room room) {
        return rDao.save(room) + " No. of raws saved to db";
    }
}
