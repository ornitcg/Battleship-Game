package com.battleship.BattleshipServer.dao;

import com.battleship.BattleshipServer.logic.IdGenerator;
import com.battleship.BattleshipServer.logic.ResponseFromDb;
import com.battleship.BattleshipServer.model.Room;
import com.battleship.BattleshipServer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao implements IDao<User> {

    private static final String USERS_TABLE_NAME = "users ";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ResponseFromDb<Integer> save(User user) {
        ResponseFromDb<Integer> retVal;
        String userId = IdGenerator.generate(user);

        try {
            int value = jdbcTemplate.update(INSERT + USERS_TABLE_NAME + "(id, name, password, bestScore) VALUES (?, ?, ?, ?)", new Object[] {userId, user.getName(), user.getPassword(), user.getBestScore()});
            retVal = ResponseFromDb.createSucceededResponse(value);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<Integer> update(User user, String userId) {
        ResponseFromDb<Integer> retVal;

        try {
            int value = jdbcTemplate.update(UPDATE + USERS_TABLE_NAME + "SET name=?, password=?, bestScore=? " + WHERE, new Object[] {user.getName(), user.getPassword(), user.getBestScore(), userId});
            retVal = ResponseFromDb.createSucceededResponse(value);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<Integer> delete(String useId) {
        ResponseFromDb<Integer> retVal;

        try {
            int value = jdbcTemplate.update(DELETE + USERS_TABLE_NAME + WHERE, useId);
            retVal = ResponseFromDb.createSucceededResponse(value);
        } catch (EmptyResultDataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<List<User>> getAll() {
        ResponseFromDb<List<User>> retVal;

        try {
            List<User> users = jdbcTemplate.query(SELECT + USERS_TABLE_NAME, new BeanPropertyRowMapper<User>(User.class));
            retVal = ResponseFromDb.createSucceededResponse(users);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<User> get(String userId) {
        ResponseFromDb<User> retVal;

        try {
            User user = jdbcTemplate.queryForObject(SELECT + USERS_TABLE_NAME + WHERE, new BeanPropertyRowMapper<User>(User.class), userId);
            retVal = ResponseFromDb.createSucceededResponse(user);
        } catch (EmptyResultDataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }
}
