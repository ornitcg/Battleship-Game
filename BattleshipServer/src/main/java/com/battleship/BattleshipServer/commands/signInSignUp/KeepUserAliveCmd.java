package com.battleship.BattleshipServer.commands.signInSignUp;

import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.SignInSignUpResource;

import java.time.LocalDateTime;


/*
*  This Class is responsible for keeping the user alive.
*  This is for preventing the same user to log-in from different devices
* */
public class KeepUserAliveCmd {
    private String userId;

    public KeepUserAliveCmd(String userId) {
        this.userId = userId;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal = ApiResponse.createSucceededResponse("OK");

        synchronized (SignInSignUpResource.loggedInUsersLock) {
            SignInSignUpResource.loggedInUsers.put(userId, LocalDateTime.now());
        }

        return retVal;
    }
}
