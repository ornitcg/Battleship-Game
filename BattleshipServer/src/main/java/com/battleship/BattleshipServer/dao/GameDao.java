package com.battleship.BattleshipServer.dao;

import com.battleship.BattleshipServer.logic.IdGenerator;
import com.battleship.BattleshipServer.model.game.DbGame;
import com.battleship.BattleshipServer.model.game.GameConverter;
import com.battleship.BattleshipServer.model.game.GameStateEnum;
import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.model.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
            jdbcTemplate.update(INSERT + GAMES_TABLE_NAME + "(id, turnUserId, startTime, endTime, gameState, " +
                    "winnerUserId, looserUserId) VALUES (?, ?, ?, ?, ?, ?, ?)", new Object[]{gameId,
                    game.getTurnUserId()
                    , game.getStartTime(), game.getEndTime(), game.getGameState().getName(), game.getWinnerUserId(),
                    game.getLooserUserId()});
            retVal = ApiResponse.createSucceededResponse(gameId);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(e.getMessage());
        }

        return retVal;
    }

    @Override
    public ApiResponse<String> update(Game game, String gameId) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(UPDATE + GAMES_TABLE_NAME + "SET turnUserId=?,endTime=?, gameState=?, " +
                    "winnerUserId=?, looserUserId=? " + WHERE_ID, new Object[]{game.getTurnUserId(), game.getEndTime(), game.getGameState().getName(), game.getWinnerUserId(), game.getLooserUserId(), gameId});
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

    public ApiResponse<List<Game>> getAllUserGames(String userId) {
        ApiResponse<List<Game>> retVal;

        try {
            List<DbGame> winningGames = jdbcTemplate.query(SELECT + GAMES_TABLE_NAME + "WHERE winnerUserId=?",
                    new BeanPropertyRowMapper<DbGame>(DbGame.class), userId);

            try {
                List<DbGame> loosingGames = jdbcTemplate.query(SELECT + GAMES_TABLE_NAME + "WHERE looserUserId=?",
                        new BeanPropertyRowMapper<DbGame>(DbGame.class), userId);

                List<Game> allUserGames = new LinkedList<>();
                allUserGames.addAll(winningGames.stream().map(GameConverter::fromDb).toList());
                allUserGames.addAll(loosingGames.stream().map(GameConverter::fromDb).toList());

                retVal = ApiResponse.createSucceededResponse(allUserGames);
            } catch (DataAccessException e) {
                retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
            }
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<Game> get(String gameId) {
        ApiResponse<Game> retVal;

        try {
            DbGame dbGame = jdbcTemplate.queryForObject(SELECT + GAMES_TABLE_NAME + WHERE_ID,
                    new BeanPropertyRowMapper<DbGame>(DbGame.class), gameId);

            Game game = GameConverter.fromDb(dbGame);
            retVal = ApiResponse.createSucceededResponse(game);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<Game> getGameForUser(String userId) {
        ApiResponse<Game> retVal;

        try {
            DbGame dbGame = jdbcTemplate.queryForObject(SELECT + GAMES_TABLE_NAME + "WHERE turnUserId=? and gameState=?",
                    new BeanPropertyRowMapper<DbGame>(DbGame.class), userId, GameStateEnum.IN_PROGRESS.getName());

            Game game = GameConverter.fromDb(dbGame);
            retVal = ApiResponse.createSucceededResponse(game);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }
}
