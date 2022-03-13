package org.metadevs.holeinthewall.players;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private final HoleInTheWall plugin;
    private final ConcurrentHashMap<Player, String> players;

    public PlayerManager(HoleInTheWall holeInTheWall) {
        this.plugin = holeInTheWall;
        players = new ConcurrentHashMap<>();
    }

    @Nullable
    public String removePlayer(Player player) {
        return players.remove(player);
    }

    public boolean isPlaying(Player player) {
        return players.containsKey(player);
    }

    public void joinArena(Player player, Arena arena) {
        arena.addPlayer(player);
        if (arena.getNumberOfPlayers() == arena.getMinPlayers() && arena.isLobby()) {
            plugin.getArenaManager().initArenaCooldown(arena);
        }
        player.teleport(arena.getLobby());
        players.put(player, arena.getName());
    }

    public void leaveArena(Player player) {
        Arena arena = plugin.getArenaManager().getArena(players.get(player));
        if (arena != null) {
            arena.removePlayer(player);
        }
        //todo teleport to lobby
        players.remove(player);
    }
}


