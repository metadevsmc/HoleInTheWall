package org.metadevs.holeinthewall;

import org.bukkit.plugin.java.JavaPlugin;
import org.metadevs.holeinthewall.arena.ArenaManager;
import org.metadevs.holeinthewall.commands.HITWCmd;
import org.metadevs.holeinthewall.data.DataManager;
import org.metadevs.holeinthewall.metalib.MetaLibs;
import org.metadevs.holeinthewall.metalib.Utils;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.players.PlayerManager;
import org.metadevs.holeinthewall.players.listeners.*;
import org.metadevs.holeinthewall.walls.WallsManager;

public final class HoleInTheWall extends JavaPlugin {

    private MetaLibs<HoleInTheWall> metaLibs;
    private ArenaManager arenaManager;
    private DataManager dataManager;
    private PlayerManager playerManager;
    private WallsManager wallsManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        load();

    }

    public void load() {
        metaLibs = new MetaLibs<>(this);
        metaLibs.init();
        this.dataManager = new DataManager(this);
        dataManager.load();
        //lobbyManager = new LobbyManager(this);\
        wallsManager = new WallsManager(this);
        wallsManager.load();

        arenaManager = new ArenaManager(this);
        playerManager = new PlayerManager(this);


        registerCommands();
        registerListeners();
    }

    private void registerListeners() {
        //new ArenaListener(this);
        new PlayerInteractArena(this);
        new PlayerFall(this);
        new PlayerLose(this);
        new PlayerWin(this);
        new PlayerDamaged(this);
        new PlayerJoin(this);
        new PlayerLeave(this);
        new PlayerClickItem(this);

    }


    public void registerCommands() {
        getCommand("hitw").setExecutor(new HITWCmd(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }


    public DataManager getDataManager() {
        return dataManager;
    }

    public MetaLibs<HoleInTheWall> getMetaLibs() {
        return metaLibs;
    }

    public String getConfigString(String key, String def, Placeholder... placeholders) {
        String message = Utils.color(getConfig().getString(key, def));
        for (Placeholder placeholder : placeholders) {
            message = message.replace(placeholder.getKey(), placeholder.getValue());
        }
        return message;
    }

    public WallsManager getWallsManager() {
        return wallsManager;
    }
}
