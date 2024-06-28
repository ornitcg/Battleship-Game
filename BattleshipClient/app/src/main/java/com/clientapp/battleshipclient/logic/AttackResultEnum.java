package com.clientapp.battleshipclient.logic;


/*
*   Enum for the result of an attack
* */
public enum AttackResultEnum {
    HIT("hit"),
    MISS("miss"),
    SUNK("sunk");

    private String name;

    AttackResultEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AttackResultEnum fromString(String name) {
        AttackResultEnum retVal = null;

        for (AttackResultEnum state : AttackResultEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                retVal = state;
            }
        }

        return retVal;
    }
}
