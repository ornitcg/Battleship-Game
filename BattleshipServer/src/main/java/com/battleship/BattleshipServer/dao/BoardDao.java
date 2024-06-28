package com.battleship.BattleshipServer.dao;

import com.battleship.BattleshipServer.logic.IdGenerator;
import com.battleship.BattleshipServer.model.Board;
import com.battleship.BattleshipServer.resources.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 *  This class is responsible for all the operations related to the board table in the database.
 * */
@Repository
public class BoardDao implements IDao<Board> {

    private static final String BOARDS_TABLE_NAME = "boards ";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ApiResponse<String> create(Board board) {
        ApiResponse<String> retVal;
        String boardId = IdGenerator.generate(board);

        try {
            jdbcTemplate.update(INSERT + BOARDS_TABLE_NAME + "(id, gameId, userId) VALUES (?, ?, ?)",
                    new Object[] {boardId, board.getGameId(), board.getUserId()});
            retVal = ApiResponse.createSucceededResponse(boardId);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ApiResponse<String> update(Board board, String boardId) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(UPDATE + BOARDS_TABLE_NAME + "SET gameId=?, userId=? " + WHERE_ID,
                    new Object[] {board.getGameId(), board.getUserId(), boardId});
            retVal = ApiResponse.createSucceededResponse(boardId);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ApiResponse<String> delete(String boardId) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(DELETE + BOARDS_TABLE_NAME + WHERE_ID, boardId);
            retVal = ApiResponse.createSucceededResponse(boardId);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<List<Board>> getGameBoards(String gameId) {
        ApiResponse<List<Board>> retVal;

        try {
            List<Board> boards = jdbcTemplate.query(SELECT + BOARDS_TABLE_NAME + "WHERE gameId=?",
                    new BeanPropertyRowMapper<Board>(Board.class), gameId);
            retVal = ApiResponse.createSucceededResponse(boards);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(e.getMessage());
        }

        return retVal;
    }
}

