package com.battleship.BattleshipServer.dao;

import com.battleship.BattleshipServer.logic.ResponseFromDb;
import com.battleship.BattleshipServer.model.User;
import org.springframework.web.util.pattern.PathPattern;

import java.util.List;

public interface IDao<T> {
    String SELECT = "SELECT * FROM ";
    String WHERE = "WHERE id=?";
    String INSERT = "INSERT INTO ";
    String UPDATE = "UPDATE ";

    String DELETE = "DELETE FROM ";

    String GENERAL_ERROR_MSG = "Failed to execute query";
    String NOT_EXISTS_ERROR_MSG = "Entity doesn't exist";


    ResponseFromDb<Integer> save(T entity);

    ResponseFromDb<Integer> update(T entity, String id);

    ResponseFromDb<Integer> delete(String id);

    ResponseFromDb<List<T>> getAll();

    ResponseFromDb<T> get(String id);
}
