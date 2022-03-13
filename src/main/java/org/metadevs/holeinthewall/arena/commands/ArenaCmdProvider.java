package org.metadevs.holeinthewall.arena.commands;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.commands.scproviders.*;
import org.metadevs.holeinthewall.arena.commands.scproviders.generators.WallCmdProvider;
import org.metadevs.holeinthewall.arena.commands.scproviders.manage.CreateCmdProvider;
import org.metadevs.holeinthewall.arena.commands.scproviders.manage.DeleteCmdProvider;
import org.metadevs.holeinthewall.arena.commands.scproviders.manage.SetCmdProvider;
import org.metadevs.holeinthewall.arena.commands.scproviders.manage.UpdateCmdProvider;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;

import java.util.Arrays;

public class ArenaCmdProvider extends SubCommand {
    private final boolean isAdmin;

    public ArenaCmdProvider(HoleInTheWall plugin, Player sender, String... args) {
        super(plugin, sender, false, args);
        this.isAdmin = sender.isOp();

    }

    @Override
    public boolean validateArgs() {

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
                //hitw &carena &7create <name>
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
                ListCmdProvider listCmdProvider = new ListCmdProvider(plugin, player, args);
                if (listCmdProvider.validateArgs()) {
                    listCmdProvider.execute();
                }
                break;
            case "set":
                SetCmdProvider setCmdProvider = new SetCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                if (setCmdProvider.validateArgs()) {
                    setCmdProvider.execute();
                }
                break;
            case "update":
                UpdateCmdProvider updateCmdProvider = new UpdateCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                if (updateCmdProvider.validateArgs()) {
                    updateCmdProvider.execute();
                }
                break;
            case "join":
                JoinCmdProvider joinCmdProvider = new JoinCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                if (joinCmdProvider.validateArgs()) {
                    joinCmdProvider.execute();
                }
                break;
            case "leave":
                LeaveCmdProvider leaveCmdProvider = new LeaveCmdProvider(plugin, player);
                if (leaveCmdProvider.validateArgs()) {
                    leaveCmdProvider.execute();
                }
                break;
            case "info":
                InfoCmdProvider infoCmdProvider = new InfoCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                if (infoCmdProvider.validateArgs()) {
                    infoCmdProvider.execute();

                }
                break;
            case "start":
                StartCmdProvider startCmdProvider = new StartCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                if (startCmdProvider.validateArgs()) {
                    startCmdProvider.execute();
                }
                break;
            case "wall":
                WallCmdProvider wallSpawnCmdProvider = new WallCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                if (wallSpawnCmdProvider.validateArgs()) {
                    wallSpawnCmdProvider.execute();
                }
                break;
            default:
                sendHelp();
                break;


        }

    }

    private void sendHelp() {
        messageHandler.sendMessage(player, "&7&m&l]------------------[&r&aAena&7&m&l]------------------[");
        player.sendMessage("");
        if (isAdmin) {
            messageHandler.sendMessage(player, "\t&7- /hitw &carena &7create <name>");
            messageHandler.sendMessage(player, "   &7Create a new arena with the given name ");
            player.sendMessage("");
            messageHandler.sendMessage(player, "&7- /hitw &carena &7delete <name>");
            messageHandler.sendMessage(player, "   &7Delete the arena with the given name if exists");
            player.sendMessage("");
            messageHandler.sendMessage(player, "&7- /hitw &carena &7set <name> {<option>=<value>, ...}");
            messageHandler.sendMessage(player, "   &7Set the given option to the given value");
            player.sendMessage("");
            messageHandler.sendMessage(player, "&7- /hitw &carena &7update <name> {, ...}");
            messageHandler.sendMessage(player, "   &7Update the arena with the given name (options: lobby, spawn, spectator, loosers, podium");
            player.sendMessage("");
            messageHandler.sendMessage(player, "&7- /hitw &carena &7info <name>");
            messageHandler.sendMessage(player, "   &7Show the arena infos");
            player.sendMessage("");
            messageHandler.sendMessage(player, "&7- /hitw &carena &7info <name>");
            messageHandler.sendMessage(player, "   &7Show the arena infos");
            player.sendMessage("");
            messageHandler.sendMessage(player, "&7- /hitw &carena &7start <name>");
            messageHandler.sendMessage(player, "   &7Start the arena with the given name");
            player.sendMessage("");
            messageHandler.sendMessage(player, "&7- /hitw &carena &7wall ");
            messageHandler.sendMessage(player, "   &7Show the wall related command");
            player.sendMessage("");
        }
        messageHandler.sendMessage(player, "&7- /hitw &carena &7list");
        messageHandler.sendMessage(player, "   &7List all the arenas");
        player.sendMessage("");
        messageHandler.sendMessage(player, "&7- /hitw &carena &7join <name>");
        messageHandler.sendMessage(player, "   &7Join the arena with the given name");
        player.sendMessage("");
        messageHandler.sendMessage(player, "&7- /hitw &carena &7leave");
        messageHandler.sendMessage(player, "   &7Leave the current arena");
        player.sendMessage("");
        messageHandler.sendMessage(player, "&7&m&l]------------------[&r&aAena&7&m&l]------------------[");

    }

}
