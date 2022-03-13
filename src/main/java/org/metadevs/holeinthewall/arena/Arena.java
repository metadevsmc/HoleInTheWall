package org.metadevs.holeinthewall.arena;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.enums.Status;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.Option;
import org.metadevs.holeinthewall.enums.Direction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Arena  {

    private final String name;
    private int minPlayers;
    private int maxPlayers;
    private int minY;
    private final ConcurrentHashMap<String, Location> locations;
    private final HashMap<Direction, WallSpawn> wallsLocations; //for each wall there are two locations, one for the min and one for the max (total of 8, NORTH, SOUTH, EAST, WEST)

    private Status status = Status.LOBBY;
    private final Set<Player> players;
    private final Set<Player> spectators;

    private BossBar cooldownBar;
    private BukkitTask countdown;
    private int seconds;

    public Arena(String name, int minPlayers, int maxPlayers) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.minY = -100;
        this.locations= new ConcurrentHashMap<>();
        this.players = new HashSet<>();
        this.spectators = new HashSet<>();
        this.wallsLocations = new HashMap<>(4);
    }

    public Arena(String name, ConcurrentHashMap<String, Location> locations, int minPlayers, int maxPlayers, int mimY, HashMap<Direction, WallSpawn> wallsLocations) {
        this.name = name;
        this.locations = locations;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.minY = mimY;
        this.players = new HashSet<>();
        this.spectators = new HashSet<>();
        this.wallsLocations = wallsLocations;
    }

    public String getName() {
        return this.name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setOptions(ConcurrentHashMap<String, Object> options) {
        if (options.containsKey("min-players")) {
            this.minPlayers = (int) options.get("min-players");
        }
        if (options.containsKey("max-players")) {
            this.maxPlayers = (int) options.getOrDefault("max-players", 12);
        }
        if (options.containsKey("min-y")) {
            this.minY = (int) options.get("min-y");
        }
    }

    public void update(Option<Location> option) {
        this.locations.put(option.getName(), option.getValue());
    }

    public Set<Player> getSpectators() {
        return spectators;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isFull() {
        return this.players.size() >= this.maxPlayers;
    }

    public boolean isPlayerInArena(Player player) {
        return this.players.contains(player);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
        this.spectators.remove(player);
    }

    public boolean canStart() {
        if (Status.ENDING.equals(this.status) || Status.PLAYING.equals(this.status))
            throw new IllegalStateException("Cannot start an arena that is already playing or ending");
        return this.players.size() >= this.minPlayers;
    }


    public int getNumberOfPlayers() {
        return this.players.size();
    }

    public void initCooldown(HoleInTheWall plugin) {
        seconds = plugin.getConfig().getInt("lobby.cooldown", 30);
        cooldownBar = Bukkit.createBossBar(
                plugin.getConfigString("lobby.bossbar.title", "Game starting in {seconds-left}", new Placeholder("{seconds-left}", seconds + "")),
                BarColor.valueOf(plugin.getConfig().getString("lobby.bossbar.color", "GREEN")),
                BarStyle.SOLID);
        status = Status.STARTING;

        cooldownBar.setVisible(true);
        players.forEach(cooldownBar::addPlayer);
        if (countdown == null) {
            this.countdown = new BukkitRunnable() {
                @Override
                public void run() {
                    if (seconds <= 0) {
                        if (canStart()) {
                            plugin.getArenaManager().startArena(name);
                            cooldownBar.removeAll();
                            countdown = null;
                            cooldownBar = null;
                        } else {

                            getPlayers().forEach(player -> {
                                plugin.getMetaLibs().messageHandler().sendMessage(player,
                                        "error.arena.not-enough-players",
                                        "There are not enough players to start the arena, needed {minPlayers}, currently {players}",
                                        new Placeholder("{minPlayers}", getMinPlayers() + ""),
                                        new Placeholder("{players}", getNumberOfPlayers() + ""));

                            });
                            cooldownBar.removeAll();
                            countdown = null;
                            cooldownBar = null;
                            status = Status.LOBBY;
                        }
                        cancel();
                    } else {
                        cooldownBar.setTitle(plugin.getConfigString("lobby.bossbar.title", "Game starting in {seconds-left}", new Placeholder("{seconds-left}", seconds + "")));
                        if (getNumberOfPlayers() == 0) {
                            cooldownBar.removeAll();
                            countdown = null;
                            cooldownBar = null;
                            cancel();
                        } else if (getNumberOfPlayers() == maxPlayers && seconds > 5) {
                            seconds= 6;
                            plugin.getMetaLibs().messageHandler().sendMessage(players,
                                    "general.arena.starting-soon",
                                    "The game will start in {seconds-left} seconds",
                                    new Placeholder("{seconds-left}", seconds + ""));
                        }
                        cooldownBar.setProgress(((float) seconds / plugin.getConfig().getInt("lobby.cooldown", 30)));
                        seconds--;
                    }


                }
            }.runTaskTimer(plugin, 0, 20);
        }
    }


    public int getMinY() {
        return minY;
    }

    public ConcurrentHashMap<String, Location> getLocations() {
        return locations;
    }

    public Location getSpawn() {
        return locations.get("spawn");
    }

    public Location getLobby() {
        return locations.get("lobby");
    }

    public void setWallSpawn(Direction direction, Region region) {
        Location min = new Location(BukkitAdapter.adapt(region.getWorld()), region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
        Location max = new Location(BukkitAdapter.adapt(region.getWorld()), region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ());

        this.wallsLocations.put(direction,new WallSpawn(min, max));

    }

    public Iterable<? extends WallSpawn> getWallSpawns() {
        return wallsLocations.values();
    }

    public WallSpawn getWallSpawn(Direction direction){
        return wallsLocations.get(direction);
    }

    public boolean isStarting() {
        return Status.STARTING.equals(status);
    }

    public boolean isLobby() {
        return Status.LOBBY.equals(status);
    }

    public void reset() {
        status = Status.LOBBY;
        players.clear();
        spectators.clear();
        countdown = null;
        cooldownBar = null;
    }

    public static class WallSpawn {
        private Location min;
        private Location max;

        public WallSpawn(Location min, Location max) {
            this.min = min;
            this.max = max;
        }

        @Nullable
        public Location getMin() {
            return min;
        }

        @Nullable
        public Location getMax() {
            return max;
        }
    }

}



