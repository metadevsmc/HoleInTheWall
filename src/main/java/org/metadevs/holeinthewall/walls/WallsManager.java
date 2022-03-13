package org.metadevs.holeinthewall.walls;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.enums.Direction;

import java.util.Collection;
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

    public Wall getRandomWall() {
        Random random = new Random();
        return walls.get((String) walls.keySet().toArray()[random.nextInt(walls.size())]);
    }

    public void generateWall(Wall wall, Arena arena, Direction direction, Material[][] wallBlocks) {

        Arena.WallSpawn wallLocations = getWallLocations(direction, arena);

        boolean isZ = wallLocations.getMin().getX() == wallLocations.getMax().getX();
        if (isZ) {

            for (int y = 0; y < wall.getHeight(); y++) {
                for (int z = 0; z < wall.getWidth(); z++) {
                    Material material = wallBlocks[y][z];
                    placeBlock(wallLocations.getMin(), material, 0, -y, -z);
                }
            }
        } else {
            for (int y = 0; y < wall.getHeight(); y++) {
                for (int x = 0; x < wall.getWidth(); x++) {
                    Material material = wallBlocks[y][x];
                    placeBlock(wallLocations.getMin(), material, -x, -y, 0);
                }
            }
        }
    }



    private Arena.WallSpawn getWallLocations(Direction direction, Arena arena) {
        return arena.getWallSpawn(direction);
    }


    private void placeBlock(Location loc, Material material, int offsetX, int offsetY, int offsetZ) {
        loc.clone().add(offsetX, offsetY, offsetZ).getBlock().setType(material);
    }
    private void placeBlock(Location loc, Material material, int offsetX, int offsetY, int offsetZ, Vector vector) {
        loc.clone().add(offsetX, offsetY, offsetZ).add(vector).getBlock().setType(material);
    }

//    private void placeBlocks(Location min, Location max, Material[][] wallBlocks, Direction direction, int i, int j, int offset) {
//        max.add(x,y,z).getBlock().setType();
//        switch (direction) {
//            case NORTH: {
//                min.getWorld().getBlockAt(min.getBlockX() + i, min.getBlockY() + j, min.getBlockZ() + offset).setType(wallBlocks[i][j]);
//                break;
//            }
//            case SOUTH: {
//                max.getWorld().getBlockAt(max.getBlockX() - i, max.getBlockY() + j, max.getBlockZ() - offset).setType(wallBlocks[i][j]);
//                break;
//            }
//            case EAST: {
//                max.getWorld().getBlockAt(max.getBlockX() + offset, max.getBlockY() + j, max.getBlockZ() - i).setType(wallBlocks[i][j]);
//                break;
//            }
//            case WEST: {
//                min.getWorld().getBlockAt(min.getBlockX() - offset, min.getBlockY() + j, min.getBlockZ() + i).setType(wallBlocks[i][j]);
//                break;
//            }
//        }
//    }

    private void moveWall(Wall wall, Arena arena, Direction direction, Material[][] wallBlocks, int offset) {

        Arena.WallSpawn wallLocations = getWallLocations(direction, arena);
        Location min = wallLocations.getMin();
        Location max = wallLocations.getMax();

        //est to west -1 x
        //west to est +1 x
        //north to south +1 z
        //south to north -1 z
        boolean isZ = wallLocations.getMin().getX() == wallLocations.getMax().getX();
        if (isZ) {
            for (int y = 0; y < wall.getHeight(); y++) {
                for (int z = 0; z < wall.getWidth(); z++) {
                    Material material = wallBlocks[y][z];
                    placeBlock(min, Material.AIR, 0, -y, -z, new Vector().add(direction.getTo()).multiply(offset-1));
                    placeBlock(min, material, 0, -y, -z, new Vector().add(direction.getTo()).multiply(offset));

                }
            }
        } else {
            for (int y = 0; y < wall.getHeight(); y++) {
                for (int x = 0; x < wall.getWidth(); x++) {
                    Material material = wallBlocks[y][x];
                    placeBlock(min, Material.AIR, -x, -y, 0, new Vector().add(direction.getTo()).multiply(offset-1));
                    placeBlock(min, material, -x, -y, 0, new Vector().add(direction.getTo()).multiply(offset));

                }
            }
        }
    }

    public CompletableFuture<Boolean> moveTask(Wall wall, Arena arena, Direction direction, Material[][] wallBlocks, int speed) {
        return CompletableFuture.supplyAsync(() -> {

            Semaphore semaphore = new Semaphore(0);

            Direction oppositeDirection =
                    direction.equals(Direction.NORTH) ? Direction.SOUTH :
                    direction.equals(Direction.SOUTH) ? Direction.NORTH :
                    direction.equals(Direction.EAST) ? Direction.WEST :
                    Direction.EAST;

            Arena.WallSpawn wallOppositesLocations = getWallLocations(oppositeDirection, arena);

            int distance = (int) arena.getWallSpawn(direction).getMin().distance(wallOppositesLocations.getMin());

            new BukkitRunnable() {
                int offset = 1;

                @Override
                public void run() {
                    moveWall(wall, arena, direction, wallBlocks, offset);
                    offset++;
                    if (offset >= distance) {
                        clearWall(wall, arena, direction, offset);
                        semaphore.release();
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, speed, speed);

            semaphore.acquireUninterruptibly();

            return true;
        });
    }

    private void clearWall(Wall wall, Arena arena, Direction direction, int offset) {
        Arena.WallSpawn wallLocations = getWallLocations(direction, arena);
        Location min = wallLocations.getMin();
        Location max = wallLocations.getMax();

        //est to west -1 x
        //west to est +1 x
        //north to south +1 z
        //south to north -1 z
        boolean isZ = wallLocations.getMin().getX() == wallLocations.getMax().getX();
        if (isZ) {
            for (int y = 0; y < wall.getHeight(); y++) {
                for (int z = 0; z < wall.getWidth(); z++) {
                    placeBlock(min, Material.AIR, 0, -y, -z, new Vector().add(direction.getTo()).multiply(offset-1));

                }
            }
        } else {
            for (int y = 0; y < wall.getHeight(); y++) {
                for (int x = 0; x < wall.getWidth(); x++) {
                    placeBlock(min, Material.AIR, -x, -y, 0, new Vector().add(direction.getTo()).multiply(offset-1));

                }
            }
        }
    }

    public Collection<Wall> getWalls() {
        return walls.values();
    }
}
