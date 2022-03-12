package org.metadevs.holeinthewall.walls;

import com.sk89q.worldedit.regions.Region;
import org.metadevs.holeinthewall.HoleInTheWall;

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
        for (Wall wall: plugin.getDataManager().loadWalls()) {
            walls.put(wall.getName(), wall);
        }
    }

    public boolean exists(String name) {
        return walls.containsKey(name);
    }


    public void createWall(String name, Region region) {
        Wall wall =  Wall.craftFromRegion(name, region);
        walls.put(name,wall);
        plugin.getDataManager().saveWall(wall);
    }

    public void deleteWall(String name) {
        walls.remove(name);
        plugin.getDataManager().deleteWall(name);
    }
}
