package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.KeepUserAliveCmd;
import com.battleship.BattleshipServer.commands.SignInCmd;
import com.battleship.BattleshipServer.commands.SignUpCmd;
import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SignInSignUpResource {

    @Autowired
    private UserDao userDao;

    public static Map<String, LocalDateTime> loggedInUsers = new HashMap<>();

    public static final Object loggedInUsersLock = new Object();

    @PostMapping("/signIn")
    public ApiResponse<String> signIn(@RequestBody User user) {
        ApiResponse<String> retVal;

        SignInCmd cmd = new SignInCmd(userDao, user);
        retVal = cmd.execute();

        return retVal;
    }

    @PostMapping("/signUp")
    private ApiResponse<String> signUp(@RequestBody User userToCreate) {
        ApiResponse<String> retVal;

        SignUpCmd cmd = new SignUpCmd(userDao, userToCreate);
        retVal = cmd.execute();

        return retVal;
    }

    @PutMapping("keepUserAlive/{userId}")
    private ApiResponse<String> keepUserAlive(@PathVariable String userId) {
        ApiResponse<String> retVal;

        KeepUserAliveCmd cmd = new KeepUserAliveCmd(userId);
        retVal = cmd.execute();

        return retVal;
    }

//    @GetMapping("/user")
//    public ResponseEntity<Object> getUsers() {
//        ResponseEntity<Object> retVal;
//
//        GetUsersCmd cmd = new GetUsersCmd(userDao);
//        retVal = cmd.execute();
//
//        return retVal;
//    }
}
