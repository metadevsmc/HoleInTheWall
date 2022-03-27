package org.metadevs.holeinthewall.players.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.metadevs.holeinthewall.arena.Arena;

public class PlayerLoseEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Arena arena;

    public PlayerLoseEvent(@NotNull Player who, @NotNull Arena arena) {
        super(who);
        this.arena = arena;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Arena getArena() {
        return arena;
    }
}
