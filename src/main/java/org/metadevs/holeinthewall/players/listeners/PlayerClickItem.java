package org.metadevs.holeinthewall.players.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.enums.Status;
import org.metadevs.holeinthewall.metalib.abstracts.MetaListener;

public class PlayerClickItem extends MetaListener<HoleInTheWall> {

    public PlayerClickItem(HoleInTheWall plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if (plugin.getPlayerManager().isPlaying(event.getPlayer()) && plugin.getPlayerManager().getArena(event.getPlayer()).getStatus() == Status.LOBBY) {

            if (event.getItem() != null) {
                plugin.getPlayerManager().leaveArena(event.getPlayer());
                event.getPlayer().teleport(plugin.getArenaManager().getGlobalLobby());
            }
            event.setCancelled(true);
        }

        }
    }
