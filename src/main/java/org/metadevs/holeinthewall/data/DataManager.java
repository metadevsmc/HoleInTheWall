package org.metadevs.holeinthewall.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.files.FileHandler;
import org.metadevs.holeinthewall.walls.Direction;
import org.metadevs.holeinthewall.walls.Wall;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {

    private final HoleInTheWall plugin;
    private File dataFolder;
    private File arenasFolder;
    private File wallsFolder;

    public DataManager(HoleInTheWall plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.getLogger().info("Loading data...");

        setupDir();

        plugin.getLogger().info("Data loaded.");
    }

    private void setupDir() {
        dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        arenasFolder = new File(dataFolder, "arenas");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdir();
        }
        wallsFolder = new File(dataFolder, "walls");
        if (!wallsFolder.exists()) {
            wallsFolder.mkdir();
        }
    }

    public void saveWall(Wall wall) {
        CompletableFuture.runAsync(() -> {
            FileHandler fileHandler = new FileHandler(wallsFolder, wall.getName());
            FileConfiguration config = fileHandler.getConfig();
            ConfigurationSection wallSection = config.createSection(wall.getName());
            ConfigurationSection materialSection = wallSection.createSection("materials");
            for (Character c : wall.getMaterials().keySet()) {
                materialSection.set(c.toString(), wall.getMaterials().get(c).name());
            }
            wallSection.set("pattern", wall.getPattern());
            fileHandler.save();
        });
    }

    public void saveArena(Arena arena) {
        CompletableFuture.runAsync(() -> {
            try {
            FileHandler fileHandler = new FileHandler(arenasFolder, arena.getName());
            FileConfiguration config = fileHandler.getConfig();
            ConfigurationSection section = config.createSection(arena.getName());
            section.set("name", arena.getName());
            section.set("min-players", arena.getMinPlayers());
            section.set("max-players", arena.getMaxPlayers());
            section.set("min-Y-level", arena.getMinY());

            ConfigurationSection locationsSection = section.createSection("locations");

            locationsSection.set("lobby", arena.getLocations().get("lobby"));
            locationsSection.set("spawn", arena.getLocations().get("spawn"));
            locationsSection.set("spectator", arena.getLocations().get("spectator"));
            locationsSection.set("podium", arena.getLocations().get("podium"));
            locationsSection.set("loosers", arena.getLocations().get("loosers"));
            ConfigurationSection wallSpawnSection = section.createSection("wall-spawns");
            for (Direction direction : Direction.values()) {
                ConfigurationSection directionSection = wallSpawnSection.createSection(direction.name());
                Arena.WallSpawn wallSpawn = arena.getWallSpawn(direction);
                if (wallSpawn == null) {
                    continue;
                }
                directionSection.set("max", arena.getWallSpawn(direction).getMin());
                directionSection.set("min", arena.getWallSpawn(direction).getMax());
            }

            if (!fileHandler.save()) {
                Bukkit.getLogger().severe("Failed to save arena " + arena.getName());
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Collection<Wall> loadWalls() {
        ArrayList<Wall> walls = new ArrayList<>();
        File[] files = wallsFolder.listFiles();
        if (files != null && files.length > 0) {
            for (File file : wallsFolder.listFiles()) {
                FileHandler fileHandler = FileHandler.load(wallsFolder, file);
                String name = file.getName().replace(".yml", "");
                FileConfiguration config = fileHandler.getConfig();
                if (!config.isConfigurationSection(name)) {
                    plugin.getMetaLibs().messageHandler().sendMessage(Bukkit.getConsoleSender(), "&cInvalid wall file: " + name);
                    continue;
                }

                ConfigurationSection wallSection = config.getConfigurationSection(name);
                ConfigurationSection materialSection = wallSection.getConfigurationSection("materials");
                ConcurrentHashMap<Character, String> materials = new ConcurrentHashMap<>();
                for (String c : materialSection.getKeys(false)) {
                    materials.put(c.charAt(0), materialSection.getString(c));
                }
                List<String> pattern = wallSection.getStringList("pattern");
                walls.add(new Wall(name, materials, pattern));
            }
        }
        return walls;
    }

    public Collection<Arena> loadArenas() {
        ArrayList<Arena> arenas = new ArrayList<>();
        File[] files = arenasFolder.listFiles();
        if (files != null && files.length > 0) {
            for (File file : arenasFolder.listFiles()) {
                FileHandler fileHandler = FileHandler.load(arenasFolder, file);
                String name = file.getName().replace(".yml", "");
                FileConfiguration config = fileHandler.getConfig();
                if (!config.isConfigurationSection(name)) {
                    plugin.getMetaLibs().messageHandler().sendMessage(Bukkit.getConsoleSender(), "&cInvalid arena file: " + name);
                    continue;
                }

                ConfigurationSection section = config.getConfigurationSection(name);
                int minPlayers = section.getInt("min-players", plugin.getConfig().getInt("arena.default-min-players", 12));
                int maxPlayers = section.getInt("max-players", plugin.getConfig().getInt("arena.default-max-players", 24));
                int mimY = section.getInt("min-Y-level", 0);

                ConfigurationSection locationsSection = section.getConfigurationSection("locations");

                ConcurrentHashMap<String, Location> locations = new ConcurrentHashMap<>();
                if (locationsSection != null) {
                    if (locationsSection.contains("lobby")) {
                        locations.put("lobby", locationsSection.getLocation("lobby"));
                    }
                    if (locationsSection.contains("spawn")) {
                        locations.put("spawn", locationsSection.getLocation("spawn"));
                    }
                    if (locationsSection.contains("spectator")) {
                        locations.put("spectator", locationsSection.getLocation("spectator"));
                    }
                    if (locationsSection.contains("podium")) {
                        locations.put("podium", locationsSection.getLocation("podium"));
                    }
                    if (locationsSection.contains("loosers")) {
                        locations.put("loosers", locationsSection.getLocation("loosers"));
                    }
                }
                ConfigurationSection wallSpawnsSection = section.getConfigurationSection("wall-spawns");
                HashMap<Direction, Arena.WallSpawn> wallSpawns = new HashMap<>();
                if (wallSpawnsSection != null) {
                    for (String direction : wallSpawnsSection.getKeys(false)) {
                        Location min = wallSpawnsSection.getLocation(direction + ".min");
                        Location max = wallSpawnsSection.getLocation(direction + ".max");
                        Arena.WallSpawn spawn = new Arena.WallSpawn(min, max);
                        wallSpawns.put(Direction.valueOf(direction), spawn);
                    }
                    arenas.add(new Arena(name, locations, minPlayers, maxPlayers, mimY, wallSpawns));
                }
            }
        }
        return arenas;
    }

    public void deleteWall(String name) {
        File file = new File(wallsFolder, name + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }

    public void deleteArena(String name) {
        File file = new File(arenasFolder, name + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }
}
