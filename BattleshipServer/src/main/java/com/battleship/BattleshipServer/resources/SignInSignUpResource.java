package com.battleship.BattleshipServer.resources;

import com.battleship.BattleshipServer.commands.SignInCmd;
import com.battleship.BattleshipServer.commands.SignUpCmd;
import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignInSignUpResource {

    @Autowired
    private UserDao userDao;

    @GetMapping("/signIn")
    public ApiResponse<User> signIn(@RequestBody User user) {
        ApiResponse<User> retVal;

        SignInCmd cmd = new SignInCmd(userDao, user);
        retVal = cmd.execute();

        return retVal;
    }

    @PostMapping("/signUp")
    private ApiResponse<User> signUp(@RequestBody User userToCreate) {
        ApiResponse<User> retVal;

        SignUpCmd cmd = new SignUpCmd(userDao, userToCreate);
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
