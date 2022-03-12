package org.metadevs.holeinthewall.enums;

public enum Status {
    LOBBY("Lobby"),
    STARTING("Starting"),
    PLAYING("Playing"),
    ENDING("Ending");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
