package org.metadevs.holeinthewall.metalib.database.sqlite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteManager<T extends JavaPlugin> {
    private final T plugin;

    private File dbFile;
    private File dataFolder;

    private final String fileName;
    private SQLiteDataSource dataSource;

    public SQLiteManager(T plugin) {
        this.plugin = plugin;
        this.fileName = plugin.getConfig().getString("database", "hitw.db");

    }

    public void load() {
        createDataFolder();
        loadFile();
        initDataSource();

    }

    /**
     * Create the data folder if it doesn't exist
     *
     */
    private void createDataFolder() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            if (dataFolder.mkdir()) {
                plugin.getLogger().info("Created data folder");
            }
        }
    }

    /**
     * Create the file if it doesn't exist
     * or load it if it does
     */
    private void loadFile() {
        dbFile = new File(dataFolder, fileName);
        if (!dbFile.exists()) {
            try {
                if (dbFile.createNewFile()) {
                    plugin.getLogger().info("Created database file");
                } else {
                    plugin.getLogger().warning("Could not create database file");
                }
            } catch (Exception e) {
                Bukkit.getLogger().severe("Could not create database file!");
            }
        }
    }


    /**
     * Initialize the dataSource
     *
     */
    private void initDataSource() {
        dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        dataSource.setDatabaseName(fileName.substring(0, fileName.lastIndexOf('.')));
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
