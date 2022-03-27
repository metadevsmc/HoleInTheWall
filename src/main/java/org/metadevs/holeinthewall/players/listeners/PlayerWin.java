package org.metadevs.holeinthewall.players.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.abstracts.MetaListener;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.players.events.PlayerWinEvent;

public class PlayerWin extends MetaListener<HoleInTheWall> {
    public PlayerWin(HoleInTheWall plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerWin(PlayerWinEvent event) {
        Player player = event.getPlayer();
        Arena arena = event.getArena();

        plugin.getMetaLibs().messageHandler().sendMessage(player, "success.arena.win-self-message", "&a Congratulation You have won the game");
        player.teleport(arena.getPodium());

        arena.getSpectators().forEach(spectator -> {
            spectator.teleport(arena.getLoosers());
            plugin.getMetaLibs().messageHandler().sendMessage(spectator, "success.arena.win", "&a Congratulation {player} has won the game", new Placeholder("{player}", player.getName()));
        });

        plugin.getArenaManager().endGame(arena);
    }
}
