package org.metadevs.holeinthewall.enums;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public enum Direction {

    NORTH("north", new Vector(0, 0, 1)), SOUTH("south", new Vector(0, 0, -1)), EAST("east", new Vector(-1, 0, 0)), WEST("west", new Vector(1, 0,0 ));

    private final String name;
    private  final Vector to;

    Direction(String name, Vector to) {
        this.to = to;
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

    public Vector getTo() {
        return to;
    }
}


