package com.battleship.BattleshipServer.dao;

import com.battleship.BattleshipServer.resources.ApiResponse;

public interface IDao<T> {
    String SELECT = "SELECT * FROM ";
    String WHERE_ID = "WHERE id=?";
    String INSERT = "INSERT INTO ";
    String UPDATE = "UPDATE ";

    String DELETE = "DELETE FROM ";

    String GENERAL_ERROR_MSG = "Failed to execute query";
    String NOT_EXISTS_ERROR_MSG = "Entity doesn't exist";


    ApiResponse<String> create(T entity);

    ApiResponse<String> update(T entity, String id);

    ApiResponse<String> delete(String id);
}
