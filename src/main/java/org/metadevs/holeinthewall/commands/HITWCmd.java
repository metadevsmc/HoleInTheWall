package org.metadevs.holeinthewall.commands;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.commands.ArenaCmdProvider;
import org.metadevs.holeinthewall.metalib.messages.MessageHandler;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.walls.commands.WallCmdProvider;

import java.util.Arrays;

public class HITWCmd implements CommandExecutor {

    private final HoleInTheWall plugin;
    private MessageHandler<HoleInTheWall> messageHandler;


    public HITWCmd(HoleInTheWall plugin) {
        this.plugin = plugin;
        this.messageHandler = plugin.getMetaLibs().messageHandler();


    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            messageHandler.sendMessage(sender, "&cYou must be a player to execute this command");
            return true;
        }

        Player player = (Player) sender;
        boolean isAdmin = player.isOp();

        if (args.length == 0) {
            sendHelp(player, isAdmin);
            return true;
        }

        String subCommand = args[0];


        switch (subCommand.toLowerCase()) {
            case "arena":
                ArenaCmdProvider arenaCmdProvider = new ArenaCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                if (arenaCmdProvider.validateArgs())
                    arenaCmdProvider.execute();
                break;
            case "wall":
                WallCmdProvider wallCmdProvider = new WallCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                if (wallCmdProvider.validateArgs())
                    wallCmdProvider.execute();
                break;
            case "help":
                sendHelp(player, isAdmin);
                break;
            default:
                messageHandler.sendMessage(player, "error.invalid-subcommand", "&cInvalid subcommand", new Placeholder("{subcommand}", subCommand));
                return true;
        }


        return true;
    }

    public void sendHelp(Player sender, boolean isAdmin) {
        messageHandler.sendMessage(sender, "&7&m&l]-----------------[&r&c&lHoleInTheWall&7&m&l]-----------------[");
        sender.sendMessage("");
        messageHandler.sendMessage(sender, "&7[&a+&7] /hitw &a&lhelp &7- &aShow this help");
        messageHandler.sendMessage(sender, "&7[&a+&7] /hitw &a&larena &7- &aShow all arena related commands");
        if (isAdmin) {
            messageHandler.sendMessage(sender, "&7[&a+&7] /hitw &a&lwall &7- &aShow all wall related commands");
            messageHandler.sendMessage(sender, "&7[&a+&7] /hitw &a&lreload &7- &aReload the plugin");
        }
    }
}