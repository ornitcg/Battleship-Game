package com.battleship.BattleshipServer.dao;

import com.battleship.BattleshipServer.model.tile.DbTile;
import com.battleship.BattleshipServer.model.tile.Tile;
import com.battleship.BattleshipServer.model.tile.TileConverter;
import com.battleship.BattleshipServer.resources.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TileDao implements IDao<Tile> {

    private static final String TILES_TABLE_NAME = "tiles ";

    private static final String WHERE_BOARD_ID = "WHERE boardId=?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ApiResponse<String> create(Tile tile) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(INSERT + TILES_TABLE_NAME + "(position, boardId, state) VALUES (?, ?, ?)",
                    new Object[] {tile.getPosition(), tile.getBoardId(), tile.getState()});
            retVal = ApiResponse.createSucceededResponse(String.format("tile with position %d on board %s", tile.getPosition(), tile.getBoardId()));
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<String> createBoardTails(ArrayList<Tile> tiles, String boardId) {
        ApiResponse<String> retVal;

        try {
            List<Object[]> batchArgs = new ArrayList<>();

            for (Tile tile : tiles) {
                Object[] args = new Object[] { tile.getPosition(), tile.getBoardId(), tile.getState().getName() };
                batchArgs.add(args);
            }

            jdbcTemplate.batchUpdate(INSERT + TILES_TABLE_NAME + "(position, boardId, state) VALUES (?, ?, ?)",
                    batchArgs);
            retVal = ApiResponse.createSucceededResponse(String.format("tile created for board %s", boardId));
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(e.getMessage());
        }

        return retVal;
    }

    @Override
    public ApiResponse<String> update(Tile tile, String boardId) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(UPDATE + TILES_TABLE_NAME + "SET position=?, boardId=?, state=? " + "WHERE position=? AND boardId=?",
                    new Object[] {tile.getPosition(), boardId, tile.getState().getName(), tile.getPosition(), boardId});
            retVal = ApiResponse.createSucceededResponse(String.format("tile with position %d on board %s", tile.getPosition(), tile.getBoardId()));
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(e.getMessage());
        }

        return retVal;
    }

    @Override
    public ApiResponse<String> delete(String boardId) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(DELETE + TILES_TABLE_NAME + WHERE_BOARD_ID, boardId);
            retVal = ApiResponse.createSucceededResponse(String.format("deleted all board %s tiles", boardId));
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<List<Tile>> getBoardTiles(String boardId) {
        ApiResponse<List<Tile>> retVal;

        try {
            List<DbTile> dbTiles = jdbcTemplate.query(SELECT + TILES_TABLE_NAME + WHERE_BOARD_ID,
                    new BeanPropertyRowMapper<DbTile>(DbTile.class), boardId);

            List<Tile> tiles = dbTiles.stream().map(TileConverter::fromDb).toList();
            retVal = ApiResponse.createSucceededResponse(tiles);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(e.getMessage());
        }

        return retVal;
    }
}
