package org.metadevs.holeinthewall.metalib.abstracts;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MetaListener<T extends JavaPlugin> implements Listener {

    protected final T plugin;

    public MetaListener(T plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


}


