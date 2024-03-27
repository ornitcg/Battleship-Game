package com.battleship.BattleshipServer.dao;

import com.battleship.BattleshipServer.logic.IdGenerator;
import com.battleship.BattleshipServer.logic.ResponseFromDb;
import com.battleship.BattleshipServer.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomDao implements IDao<Room> {

    private static final String ROOMS_TABLE_NAME = "rooms ";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ResponseFromDb<Integer> save(Room room) {
        ResponseFromDb<Integer> retVal;
        String roomId = IdGenerator.generate(room);

        try {
            int value = jdbcTemplate.update(INSERT + ROOMS_TABLE_NAME + "(id, name, status, userId1, userId2) VALUES (?, ?, ?, ?, ?)", new Object[]{roomId, room.getName(), room.getStatus().name(), room.getUserId1(), room.getUserId2()});
            retVal = ResponseFromDb.createSucceededResponse(value);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<Integer> update(Room room, String roomId) {
        ResponseFromDb<Integer> retVal;

        try {
            int value = jdbcTemplate.update(UPDATE + ROOMS_TABLE_NAME + "SET name=?, status=?, userId1=?, userId2=? " + WHERE, new Object[]{room.getName(), room.getStatus().name(), room.getUserId1(), room.getUserId2(), roomId});
            retVal = ResponseFromDb.createSucceededResponse(value);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<Integer> delete(String roomId) {
        ResponseFromDb<Integer> retVal;

        try {
            int value = jdbcTemplate.update(DELETE + ROOMS_TABLE_NAME + WHERE, roomId);
            retVal = ResponseFromDb.createSucceededResponse(value);
        } catch (EmptyResultDataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<List<Room>> getAll() {
        ResponseFromDb<List<Room>> retVal;

        try {
            List<Room> rooms = jdbcTemplate.query(SELECT + ROOMS_TABLE_NAME, new BeanPropertyRowMapper<Room>(Room.class));
            retVal = ResponseFromDb.createSucceededResponse(rooms);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }

    @Override
    public ResponseFromDb<Room> get(String roomId) {
        ResponseFromDb<Room> retVal;

        try {
            Room room = jdbcTemplate.queryForObject(SELECT + ROOMS_TABLE_NAME + WHERE, new BeanPropertyRowMapper<Room>(Room.class), roomId);
            retVal = ResponseFromDb.createSucceededResponse(room);
        } catch (EmptyResultDataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(NOT_EXISTS_ERROR_MSG);
        } catch (DataAccessException e) {
            retVal = ResponseFromDb.createFailedResponse(GENERAL_ERROR_MSG);
        }

        return retVal;
    }
}
