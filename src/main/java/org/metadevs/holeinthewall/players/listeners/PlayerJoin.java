package org.metadevs.holeinthewall.players.listeners;

import org.bukkit.event.EventHandler;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.metalib.abstracts.MetaListener;

public class PlayerJoin extends MetaListener<HoleInTheWall> {
    public PlayerJoin(HoleInTheWall plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        plugin.getPlayerManager().startMoveTask(event.getPlayer());
    }

}
