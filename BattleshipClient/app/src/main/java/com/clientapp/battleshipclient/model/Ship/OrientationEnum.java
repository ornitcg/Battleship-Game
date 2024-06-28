package com.clientapp.battleshipclient.model.Ship;


import lombok.Getter;

@Getter
public enum OrientationEnum {
    VERTICAL("vertical"),
    HORIZONTAL("horizontal"), EDGE("edge");

    private final String name;

    OrientationEnum(String name) {
        this.name = name;
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
