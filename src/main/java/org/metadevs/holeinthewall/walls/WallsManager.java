package org.metadevs.holeinthewall.walls;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class WallsManager {

    private final ConcurrentHashMap<String, Wall> walls;
    private final HoleInTheWall plugin;

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
        for (Wall wall : plugin.getDataManager().loadWalls()) {
            walls.put(wall.getName(), wall);
        }
    }

    public boolean exists(String name) {
        return walls.containsKey(name);
    }


    public void createWall(String name, Region region) {
        Wall wall = Wall.craftFromRegion(name, region);
        walls.put(name, wall);
        plugin.getDataManager().saveWall(wall);
    }

    public void deleteWall(String name) {
        walls.remove(name);
        plugin.getDataManager().deleteWall(name);
    }

    public Wall getRandomWall() {
        Random random = new Random();
        return walls.get((String) walls.keySet().toArray()[random.nextInt(walls.size())]);
    }

    public void generateWall(Wall wall, Arena arena) {

        Material[][] wallBlocks = new Material[wall.getHeight()][wall.getHeight()];


        for (int i = 0; i < wall.getHeight(); i++) {
            char[] mats = wall.getPattern().get(i).toCharArray();
            for (int j = 0; j < wall.getHeight(); j++) {
                wallBlocks[i][j] = wall.getMaterials().get(mats[j]);
            }
        }

        Direction direction = Direction.values()[new Random().nextInt(Direction.values().length)];

        Location[] wallLocations = getWallLocations(direction, arena);

        Location min = wallLocations[0];
        Location max = wallLocations[1];

        for (int i = 0; i < wall.getHeight(); i++) {
            for (int j = 0; j < wall.getHeight(); j++) {
                placeBlocks(min, max, wallBlocks, direction, i, j, 0);
            }
        }
    }

    private Location[] getWallLocations(Direction direction, Arena arena) {
        Location min;
        Location max;

        switch (direction) {
            case NORTH:
                min = arena.getWallsLocations().get(0);
                max = arena.getWallsLocations().get(1);
                break;
            case SOUTH:
                min = arena.getWallsLocations().get(2);
                max = arena.getWallsLocations().get(3);
                break;
            case EAST:
                min = arena.getWallsLocations().get(4);
                max = arena.getWallsLocations().get(5);
                break;
            case WEST:
                min = arena.getWallsLocations().get(6);
                max = arena.getWallsLocations().get(7);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }
        return new Location[]{min, max};
    }

    private void placeBlocks(Location min, Location max, Material[][] wallBlocks, Direction direction, int i, int j, int offset) {
        switch (direction) {
            case NORTH: {
                min.getWorld().getBlockAt(min.getBlockX() + i, min.getBlockY() + j, min.getBlockZ() + offset).setType(wallBlocks[i][j]);
                break;
            }
            case SOUTH: {
                max.getWorld().getBlockAt(max.getBlockX() - i, max.getBlockY() + j, max.getBlockZ() - offset).setType(wallBlocks[i][j]);
                break;
            }
            case EAST: {
                max.getWorld().getBlockAt(max.getBlockX() + offset, max.getBlockY() + j, max.getBlockZ() - i).setType(wallBlocks[i][j]);
                break;
            }
            case WEST: {
                min.getWorld().getBlockAt(min.getBlockX() - offset, min.getBlockY() + j, min.getBlockZ() + i).setType(wallBlocks[i][j]);
                break;
            }
        }
    }

    private void moveWall(Wall wall, Arena arena, Direction direction, Material[][] wallBlocks, int offset) {
        Location[] wallLocations = getWallLocations(direction, arena);
        Location min = wallLocations[0];
        Location max = wallLocations[1];

        Material[][] wallBlocksCopy = Arrays.stream(wallBlocks).map(x -> Arrays.stream(x).map(y -> y = Material.AIR).toArray(Material[]::new)).toArray(Material[][]::new);


        for (int i = 0; i < wall.getHeight(); i++) {
            for (int j = 0; j < wall.getHeight(); j++) {
                placeBlocks(min, max, wallBlocksCopy, direction, i, j, offset - 1);
                placeBlocks(min, max, wallBlocks, direction, i, j, offset);
            }
        }
    }

    private CompletableFuture<Boolean> moveTask(Wall wall, Arena arena, Direction direction, Material[][] wallBlocks, int speed) {
        return CompletableFuture.supplyAsync(() -> {
            final int[] offset = {0};

            Semaphore semaphore = new Semaphore(0);

            Direction oppositeDirection = direction.equals(Direction.NORTH) ? Direction.SOUTH : direction.equals(Direction.SOUTH) ? Direction.NORTH : direction.equals(Direction.EAST) ? Direction.WEST : Direction.EAST;

            Location[] wallLocations = getWallLocations(oppositeDirection, arena);
            Location[] wallOppositesLocations = getWallLocations(oppositeDirection, arena);

            int distance = (int) wallLocations[0].distance(wallOppositesLocations[0]);

            int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                moveWall(wall, arena, direction, wallBlocks, offset[0]);
                offset[0]++;
                if (offset[0] == distance) {
                    System.out.println("STOP");
                    semaphore.release();
                }
            }, speed, speed);

            semaphore.acquireUninterruptibly();

            plugin.getServer().getScheduler().cancelTask(task);
            return true;
        });
    }
}
