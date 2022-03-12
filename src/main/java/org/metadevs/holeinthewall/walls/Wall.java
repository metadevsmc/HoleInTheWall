package org.metadevs.holeinthewall.walls;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Wall {
    String name;
    private char[][] grid;
    private int width;
    private int height;
    private ConcurrentHashMap<Character, Material> materials;


    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public char get(int x, int y) {
        return this.grid[y][x];
    }

    public Wall(Location min, Location max) {
        materials = new ConcurrentHashMap<>();

        this.width = (max.getBlockX() - min.getBlockX() == 0 ?  max.getBlockZ() - min.getBlockZ() : max.getBlockX() - min.getBlockX());
        this.width = max.getBlockX() - min.getBlockX() + 1;
        this.height = max.getBlockY() - min.getBlockY() + 1;
        this.grid = new char[this.height][this.width];
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                min.add(x, y, 0);
                Block block = min.getBlock();
                if (min.getBlock().getType() == Material.AIR) {
                    this.grid[y][x] = ' ';
                } else if (block.isSolid()){
                    this.grid[y][x] = block.getType().name().charAt(0);
                    if (!materials.containsKey(block.getType().name().charAt(0))) {
                        materials.put(block.getType().name().charAt(0), block.getType());
                    }
                }

            }
        }
    }

    public static Wall fromRegion(String name, Region region) {
        Location min = new Location(BukkitAdapter.adapt(region.getWorld()), region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
        Location max = new Location(BukkitAdapter.adapt(region.getWorld()), region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ());
        return new Wall(min, max);
    }

    public List<String> getPattern() {
        ArrayList<String> pattern = new ArrayList<>();
        for (int y = 0; y < this.height; y++) {
            pattern.add(new String(this.grid[y]));
        }
        return pattern;
    }

    public Map<Character, Material> getMaterials() {
        return this.materials;
    }
}
