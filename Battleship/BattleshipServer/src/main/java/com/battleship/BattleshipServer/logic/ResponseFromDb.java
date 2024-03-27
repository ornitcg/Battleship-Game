package com.battleship.BattleshipServer.logic;

import lombok.Data;

@Data
public class ResponseFromDb <T> {
    private boolean succeeded;
    private T value;
    private String errorMsg;

    private ResponseFromDb(boolean succeeded, T value, String errorMsg) {
        setSucceeded(succeeded);
        setValue(value);
        setErrorMsg(errorMsg);
    }

    public static <T> ResponseFromDb<T> createSucceededResponse(T value) {
        return new ResponseFromDb<T>(true, value, null);
    }

    public static <T> ResponseFromDb<T> createFailedResponse(String errorMsg) {
        return new ResponseFromDb<T>(false, null, errorMsg);
    }
}
