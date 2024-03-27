package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.UserDao;
import com.battleship.BattleshipServer.logic.ResponseFromDb;
import com.battleship.BattleshipServer.model.User;

import java.util.List;

public class GetUsersCmd {
    private UserDao userDao;

    public GetUsersCmd(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> execute() {
        List<User> retVal;

        ResponseFromDb<List<User>> response = userDao.getAll();

        if (response.isSucceeded()) {
            retVal = response.getValue();
        }
        else {
            retVal = null;
            System.err.println(response.getErrorMsg());
        }

        return retVal;
    }
}
