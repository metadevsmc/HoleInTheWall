package org.metadevs.holeinthewall.walls;

import org.bukkit.configuration.ConfigurationSection;
import org.metadevs.holeinthewall.HoleInTheWall;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class WallsManager {

    private ConcurrentHashMap<String, Wall> walls;
    private HoleInTheWall plugin;

    public WallsManager(HoleInTheWall plugin) {
        this.plugin = plugin;
        this.walls = new ConcurrentHashMap<>();

    }

    public void load() {
        plugin.getLogger().info("Loading walls...");
        loadAllWalls();
        plugin.getLogger().info("Loading walls done.");

    }

    private void loadAllWalls() {
        int i = 0;
        for (Wall wall: arena.getWalls()) {
            ConfigurationSection wallSection = wallsSection.createSection(i++ + "");
            ConfigurationSection materialSection = wallSection.createSection("material");
            for (Character c: wall.getMaterials().keySet()) {
                materialSection.set(c.toString(), wall.getMaterials().get(c));
            }
            wallSection.set("pattern", wall.getPattern());
        }
    }

    public boolean exists(String name) {
        return walls.containsKey(name);
    }


}
