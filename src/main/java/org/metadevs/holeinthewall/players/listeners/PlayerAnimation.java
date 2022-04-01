package org.metadevs.holeinthewall.players.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.metadevs.holeinthewall.HoleInTheWall;

public class PlayerAnimation implements Listener {

    private final HoleInTheWall plugin;

    public PlayerAnimation(final HoleInTheWall plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        if(plugin.getPlayerManager().isPlaying(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

}
