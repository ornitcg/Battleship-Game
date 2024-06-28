package com.clientapp.battleshipclient.logic;

import lombok.Getter;


/*
*  Enum for the different states of the authentication process
* */
@Getter
public enum AuthStateEnum {

    SUCCEEDED("succeeded"),
    VALID("valid"),
    USER_EXISTS("User name already exist"),
    INVALID_USERNAME("Invalid Username"),
    PENDING("pending"),
    WRONG_PASSWORD("Wrong Password"),
    USER_DOESNT_EXIST("User Doesn't Exist"),
    INVALID_PASSWORD("Invalid Password"),
    CONNECTION_ERROR("Connection Error"),
    USER_ALREADY_SIGNED_IN("User is already in the system"),
    INVALID_IP("Invalid IP");



    private final String name;

    AuthStateEnum(String name) {
        this.name = name;
    }


    public static AuthStateEnum fromString(String name) {
        AuthStateEnum retVal = null;

        for (AuthStateEnum state : AuthStateEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                retVal = state;
            }
        }

        return retVal;
    }
}
