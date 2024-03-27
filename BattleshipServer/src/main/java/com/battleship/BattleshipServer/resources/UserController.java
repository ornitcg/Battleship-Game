package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserDao uDao;

//    @GetMapping("/user")
//    public List<User> getUsers() {
////        return uDao.getAll();
//        return null;
//    }

    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable String userId) {
//        return uDao.get(userId);
        return null;
    }

//    @PostMapping("/user")
//    public String createUser(@RequestBody User user) {
//        return uDao.save(user) + " No. of raws saved to db";
//    }

    @PutMapping("/user/{userId}")
    public String updateUser(@RequestBody User user, @PathVariable String userId) {
        return uDao.update(user, userId) + " No. of raws updated in db";
    }

    @DeleteMapping("/user/{userId}")
    public String deleteUser(@PathVariable String userId) {
        return uDao.delete(userId) + " No. of raws deleted from db";
    }
}
