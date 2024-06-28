package com.clientapp.battleshipclient.logic;


/*
*  Enum for the type of authentication
* */
public enum AuthTypeEnum {

    SIGN_UP("Signing Up"),
    SIGN_IN("Signing In");


    private final String name;

    AuthTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AuthTypeEnum fromString(String name) {
        AuthTypeEnum retVal = null;

        for (AuthTypeEnum state : AuthTypeEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                retVal = state;
            }
        }

        return retVal;
    }


}
