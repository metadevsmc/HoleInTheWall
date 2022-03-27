package org.metadevs.holeinthewall.players.listeners;

import org.bukkit.event.EventHandler;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.metalib.abstracts.MetaListener;

public class PlayerLeave extends MetaListener<HoleInTheWall> {

    public PlayerLeave(HoleInTheWall plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent event) {
        plugin.getPlayerManager().endPlayerTask(event.getPlayer());
    }
}
