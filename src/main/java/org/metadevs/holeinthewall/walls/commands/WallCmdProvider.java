package org.metadevs.holeinthewall.walls.commands;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.walls.commands.scproviders.DeleteCmdProvider;
import org.metadevs.holeinthewall.walls.commands.scproviders.CreateCmdProvider;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;
import org.metadevs.holeinthewall.walls.commands.scproviders.ListCmdProvider;

import java.util.Arrays;

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
                CreateCmdProvider createCmdProvider = new CreateCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                if (createCmdProvider.validateArgs()) {
                    createCmdProvider.execute();
                }
                break;
            case "delete":
                DeleteCmdProvider deleteCmdProvider = new DeleteCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                if (deleteCmdProvider.validateArgs()) {
                    deleteCmdProvider.execute();
                }
                break;
                case "list":
                    ListCmdProvider listCmdProvider = new ListCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                    if (listCmdProvider.validateArgs()) {
                        listCmdProvider.execute();
                    }
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
        messageHandler.sendMessage(player, "   &7Create a new wall with the given name.");
        player.sendMessage("");
        messageHandler.sendMessage(player, "&7- /hitw &awall &7delete <name>");
        messageHandler.sendMessage(player, "   &7Delete the wall with the given name");
        player.sendMessage("");
        messageHandler.sendMessage(player, "&7- /hitw &awall &7list");
        messageHandler.sendMessage(player, "   &7List all the walls");
        messageHandler.sendMessage(player, "&7&m&l]------------------[&r&aWalls&7&m&l]------------------[");
    }
}

