package com.battleship.BattleshipServer.dao;

import com.battleship.BattleshipServer.logic.IdGenerator;
import com.battleship.BattleshipServer.logic.ResponseFromDb;
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
    public ResponseFromDb<Integer> save(Game game) {
        ResponseFromDb<Integer> retVal;
        String gameId = IdGenerator.generate(game);

        try {
            int value = jdbcTemplate.update(INSERT + GAMES_TABLE_NAME + "(id, roomId, startTime, endTime, gameState, " + "winnerUserId, looserUserId) VALUES (?, ?, ?, ?)", new Object[]{gameId, game.getRoomId(), game.getStartTime(), game.getEndTime(), game.getGameState().name(), game.getWinnerUserId(), game.getLooserUserId()});
            retVal = ResponseFromDb.createSucceededResponse(value);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<Integer> update(Game game, String gameId) {
        ResponseFromDb<Integer> retVal;

        try {
            int value = jdbcTemplate.update(UPDATE + GAMES_TABLE_NAME + "SET roomId=?, startTime=?, endTime=?, gameState=?, winnerUserId=?, looserUserId=? " + WHERE, new Object[]{game.getRoomId(), game.getStartTime(), game.getEndTime(), game.getGameState().name(), game.getWinnerUserId(), game.getLooserUserId(), gameId});
            retVal = ResponseFromDb.createSucceededResponse(value);
        } catch (EmptyResultDataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<Integer> delete(String gameId) {
        ResponseFromDb<Integer> retVal;

        try {
            int value = jdbcTemplate.update(DELETE + GAMES_TABLE_NAME + WHERE, gameId);
            retVal = ResponseFromDb.createSucceededResponse(value);
        } catch (EmptyResultDataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<List<Game>> getAll() {
        ResponseFromDb<List<Game>> retVal;

        try {
            List<Game> games = jdbcTemplate.query(SELECT + GAMES_TABLE_NAME, new BeanPropertyRowMapper<Game>(Game.class));
            retVal = ResponseFromDb.createSucceededResponse(games);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<Game> get(String gameId) {
        ResponseFromDb<Game> retVal;

        try {
            Game game = jdbcTemplate.queryForObject(SELECT + GAMES_TABLE_NAME + WHERE,
                    new BeanPropertyRowMapper<Game>(Game.class), gameId);
            retVal = ResponseFromDb.createSucceededResponse(game);
        } catch (EmptyResultDataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }
}
