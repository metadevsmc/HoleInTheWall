package org.metadevs.holeinthewall.players;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private final HoleInTheWall plugin;
    private final ConcurrentHashMap<Player, String> players;
    private final ConcurrentHashMap<Player, BukkitTask> playersTasks;

    public PlayerManager(HoleInTheWall holeInTheWall) {
        this.plugin = holeInTheWall;
        players = new ConcurrentHashMap<>();
        playersTasks = new ConcurrentHashMap<Player, org.bukkit.scheduler.BukkitTask>();
    }

    @Nullable
    public String removePlayer(Player player) {
        return players.remove(player);
    }

    public boolean isPlaying(Player player) {
        return players.containsKey(player);
    }

    public void joinArena(Player player, Arena arena) {
        arena.join(player);
        if (arena.getNumberOfPlayers() == arena.getMinPlayers() && arena.isLobby()) {
            plugin.getArenaManager().initArenaCooldown(arena);
        }
        for (Player p : arena.getPlayers()) {
            plugin.getMetaLibs().messageHandler().sendMessage(p, "success.arena.join", "&7[&a+&7] &a&l{player}", new Placeholder("{player}", player.getName()));
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

    public Arena getArena(Player player) {
        return plugin.getArenaManager().getArena(players.get(player));
    }

    public Set<Player> getPlayers() {
        return players.keySet();
    }

    public void endPlayerTask(Player player) {
        BukkitTask task = playersTasks.get(player);
        if (task != null) {
            task.cancel();
            playersTasks.remove(player);
        }
    }

    public void startMoveTask(Player player) {
//        playersTasks.put(player, new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (player.isOnline()) {
//                    if (isPlaying(player)) {
//                        if (player.getLocation().getBlock().isSolid() || player.getEyeLocation().getBlock().isSolid()) {
//                            Arena arena = getArena(player);
//                            player.setVelocity(player.getFacing().getDirection().multiply(-2));
//                        }
//                    }
//                } else {
//                    cancel();
//                }
//            }
//        }.runTaskTimer(plugin, 0,5));
    }
}


