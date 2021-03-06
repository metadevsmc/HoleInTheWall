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


    private final String name;
    private final char[][] grid;
    private final Material[][] materialsGrid;
    private final int width;
    private final int height;
    private final ConcurrentHashMap<Character, Material> materials;

    public Wall(String name, ConcurrentHashMap<Character, String> materials, List<String> pattern) {
        this.name = name;
        this.materials = new ConcurrentHashMap<>();
        for (Map.Entry<Character, String> entry : materials.entrySet()) {
            this.materials.put(entry.getKey(), Material.valueOf(entry.getValue()));
        }
        this.height = pattern.size();
        this.width = pattern.get(0).length();
        this.grid = new char[this.height][this.width];
        this.materialsGrid = new Material[this.height][this.width];
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                this.grid[y][x] = pattern.get(y).charAt(x);
                if (this.grid[y][x] == ' ') {
                    materialsGrid[y][x] = Material.AIR;
                } else {
                    materialsGrid[y][x] = this.materials.get(this.grid[y][x]);
                }
            }
        }
    }

    public Wall(String name, Location min, Location max) {
        this.name = name;
        materials = new ConcurrentHashMap<>();
        boolean zVariant = max.getBlockX() - min.getBlockX() == 0;
        this.width = (zVariant ?  max.getBlockZ() - min.getBlockZ() : max.getBlockX() - min.getBlockX());
        this.height = max.getBlockY() - min.getBlockY() + 1;
        this.grid = new char[this.height][this.width];
        this.materialsGrid = new Material[this.height][this.width];
        if (!zVariant) {
            for (int i = 0; i < this.height; i++) {
                for (int j = 0; j < this.width; j++) {
                    Block block = min.getWorld().getBlockAt(min.getBlockX() + j, min.getBlockY() + i, min.getBlockZ());
                    materialsGrid[height - 1 - i][j] = block.getType();
                    if (block.getType() == Material.AIR) {
                        this.grid[height - 1 - i][j] = ' ';
                    } else if (block.isSolid()) {
                        Character key = block.getType().name().charAt(0);
                        this.grid[height - 1 - i][j] = key;
                        if (!materials.containsKey(key)) {
                            materials.put(key, block.getType());
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < this.height; i++) {
                for (int j = 0; j < this.width; j++) {
                    Block block = min.getWorld().getBlockAt(min.getBlockX(), min.getBlockY() + i, min.getBlockZ()+j);
                    materialsGrid[height - 1 - i][j] = block.getType();
                    if (block.getType() == Material.AIR) {
                        this.grid[height - 1 - i][j] = ' ';
                    } else if (block.isSolid()) {
                        Character key = block.getType().name().charAt(0);
                        this.grid[height - 1 - i][j] = key;
                        if (!materials.containsKey(key)) {
                            materials.put(key, block.getType());
                        }
                    }
                }
            }
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public char get(int x, int y) {
        return this.grid[y][x];
    }

    public static Wall craftFromRegion(String name, Region region) {
        Location min = new Location(BukkitAdapter.adapt(region.getWorld()), region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
        Location max = new Location(BukkitAdapter.adapt(region.getWorld()), region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ());
        return new Wall(name, min, max);
    }

    public List<String> getPattern() {
        ArrayList<String> pattern = new ArrayList<>();
        for (int i = 0; i < this.height; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < this.width; j++) {
                sb.append(this.grid[i][j]);
            }
            pattern.add(sb.toString());
        }
        return pattern;
    }

    public Material[][] getMaterialsGrid() {
        return this.materialsGrid;
    }

    public char[][] getGrid() {
        return this.grid;
    }

    public String getName() {
        return name;
    }

    public Map<Character, Material> getMaterials() {
        return this.materials;
    }
}
