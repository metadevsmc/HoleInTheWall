package org.metadevs.holeinthewall.arena;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.metalib.messages.MessageHandler;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ArenaManager {

    private final HoleInTheWall plugin;
    private final ConcurrentHashMap<String, Arena> arenas;
    private final MessageHandler<HoleInTheWall> messaageHandler;

    public ArenaManager(HoleInTheWall holeInTheWall) {
        this.plugin = holeInTheWall;
        this.arenas = new ConcurrentHashMap<>();
        this.messaageHandler = plugin.getMetaLibs().messageHandler();
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
     * @param name The name of the arena.
     * @return The arena with the given name. Null if the arena doesn't exist.
     */
    @Nullable
    public Arena getArena(String name) {
        return arenas.get(name);
    }

    /**
     * Checks if the arena exists.
     * @param name The name of the arena.
     * @return True if the arena exists, false otherwise.
     */
    public boolean exists(String name) {
        return arenas.containsKey(name);
    }

    /**
     * Creates a new arena.
     * @param name The name of the arena.
     *
     */
    public void createArena(String name) {
        Arena arena = new Arena(name, plugin.getConfig().getInt("arena.default-min-players", 12), plugin.getConfig().getInt("arena.default-max-players", 24));
        arenas.put(name, arena);
        plugin.getDataManager().saveArena(arena);
        //todo create arena in database
    }

    /**
     * Removes an arena.
     * @param name The name of the arena.
     *             If the arena doesn't exist, nothing happens.
     *             If the arena is running, it will be stopped.
     *             If the arena is not running, it will be removed.
     *
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

        //todo startup logic

    }


    public void initArenaCooldown(Arena arena) {

        arena.initCooldown(plugin);
    }


}
