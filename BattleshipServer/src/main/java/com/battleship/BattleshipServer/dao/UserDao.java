package com.battleship.BattleshipServer.dao;

import com.battleship.BattleshipServer.logic.IdGenerator;
import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
*  This class is responsible for all the operations related to the user table in the database.
* */
@Repository
public class UserDao implements IDao<User> {

    private static final String USERS_TABLE_NAME = "users ";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ApiResponse<String> create(User user) {
        ApiResponse<String> retVal;
        String userId = IdGenerator.generate(user);

        try {
            jdbcTemplate.update(INSERT + USERS_TABLE_NAME + "(id, name, password, bestScore) VALUES (?, ?, ?, ?)",
                    new Object[] {userId, user.getName(), user.getPassword(), 0}); //initiate best score to 0
            retVal = ApiResponse.createSucceededResponse(userId);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ApiResponse<String> update(User user, String userId) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(UPDATE + USERS_TABLE_NAME + "SET name=?, password=?, bestScore=? " + WHERE_ID, new Object[] {user.getName(), user.getPassword(), user.getBestScore(), userId});
            retVal = ApiResponse.createSucceededResponse(userId);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<String> updateBestScore(String userId, int bestScore) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(UPDATE + USERS_TABLE_NAME + "SET bestScore=? " + WHERE_ID, new Object[] {bestScore, userId});
            retVal = ApiResponse.createSucceededResponse(userId);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ApiResponse<String> delete(String userId) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(DELETE + USERS_TABLE_NAME + WHERE_ID, userId);
            retVal = ApiResponse.createSucceededResponse(userId);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<List<User>> getAll() {
        ApiResponse<List<User>> retVal;

        try {
            List<User> users = jdbcTemplate.query(SELECT + USERS_TABLE_NAME, new BeanPropertyRowMapper<User>(User.class));
            retVal = ApiResponse.createSucceededResponse(users);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<User> get(String userId) {
        ApiResponse<User> retVal;

        try {
            User user = jdbcTemplate.queryForObject(SELECT + USERS_TABLE_NAME + WHERE_ID ,
                    new BeanPropertyRowMapper<User>(User.class), userId);
            retVal = ApiResponse.createSucceededResponse(user);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<User> getUserByName(String userName) {
        ApiResponse<User> retVal;

        try {
            User user = jdbcTemplate.queryForObject(SELECT + USERS_TABLE_NAME + "WHERE name=?" ,
                    new BeanPropertyRowMapper<User>(User.class), userName);
            retVal = ApiResponse.createSucceededResponse(user);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }
}
