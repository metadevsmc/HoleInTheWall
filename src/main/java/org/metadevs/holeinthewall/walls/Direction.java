package org.metadevs.holeinthewall.walls;

import org.jetbrains.annotations.Nullable;

public enum Direction {

    NORTH("north"), SOUTH("south"), EAST("east"), WEST("west");

    private String name;

    Direction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public static Direction fromString(String name) {
        for (Direction direction : Direction.values()) {
            if (direction.name.equalsIgnoreCase(name)) {
                return direction;
            }
        }
        return null;
    }


}
