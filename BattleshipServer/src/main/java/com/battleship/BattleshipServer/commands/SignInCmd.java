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

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal;
        String userName = user.getName();

        ApiResponse<User> userApiResponse = userDao.getUserByName(userName);
        User realUser = userApiResponse.getValue();

        if (realUser != null) {
            String realUserPassword = realUser.getPassword();
            String passwordFromRequest = user.getPassword();

            // if it wrong password, we want to build different response
            if (realUserPassword.equals(passwordFromRequest) == false) {
                String msg = String.format("Wrong password for user %s", userName);
                retVal = ApiResponse.createFailedResponse(msg);
            }
            else {
                retVal = ApiResponse.createSucceededResponse(realUser.getId());
            }
        }
        else { //request failed
            retVal = ApiResponse.createFailedResponse(userApiResponse.getMsg());
        }

        return retVal;
    }
}
