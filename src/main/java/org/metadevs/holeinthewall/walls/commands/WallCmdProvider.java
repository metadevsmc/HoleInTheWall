package org.metadevs.holeinthewall.walls.commands;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.walls.commands.scproviders.CreateCmdProvider;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;

public class WallCmdProvider extends SubCommand {


    public WallCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, true, args);
    }

    @Override
    public boolean validateArgs() {
        if (requireAdmin) {
            if (!player.isOp()) {
                messageHandler.sendMessage(player, "error.only-admins" ,"I'm sorry, but only admins can use this commands.");
                return false;
            }
        }

        if (args.length < 1) {
            sendHelp();
            return false;
        }

        return true;
    }

    @Override
    public void execute() {

        String subCommand = args[0];
        switch (subCommand) {
            case "create":
                CreateCmdProvider createCmdProvider = new CreateCmdProvider(plugin, player, args);
                if (createCmdProvider.validateArgs()) {
                    createCmdProvider.execute();
                }
                break;
            case "delete":
                break;
            default:
                sendHelp();
                break;

        }

    }

    public void sendHelp() {
        messageHandler.sendMessage(player, "&7&m&l]------------------[&r&aWalls&7&m&l]------------------[");
        player.sendMessage("");
        messageHandler.sendMessage(player, "&7- /hitw &awall &7create <name>");
        messageHandler.sendMessage(player, "   &7Add a wall to the arena with the given name");
        player.sendMessage("");
        messageHandler.sendMessage(player, "&7- /hitw &awall &7delete <name>");
        messageHandler.sendMessage(player, "   &7Delete the wall with the given name");
        player.sendMessage("");
        messageHandler.sendMessage(player, "&7&m&l]------------------[&r&aAena&7&m&l]------------------[");
    }
}

