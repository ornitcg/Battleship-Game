package com.battleship.BattleshipServer.dao;

import com.battleship.BattleshipServer.logic.IdGenerator;
import com.battleship.BattleshipServer.model.User;
import com.battleship.BattleshipServer.model.ship.DbShip;
import com.battleship.BattleshipServer.model.ship.Ship;
import com.battleship.BattleshipServer.model.ship.ShipConverter;
import com.battleship.BattleshipServer.model.tile.Tile;
import com.battleship.BattleshipServer.resources.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ShipDao implements IDao<Ship> {
    private static final String SHIPS_TABLE_NAME = "ships ";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ApiResponse<String> create(Ship ship) {
        ApiResponse<String> retVal;

        String shipId = IdGenerator.generate(ship);

        try {
            jdbcTemplate.update(INSERT + SHIPS_TABLE_NAME + "(id, boardId, position, type, orientation, size, numHits) VALUES (?, ?, ?, ?, ?, ?)",
                    new Object[] {shipId, ship.getBoardId(), ship.getPosition(), ship.getType().getName(), ship.getOrientation().getName(),
                    ship.getSize(), ship.getNumHits()});
            retVal = ApiResponse.createSucceededResponse(shipId);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<List<Ship>> createBoardShips(ArrayList<Ship> ships, String boardId) {
        ApiResponse<List<Ship>> retVal;

        try {
            List<Object[]> batchArgs = new ArrayList<>();

            for (Ship ship : ships) {
                String shipId = IdGenerator.generate(ship);
                ship.setId(shipId);

                Object[] args = new Object[] {ship.getId(), boardId, ship.getPosition(), ship.getType().getName(),
                        ship.getOrientation().getName(),
                        ship.getSize(), ship.getNumHits() };
                batchArgs.add(args);
            }

            jdbcTemplate.batchUpdate(INSERT + SHIPS_TABLE_NAME + "(id, boardId, position, type, orientation, size, numHits) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    batchArgs);
            retVal = ApiResponse.createSucceededResponse(ships);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(e.getMessage());
        }

        return retVal;
    }

    @Override
    public ApiResponse<String> update(Ship ship, String shipId) {
        ApiResponse<String> retVal;

        try {
            jdbcTemplate.update(UPDATE + SHIPS_TABLE_NAME + "SET size=?, numHits=? " + WHERE_ID,
                    new Object[]{ship.getSize(), ship.getNumHits(), shipId});
            retVal = ApiResponse.createSucceededResponse(shipId);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<Ship> get(String shipId) {
        ApiResponse<Ship> retVal;

        try {
            DbShip dbShip = jdbcTemplate.queryForObject(SELECT + SHIPS_TABLE_NAME + WHERE_ID,
                    new BeanPropertyRowMapper<DbShip>(DbShip.class), shipId);
            Ship ship = ShipConverter.fromDb(dbShip);
            retVal = ApiResponse.createSucceededResponse(ship);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    public ApiResponse<List<Ship>> getBoardShips(String boardId) {
        ApiResponse<List<Ship>> retVal;

        try {
            List<DbShip> dbShips = jdbcTemplate.query(SELECT + SHIPS_TABLE_NAME + "WHERE boardId=?",
                    new BeanPropertyRowMapper<DbShip>(DbShip.class), boardId);
            List<Ship> ships = dbShips.stream().map(ShipConverter::fromDb).toList();
            retVal = ApiResponse.createSucceededResponse(ships);
        } catch (EmptyResultDataAccessException e) {
            retVal = ApiResponse.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ApiResponse.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    //not relevant for this table
    @Override
    public ApiResponse<String> delete(String id) {
        return null;
    }
}
