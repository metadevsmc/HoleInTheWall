package org.metadevs.holeinthewall.players.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.metalib.abstracts.MetaListener;

public class PlayerDamaged extends MetaListener<HoleInTheWall> {

    public PlayerDamaged(HoleInTheWall plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDamaged(org.bukkit.event.entity.EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION) return;
        if (!(event.getEntity() instanceof org.bukkit.entity.Player)) return;
        if (!plugin.getPlayerManager().isPlaying((org.bukkit.entity.Player) event.getEntity())) return;
        event.setCancelled(true);
    }
}
