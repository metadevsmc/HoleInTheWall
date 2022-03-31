package org.metadevs.holeinthewall.players;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.metadevs.holeinthewall.metalib.Utils.color;

public class PlayerManager {
    private final HoleInTheWall plugin;
    private final ConcurrentHashMap<Player, String> players;
    private final ConcurrentHashMap<Player, BukkitTask> playersTasks;
    private final ConcurrentHashMap<Player, ItemStack[]> playersInventory;

    public PlayerManager(HoleInTheWall holeInTheWall) {
        this.plugin = holeInTheWall;
        players = new ConcurrentHashMap<>();
        playersTasks = new ConcurrentHashMap<Player, org.bukkit.scheduler.BukkitTask>();
        playersInventory = new  ConcurrentHashMap<Player, ItemStack[]>();
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
        plugin.getPlayerManager().giveLobbyItem(player);
        players.put(player, arena.getName());
    }

    public void giveLobbyItem(Player player) {

        playersInventory.put(player, player.getInventory().getContents());

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        //Give lobby Item

        ItemStack item = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color("&c&lLeave Arena"));
        item.setItemMeta(meta);
        player.getInventory().setItem(0, item);

        player.updateInventory();
    }

    public void takeLobbyItem(Player player) {
        ItemStack[] inventory = playersInventory.remove(player);
        if (inventory != null) {
            player.getInventory().setContents(inventory);
        }
    }

    public void leaveArena(Player player) {
        Arena arena = plugin.getArenaManager().getArena(players.get(player));
        if (arena != null) {

            arena.removePlayer(player);
        }
        //todo teleport to lobby
        players.remove(player);
        takeLobbyItem(player);

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

    public boolean isPlayerInArea(Player player, Location traslatedMin, Location traslatedMax) {

        return player.getLocation().getBlock().getX() >= traslatedMin.getX() && player.getLocation().getBlock().getX() <= traslatedMax.getX() &&
                player.getLocation().getBlock().getY()>= traslatedMin.getY() && player.getLocation().getBlock().getY() <= traslatedMax.getY() &&
                player.getLocation().getBlock().getZ() >= traslatedMin.getZ() && player.getLocation().getBlock().getZ() <= traslatedMax.getZ();
    }
}


