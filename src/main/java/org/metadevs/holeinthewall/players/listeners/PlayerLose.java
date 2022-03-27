package org.metadevs.holeinthewall.players.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.abstracts.MetaListener;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.players.events.PlayerLoseEvent;
import org.metadevs.holeinthewall.players.events.PlayerWinEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerLose extends MetaListener<HoleInTheWall> {

    public PlayerLose(HoleInTheWall plugin) {
        super(plugin);

    }

    @EventHandler
    public void onPlayerLose(PlayerLoseEvent event) {

        Arena arena = event.getArena();
        Player player = event.getPlayer();


        arena.getPlayers().remove(player);
        List<Player> players = new ArrayList<>();

        players.addAll(arena.getPlayers());
        players.addAll(arena.getSpectators());
        for (Player p : players) {
            plugin.getMetaLibs().messageHandler().sendMessage(p, "success.arena.eliminated", "&a {player} has been eliminated remaining players: {remaining}", new Placeholder("{player}", player.getName()), new Placeholder("{remaining}", arena.getPlayers().size() + ""));
        }
        arena.getSpectators().add(player);
        player.teleport(arena.getSpectator());
        player.sendMessage(plugin.getMetaLibs().messageHandler().getMessage("success.arena.eliminated-self-message", "&aYou have been eliminated remaining players: {remaining}", new Placeholder("{remaining}", arena.getPlayers().size() + "")));

        if (arena.getNumberOfPlayers() == 1) {
            for (Player p : arena.getPlayers()) {
                Bukkit.getPluginManager().callEvent(new PlayerWinEvent(p, arena));
                return;
            }

        }
    }
}
