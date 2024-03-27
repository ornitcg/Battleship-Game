package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.GetUsersCmd;
import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SignInSignUpResource {

    @Autowired
    private UserDao userDao;

    @GetMapping("/user")
    public List<User> getUsers() {
        List<User> retVal;

        GetUsersCmd cmd = new GetUsersCmd(userDao);
        retVal = cmd.execute();

        return retVal;
    }

//    @PostMapping("/user")
//    public String createUser(@RequestBody User user) {
//        return uDao.save(user) + " No. of raws saved to db";
//    }
}
