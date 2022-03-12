package org.metadevs.holeinthewall.metalib;

import org.bukkit.plugin.java.JavaPlugin;
import org.metadevs.holeinthewall.metalib.database.sqlite.SQLiteManager;
import org.metadevs.holeinthewall.metalib.messages.MessageHandler;

public final class MetaLibs<T extends JavaPlugin> {

    private final T plugin;
    private MessageHandler<T> messageHandler;
    private SQLiteManager<T> sqlite;

    public MetaLibs(T plugin) {
        this.plugin = plugin;
    }

    public T getPlugin() {
        return plugin;
    }

    public void init() {
        plugin.saveDefaultConfig();
        load();
    }

    public void load() {
        plugin.reloadConfig();
        loadManagers();

    }

    private void loadManagers() {

        sqlite = new SQLiteManager<>(plugin);
        sqlite.load();
        messageHandler = new MessageHandler<>(plugin);
        messageHandler.init();

    }


    public SQLiteManager<T> getSQLite() {
        return sqlite;
    }

    public MessageHandler<T> messageHandler() {
        return messageHandler;
    }



}
