package org.metadevs.holeinthewall.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.enums.Direction;
import org.metadevs.holeinthewall.enums.Status;
import org.metadevs.holeinthewall.metalib.messages.MessageHandler;
import org.metadevs.holeinthewall.players.events.PlayerWinEvent;
import org.metadevs.holeinthewall.walls.Wall;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ArenaManager {

    private final HoleInTheWall plugin;
    private final ConcurrentHashMap<String, Arena> arenas;
    private final MessageHandler<HoleInTheWall> messaageHandler;
    private Location globalLobby;

    public ArenaManager(HoleInTheWall holeInTheWall) {
        this.plugin = holeInTheWall;
        this.arenas = new ConcurrentHashMap<>();
        this.messaageHandler = plugin.getMetaLibs().messageHandler();
        globalLobby = plugin.getConfig().getLocation("global-lobby");
        loadArenas();
    }

    public void loadArenas() {
        Collection<Arena> arenas = plugin.getDataManager().loadArenas();
        for (Arena arena : arenas) {
            this.arenas.put(arena.getName(), arena);
        }
    }

    /**
     * returns the arena with the given name.
     *
     * @param name The name of the arena.
     * @return The arena with the given name. Null if the arena doesn't exist.
     */
    @Nullable
    public Arena getArena(String name) {
        return arenas.get(name);
    }

    /**
     * Checks if the arena exists.
     *
     * @param name The name of the arena.
     * @return True if the arena exists, false otherwise.
     */
    public boolean exists(String name) {
        return arenas.containsKey(name);
    }

    /**
     * Creates a new arena.
     *
     * @param name The name of the arena.
     */
    public void createArena(String name) {
        Arena arena = new Arena(name, plugin.getConfig().getInt("arena.default-min-players", 12), plugin.getConfig().getInt("arena.default-max-players", 24));
        arenas.put(name, arena);
        plugin.getDataManager().saveArena(arena);
    }

    /**
     * Removes an arena.
     *
     * @param name The name of the arena.
     *             If the arena doesn't exist, nothing happens.
     *             If the arena is running, it will be stopped.
     *             If the arena is not running, it will be removed.
     */
    public void deleteArena(String name) {
        Arena arena = arenas.get(name);
        if (arena != null) {
            // arena.stop();
            arenas.remove(name);
            plugin.getDataManager().deleteArena(name);
        }
        //todo remove arena from database
    }

    public Set<String> getArenasNames() {
        return arenas.keySet();
    }

    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    public boolean canStart(String name) {
        Arena arena = arenas.get(name);
        if (arena == null)
            return false;
        return arena.canStart();
    }

    public void startArena(String name) {
        Arena arena = arenas.get(name);
        if (arena == null) {
            messaageHandler.sendMessage(Bukkit.getConsoleSender(), "Arena " + name + " doesn't exist.");
            return;
        }
        startGameTask(arena);

        //todo startup logic

    }

    public void startGameTask(Arena arena) {
        CompletableFuture.runAsync(() -> {
            try {
                arena.setStatus(Status.PLAYING);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        arena.getPlayers().forEach(player -> {
                            player.teleport(arena.getSpawn());
                        });
                    }
                }.runTask(plugin);


                int round = 1;
                int nWall;
                int speed = 20;


                while (arena.getStatus() == Status.PLAYING && round <= 40) {
                    nWall = (int) Math.ceil(round / 10.0);
                    List<CompletableFuture<Boolean>> futures = new ArrayList<>();
                    List<Direction> directions = Arrays.stream(Direction.values()).collect(Collectors.toList());
                    for (int i = 0; i < nWall; i++) {
                        CompletableFuture<Boolean> wallFuture = processWall(arena, directions, round, speed);
                        futures.add(wallFuture);
                    }
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[nWall])).get();

                    round++;

                    if (arena.getNumberOfPlayers() == 1) {
                        arena.getPlayers().forEach(player -> {
                            Bukkit.getPluginManager().callEvent(new PlayerWinEvent(player, arena));
                        });
                    }
                }
                arena.setStatus(Status.ENDING);
                //pre end -> player get teleported to podium and looser positions after a few seconds -> end

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private CompletableFuture<Boolean> processWall(Arena arena, List<Direction> directions, int round, final int speed) {
        Wall wall = plugin.getWallsManager().getRandomWall();
        Direction direction = directions.remove(new Random().nextInt(directions.size()));

        arena.getCurrentDirections().add(direction);

        //gen nord
        Material[][] wallBlocks = wall.getMaterialsGrid();

        plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getWallsManager().generateWall(wall, arena, direction, wallBlocks));

        return plugin.getWallsManager().moveTask(wall, arena, direction, wallBlocks, speed - ((round - 1) % 10));
    }

    public void initArenaCooldown(Arena arena) {

        arena.initCooldown(plugin);
    }


    public void setGlobalLobby(Location location) {
        globalLobby = location;
        plugin.getConfig().set("global-lobby", location);
        plugin.saveConfig();
    }

    public Location getGlobalLobby() {
        return globalLobby;
    }

    public void endGame(Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Player> iterator = plugin.getPlayerManager().getPlayers().iterator();
                while (iterator.hasNext()) {
                    Player player = iterator.next();
                    if (plugin.getPlayerManager().getArena(player).getName().equals(arena.getName())) {
                        iterator.remove();
                        plugin.getMetaLibs().messageHandler().sendMessage(player, "general.arena.game-ended", "the game is ended, now you will be teleported to the lobby");
                        player.teleport(plugin.getArenaManager().getGlobalLobby() == null ? Bukkit.getWorlds().get(0).getSpawnLocation() : plugin.getArenaManager().getGlobalLobby());
                    }
                }

                arena.reset();
            }
        }.runTaskLater(plugin, 10 * 20);
    }
}
