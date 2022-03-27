package org.metadevs.holeinthewall.players.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.metadevs.holeinthewall.arena.Arena;

public class PlayerWinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final Player player;
    private final Arena arena;

    public PlayerWinEvent(Player player, Arena arena) {
        this.player = player;
        this.arena = arena;
    }




    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Arena getArena() {
        return arena;
    }
}
