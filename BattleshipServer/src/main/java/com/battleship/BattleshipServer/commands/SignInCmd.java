package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.model.User;
import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.SignInSignUpResource;

import java.time.LocalDateTime;

public class SignInCmd {
    private UserDao userDao;
    private User user;

    private static final int MINUTES_AGO_TO_CONSIDER_USER_IN_SYSTEM = 1;
    private static final String USER_LOGGED_IN_ERROR_MSG = "User is already in the system";

    public SignInCmd(UserDao userDao, User user) {
        this.userDao = userDao;
        this.user = user;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal = null;
        String userName = user.getName();

        ApiResponse<User> userApiResponse = userDao.getUserByName(userName);
        User realUser = userApiResponse.getValue();

        if (realUser != null) {
            String realUserId = realUser.getId();

            synchronized (SignInSignUpResource.loggedInUsersLock) {
                LocalDateTime lastNotifiedAsAlive = SignInSignUpResource.loggedInUsers.get(realUserId);

                if (lastNotifiedAsAlive != null) { // user notified his alive
                    LocalDateTime minTimeToConsiderUserLoggedIn = LocalDateTime.now().minusMinutes(MINUTES_AGO_TO_CONSIDER_USER_IN_SYSTEM);

                    if (lastNotifiedAsAlive.isAfter(minTimeToConsiderUserLoggedIn)) {
                        retVal = ApiResponse.createFailedResponse(USER_LOGGED_IN_ERROR_MSG);
                    }
                }
            }

            if (retVal == null) {
                String realUserPassword = realUser.getPassword();
                String passwordFromRequest = user.getPassword();

                // if it wrong password, we want to build different response
                if (realUserPassword.equals(passwordFromRequest) == false) {
                    String msg = String.format("Wrong password for user %s", userName);
                    retVal = ApiResponse.createFailedResponse(msg);
                } else {
                    retVal = ApiResponse.createSucceededResponse(realUser.getId());
                }
            }
        }
        else { //request failed
            retVal = ApiResponse.createFailedResponse(userApiResponse.getMsg());
        }

        return retVal;
    }
}
