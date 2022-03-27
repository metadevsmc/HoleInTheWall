package org.metadevs.holeinthewall.players.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.metadevs.holeinthewall.HoleInTheWall;

public class PlayerInteractArena implements Listener {

    private final HoleInTheWall plugin;

    public PlayerInteractArena(final HoleInTheWall plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerManager().leaveArena(event.getPlayer());
    }



}
