package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.UserDao;
import org.springframework.http.ResponseEntity;

public class GetUsersCmd {
    private UserDao userDao;

    public GetUsersCmd(UserDao userDao) {
        this.userDao = userDao;
    }

    public ResponseEntity<Object> execute() {
        ResponseEntity<Object> retVal = null;

//        ApiResponse<List<User>> response = userDao.getAll();
//
//        if (response.isSucceeded()) {
//            retVal = ResponseEntity.status(HttpStatus.OK).body(response.getValue());
//        }
//        else {
//            retVal = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getErrorMsg());
//        }

        return retVal;
    }
}
