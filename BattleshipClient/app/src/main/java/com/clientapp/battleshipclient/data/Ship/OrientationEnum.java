package com.clientapp.battleshipclient.data.Ship;



public enum OrientationEnum {
    VERTICAL("vertical"),
    HORIZONTAL("horizontal");

    private final String name;

    OrientationEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static OrientationEnum fromString(String name) {
        OrientationEnum retVal = null;

        for (OrientationEnum state : OrientationEnum.values()) {
            if (state.name.equalsIgnoreCase(name)) {
                retVal = state;
            }
        }
        return retVal;
    }
}
