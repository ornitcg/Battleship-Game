package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.signInSignUp.KeepUserAliveCmd;
import com.battleship.BattleshipServer.commands.signInSignUp.SignInCmd;
import com.battleship.BattleshipServer.commands.signInSignUp.SignUpCmd;
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

    /*
    *  This endpoint is used to sign in a user.
    *  To sign in a user, you need to provide the user object in the request body.
    *  {
    *      "name": "Dan",
    *      "password": "12345"
    *  }
    */
    @PostMapping("/signIn")
    public ApiResponse<String> signIn(@RequestBody User user) {
        ApiResponse<String> retVal;

        SignInCmd cmd = new SignInCmd(userDao, user);
        retVal = cmd.execute();

        return retVal;
    }

    /*
    *  This endpoint is used to sign up a new user.
    *  To sign up a new user, you need to provide the user object in the request body.
    *  {
    *     "name": "Dan",
    *     "password": "12345"
    *  }
    */
    @PostMapping("/signUp")
    private ApiResponse<String> signUp(@RequestBody User userToCreate) {
        ApiResponse<String> retVal;

        SignUpCmd cmd = new SignUpCmd(userDao, userToCreate);
        retVal = cmd.execute();

        return retVal;
    }

    /*
    *  This endpoint is used to keep the user alive (in game).
    *  To keep the user alive, you need to provide userId in path params.
    *  This is used to prevent that same user will log-in from different devices
    *  at the same time.
    */
    @PutMapping("keepUserAlive/{userId}")
    private ApiResponse<String> keepUserAlive(@PathVariable String userId) {
        ApiResponse<String> retVal;

        KeepUserAliveCmd cmd = new KeepUserAliveCmd(userId);
        retVal = cmd.execute();

        return retVal;
    }
}
