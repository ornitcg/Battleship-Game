package com.battleship.BattleshipServer.resources;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean succeeded;
    private T value;
    private String msg;

    private ApiResponse(boolean succeeded, T value, String msg) {
        setSucceeded(succeeded);
        setValue(value);
        setMsg(msg);
    }

    public static <T> ApiResponse<T> createSucceededResponse(T value) {
        return new ApiResponse<T>(true, value, "OK");
    }

    public static <T> ApiResponse<T> createSucceededResponse(T value, String msg) {
        return new ApiResponse<T>(true, value, msg);
    }

    public static <T> ApiResponse<T> createFailedResponse(String msg) {
        return new ApiResponse<T>(false, null, msg);
    }
}
