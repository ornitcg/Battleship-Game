package com.battleship.BattleshipServer.commands.signInSignUp;

import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.model.User;
import com.battleship.BattleshipServer.resources.ApiResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/*
*  This Class is responsible for creating a new user if it does not exist.
* */
@RestController
public class SignUpCmd {

    private UserDao userDao;
    private User userToCreate;
    private static Lock createIfNotExistLock = new ReentrantLock();

    public SignUpCmd(UserDao userDao, User userToCreate) {
        this.userDao = userDao;
        this.userToCreate = userToCreate;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal;
        ApiResponse<List<User>> getUsersResponse = userDao.getAll();

        if (getUsersResponse.isSucceeded()) {
            retVal = createUserIfNotExist(getUsersResponse.getValue());
        }
        else {
            retVal = ApiResponse.createFailedResponse(getUsersResponse.getMsg());
        }

        return retVal;
    }

    /*
    *  This method is used to create a new user if it does not exist,
    *  or returns an error if it does.
    * */
    private ApiResponse<String> createUserIfNotExist(List<User> users) {
        ApiResponse<String> retVal;

        try {
            createIfNotExistLock.lock();
            boolean isUserExist = users.stream().anyMatch(e -> e.getName().equals(userToCreate.getName()));

            if (isUserExist == false) {
                ApiResponse<String> createResponse = userDao.create(userToCreate);

                if (createResponse.isSucceeded()) {
                    retVal = ApiResponse.createSucceededResponse(createResponse.getValue());
                }
                else {
                    retVal = ApiResponse.createFailedResponse(createResponse.getMsg());
                }
            }
            else {
                retVal = ApiResponse.createFailedResponse("User name already exist");
            }

        } finally {
            createIfNotExistLock.unlock();
        }

        return retVal;
    }
}
