package org.metadevs.holeinthewall.players.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.abstracts.MetaListener;
import org.metadevs.holeinthewall.players.events.PlayerLoseEvent;

public class PlayerFall extends MetaListener<HoleInTheWall> {

    public PlayerFall(HoleInTheWall plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerFall(org.bukkit.event.player.PlayerMoveEvent event) {

        if (event.getFrom().getBlockY() <= event.getTo().getBlockY()) return;
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL && event.getPlayer().getGameMode() != GameMode.ADVENTURE) return;
        Arena  arena;
        Player player = event.getPlayer();
        if (plugin.getPlayerManager().isPlaying(player)) {
            if ((arena = this.plugin.getPlayerManager().getArena(player)) == null) return;
            if (!arena.getPlayers().contains(player)) return;

            if (event.getTo().getBlockY() <= arena.getMinY()) {
                //call an event to handle the player death
                Bukkit.getServer().getPluginManager().callEvent(new PlayerLoseEvent(player, arena));

            }
        }


    }
}
