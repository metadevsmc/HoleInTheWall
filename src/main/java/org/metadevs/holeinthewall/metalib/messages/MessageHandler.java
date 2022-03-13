package org.metadevs.holeinthewall.metalib.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.metadevs.holeinthewall.metalib.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class MessageHandler <T extends JavaPlugin> {

    private final T plugin;
    private final String lang;
    private YamlConfiguration config;

    public MessageHandler(T plugin) {
        this.plugin = plugin;
        lang = plugin.getConfig().getString("lang", "en");

    }

    /**
     * Creates the lang folder and the lang file they do not exists and load them
     */
    public void init() {
        String fileName = lang+".yml";
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            Bukkit.getLogger().info("Lang folder not found, creating one!");
            if (!langFolder.mkdir()) {
                Bukkit.getLogger().severe("There was an error creating the lang folder");
                return;
            }
        }
        Bukkit.getLogger().info("Lang folder created");
        File langFile = new File(langFolder, fileName);
        if (!langFile.exists()) {
            if (saveFromResources()) {
                Bukkit.getLogger().info("Lang file found, copying from resources");
            } else {
                try {
                    if (langFile.createNewFile()) {
                        Bukkit.getLogger().info("Lang file not found, creating one!");
                    } else {
                        Bukkit.getLogger().severe("There was an error creating the lang file");
                    }
                } catch (IOException ignored) {
                    sendMessage(Bukkit.getConsoleSender(), "There was an error creating the lang file");
                }
            }
        }
        config = YamlConfiguration.loadConfiguration(langFile);
    }

    private boolean saveFromResources() {
        String resourcePath = "lang"+File.separator+lang+".yml";
        if (plugin.getResource(resourcePath) != null) {
            plugin.saveResource(resourcePath, false);
            return true;
        }
            return false;
    }


    /**
     * Return the message for the given key from the confing if found,
     * otherwise will return the default value specified.
     * The message is already colored
     * @param key the key of the message to get
     * @param def the default value for the message
     *
     * @return the message
     */
    private String getMessage(String key, String def) {

        return Utils.color(config.getString(key, def));

    }

    /**
     * Send the message to the audience
     * @param audience the Audience to send the message to
     * @param message the message to send
     */
    public void sendMessage(Audience audience, String message) {
        audience.sendMessage(Component.text(Utils.color(message)));
    }

    /**
     * Send the message to the audience
     * @param audience the Audience to send the message to
     * @param message the message to send
     */
    public void sendMessage(Audience audience, Component message) {
        audience.sendMessage(message);
    }

    /**
     * Send a message to any Audience retrieved from the config file or the default one
     * @param audience the Audience to send the message to
     * @param key the key of the message to send
     * @param def the default value for the message
     */
    public void sendMessage(Audience audience, String key, String def) {
      sendMessage(audience, getMessage(key, def));
    }

    /**
     * Send a message to any Audience retrieved from the config file or the default one
     * replacing the placeholders with the given values
     * @param audience the Audience to send the message to
     * @param key the key of the message to send
     * @param def the default value for the message
     * @param placeholders the values to replace the placeholders with
     */
    public void sendMessage(Audience audience, String key, String def, Placeholder... placeholders) {
        String message = getMessage(key, def);
        for (Placeholder placeholder : placeholders) {
            message = message.replace(placeholder.getKey(), placeholder.getValue());
        }
        sendMessage(audience, message);
    }

    public <A extends Audience>void  sendMessage(Collection<A> audience, String key, String def, Placeholder ...placeholders) {
        String message = getMessage(key, def);
        for (Placeholder placeholder : placeholders) {
            message = message.replace(placeholder.getKey(), placeholder.getValue());
        }
        for (A a : audience) {
            sendMessage(a, message);
        }
    }


    public String getMessage(String key, String def, Placeholder ...placeholders) {
        String message = getMessage(key, def);
        for (Placeholder placeholder : placeholders) {
            message = message.replace(placeholder.getKey(), placeholder.getValue());
        }
        return message;

    }
}
