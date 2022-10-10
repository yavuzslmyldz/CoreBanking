package com.tuum.core.banking.constants;

public enum Direction {
    IN,
    OUT;

    public static boolean isMember(String name) {
        for (Direction direction : Direction.values()) {
            if (direction.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
