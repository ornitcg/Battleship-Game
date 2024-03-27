package com.battleship.BattleshipServer.dao;

import com.battleship.BattleshipServer.logic.IdGenerator;
import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GameDao implements IDao<Game> {

    private static final String GAMES_TABLE_NAME = "games ";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ApiResponse<String> create(Game game) {
        ApiResponse<String> retVal;
        String gameId = IdGenerator.generate(game);

        try {
            jdbcTemplate.update(INSERT + GAMES_TABLE_NAME + "(id, startTime, endTime, gameState, " + "winnerUserId, looserUserId) VALUES (?, ?, ?, ?)", new Object[]{gameId, game.getStartTime(), game.getEndTime(), game.getGameState().name(), game.getWinnerUserId(), game.getLooserUserId()});
            retVal = ApiResponse.createSucceededResponse(gameId);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ApiResponse<String> update(Game game, String gameId) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(UPDATE + GAMES_TABLE_NAME + "SET startTime=?, endTime=?, gameState=?, winnerUserId=?, looserUserId=? " + WHERE_ID, new Object[]{game.getStartTime(), game.getEndTime(), game.getGameState().name(), game.getWinnerUserId(), game.getLooserUserId(), gameId});
            retVal = ApiResponse.createSucceededResponse(gameId);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ApiResponse<String> delete(String gameId) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(DELETE + GAMES_TABLE_NAME + WHERE_ID, gameId);
            retVal = ApiResponse.createSucceededResponse(gameId);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<List<Game>> getAll() {
        ApiResponse<List<Game>> retVal;

        try {
            List<Game> games = jdbcTemplate.query(SELECT + GAMES_TABLE_NAME, new BeanPropertyRowMapper<Game>(Game.class));
            retVal = ApiResponse.createSucceededResponse(games);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<Game> get(String gameId) {
        ApiResponse<Game> retVal;

        try {
            Game game = jdbcTemplate.queryForObject(SELECT + GAMES_TABLE_NAME + WHERE_ID,
                    new BeanPropertyRowMapper<Game>(Game.class), gameId);
            retVal = ApiResponse.createSucceededResponse(game);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }
}
