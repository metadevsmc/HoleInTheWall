package org.metadevs.holeinthewall.walls;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.enums.Direction;
import org.metadevs.holeinthewall.enums.Status;

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

    public void generateWall(Wall wall, Arena arena, Direction direction, Material[][] wallBlocks) {

        Arena.WallSpawn wallLocations = getWallLocations(direction, arena);

        boolean isZ = wallLocations.getMin().getX() == wallLocations.getMax().getX();
        if (isZ) {

            for (int y = 0; y < wall.getHeight(); y++) {
                for (int z = 0; z < wall.getWidth(); z++) {
                    Material material = wallBlocks[y][z];
                    placeBlock(wallLocations.getMax(), material, 0, -y, -z);
                    //spawnSlime(wallLocations.getMax(), 0, -y, -z);


                }
            }
        } else {
            for (int y = 0; y < wall.getHeight(); y++) {
                for (int x = 0; x < wall.getWidth(); x++) {
                    Material material = wallBlocks[y][x];
                    placeBlock(wallLocations.getMax(), material, -x, -y, 0);
                    //spawnSlime(wallLocations.getMax(), -x, -y, 0);

                }
            }
        }
    }

    private void spawnSlime(Location max, int i, int i1, int i2) {
        Slime entity = (Slime) max.getWorld().spawnEntity(max.clone().add(i, i1, i2), EntityType.SLIME);
        entity.setSize(1);
        entity.setCustomNameVisible(false);
        entity.setInvisible(true);
        entity.setCollidable(true);
        entity.setAI(false);
        entity.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOW, Integer.MAX_VALUE, 10000));
    }

    private void spawnSlime(Location loc) {
        Slime entity = (Slime) loc.getWorld().spawnEntity(loc, EntityType.SLIME);
        entity.setSize(1);
        entity.setCustomNameVisible(false);
        entity.setInvisible(true);
        entity.setCollidable(true);
        entity.setAI(false);
        entity.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOW, Integer.MAX_VALUE, 10000));
    }


    private Arena.WallSpawn getWallLocations(Direction direction, Arena arena) {
        return arena.getWallSpawn(direction);
    }


    private void placeBlock(Location loc, Material material, int offsetX, int offsetY, int offsetZ) {
        Location locF = loc.clone().add(offsetX, offsetY, offsetZ);
        Block block = locF.getBlock();
        block.setType(material);
    }

    private void placeBlock(Location loc, Material material) {
        loc.getBlock().setType(material);
    }

    private void moveWall(Wall wall, Arena arena, Direction direction, Material[][] wallBlocks, int offset) {

        Arena.WallSpawn wallLocations = getWallLocations(direction, arena);
        Location min = wallLocations.getMin();
        Location max = wallLocations.getMax();

        //est to west -1 x
        //west to est +1 x
        //north to south +1 z
        //south to north -1 z
        boolean isZ = min.getX() == max.getX();
        Vector vector = direction.getTo();
        if (isZ) {

            for (int y = 0; y < wall.getHeight(); y++) {
                for (int z = 0; z < wall.getWidth(); z++) {
                    Material material = wallBlocks[y][z];
                    Location loc = getBlockLocation(max, 0, -y, -z, vector.clone().multiply(offset - 1));
                    placeBlock(loc, Material.AIR);
                    //loc.getNearbyEntitiesByType(EntityType.SLIME.getEntityClass(), 1).forEach(Entity::remove);
                    loc = getBlockLocation(max, 0, -y, -z, vector.clone().multiply(offset));

                    placeBlock(loc, material);
                    //spawnSlime(loc);


                }
            }
        } else {

            for (int y = 0; y < wall.getHeight(); y++) {
                for (int x = 0; x < wall.getWidth(); x++) {
                    Material material = wallBlocks[y][x];
                    Location loc = getBlockLocation(max, -x, -y, 0, vector.clone().multiply(offset - 1));
                    placeBlock(loc, Material.AIR);
                    //loc.getNearbyEntitiesByType(EntityType.SLIME.getEntityClass(), 1).forEach(Entity::remove);
                    loc = getBlockLocation(max, -x, -y, 0, vector.clone().multiply(offset));

                    placeBlock(loc, material);


                    //spawnSlime(loc);
                    //loc = getBlockLocation(max, -x, -y, 0, vector.clone().multiply(offset));


                }
            }
        }
    }

    public CompletableFuture<Boolean> moveTask(Wall wall, Arena arena, Direction direction, Material[][] wallBlocks,
                                               int speed) {
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
                    
                    checkCollision(wall, arena, direction, wallBlocks, offset);
                    moveWall(wall, arena, direction, wallBlocks, offset);
                    offset++;
                    if (arena.getNumberOfPlayers() == 1) {
                        clearWall(wall, arena, direction, offset);
                        this.cancel();
                        return;
                    }
                    if (offset >= distance || arena.getStatus() == Status.ENDING) {
                        clearWall(wall, arena, direction, offset);
                        semaphore.release();
                        this.cancel();
                    }
                    Arena.WallSpawn wallLocations = getWallLocations(direction, arena);
                    if (wallLocations.getMin() == null || wallLocations.getMax() == null) {
                        plugin.getLogger().warning("WallLocations is null");
                        return;

                    }
                }
            }.runTaskTimer(plugin, speed, speed);

            semaphore.acquireUninterruptibly();

            return true;
        });
    }

    private void checkCollision(Wall wall, Arena arena, Direction direction, Material[][] wallBlocks, int offset) {
        Arena.WallSpawn wallLocations = getWallLocations(direction, arena);
        Location min = wallLocations.getMin().clone();
        Location max = wallLocations.getMax().clone();
        Location traslatedMin = min.add(direction.getTo().multiply(offset-1));
        Location traslatedMax = max.add(direction.getTo().multiply(offset));
        traslatedMin.getBlock().setType(Material.RED_WOOL);
        traslatedMax.getBlock().setType(Material.RED_WOOL);

        for (Player player : arena.getPlayers()) {
            if (plugin.getPlayerManager().isPlayerInArea(player, traslatedMin, traslatedMax)) {
//                Location relativeFoot = traslatedMax.clone().subtract(player.getLocation().getBlock().getLocation());
//                boolean isZ = relativeFoot.getBlockZ() == traslatedMax.getBlockZ();
//                if (isZ) {
////                    if (wallBlocks[Math.abs(relativeFoot.getBlockY())][Math.abs(relativeFoot.getBlockZ())].isCollidable()
////                        && wallBlocks[Math.abs(relativeFoot.getBlockY())- 1][Math.abs(relativeFoot.getBlockZ())].isCollidable()) {
////
//                       player.setVelocity(direction.getTo().clone().multiply(1.3));
////                    }
//                } else {
////                    if (wallBlocks[Math.abs(relativeFoot.getBlockY())][Math.abs(relativeFoot.getBlockX())].isCollidable()
// //                           && wallBlocks[Math.abs(relativeFoot.getBlockY())-1][Math.abs(relativeFoot.getBlockX())].isCollidable()) {
//                    player.setVelocity(direction.getTo().clone().multiply(1.5));
//   //                 }
//                }
                boolean check = player.getLocation().clone().add(direction.getTo().clone()).getBlock().getType() == Material.AIR;
                boolean check2 = player.getEyeLocation().clone().add(direction.getTo().clone()).getBlock().getType() == Material.AIR;

                Bukkit.broadcastMessage(player.getName() + " -> " + check + " " + check2);



            }
        }

    }

    private boolean checkPlayerIn(Player player, Location min, Location max) {
        return player.getLocation().getX() >= min.getX() && player.getLocation().getX() <= max.getX() && player.getLocation().getZ() >= min.getZ() && player.getLocation().getZ() <= max.getZ();
    }

    public boolean isInArea(Location loc, Location min, Location max) {
        return (min.getX() <= loc.getX()
                && min.getY() <= loc.getY()
                && min.getZ() <= loc.getZ()
                && max.getX() >= loc.getX()
                && max.getY() >= loc.getY()
                && max.getZ() >= loc.getZ());
    }

    private void clearWall(Wall wall, Arena arena, Direction direction, int offset) {
        Arena.WallSpawn wallLocations = getWallLocations(direction, arena);
        Location min = wallLocations.getMin();
        Location max = wallLocations.getMax();

        //est to west -1 x
        //west to est +1 x
        //north to south +1 z
        //south to north -1 z
        Vector vector = direction.getTo();
        boolean isZ = min.getX() == max.getX();
        if (isZ) {
            for (int y = 0; y < wall.getHeight(); y++) {
                for (int z = 0; z < wall.getWidth(); z++) {
                    Location loc = getBlockLocation(max, 0, -y, -z, vector.clone().multiply(offset - 1));
                    placeBlock(loc, Material.AIR);

                }
            }
        } else {
            for (int y = 0; y < wall.getHeight(); y++) {
                for (int x = 0; x < wall.getWidth(); x++) {
                    Location loc = getBlockLocation(max, -x, -y, 0, vector.clone().multiply(offset - 1));
                    placeBlock(loc, Material.AIR);
                }
            }
        }
    }

    private Location getBlockLocation(Location location, int x, int y, int z, Vector vector) {
        return location.clone().add(x, y, z).add(vector);
    }

    public Collection<Wall> getWalls() {
        return walls.values();
    }

}
