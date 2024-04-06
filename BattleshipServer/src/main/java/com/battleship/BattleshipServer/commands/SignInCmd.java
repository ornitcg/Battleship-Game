package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.model.User;
import com.battleship.BattleshipServer.resources.ApiResponse;

public class SignInCmd {
    private UserDao userDao;
    private User user;

    public SignInCmd(UserDao userDao, User user) {
        this.userDao = userDao;
        this.user = user;
    }

    public ApiResponse<User> execute() {
        ApiResponse<User> retVal;
        String userName = user.getName();

        ApiResponse<User> userApiResponse = userDao.get(userName);

        /*
        If the request succeeded, or if it failed (user doesn't exist/failed get response from db) -
        Then we want to return the response from db
         */
        retVal = userApiResponse;

        User realUser = userApiResponse.getValue();

        if (realUser != null) {
            String realUserPassword = realUser.getPassword();
            String passwordFromRequest = user.getPassword();

            // if it wrong password, we want to build different response
            if (realUserPassword.equals(passwordFromRequest) == false) {
                String msg = String.format("Wrong password for user %s", userName);
                retVal = ApiResponse.createFailedResponse(msg);
            }
        }

        return retVal;
    }
}
