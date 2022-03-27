package org.metadevs.holeinthewall.arena.commands.scproviders.manage;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.Option;

public class UpdateCmdProvider extends SubCommand {

    //cases: lobby, spawn, spectator
    private Arena arena;
    private Option<Location> option;

    public UpdateCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, true, args);
    }

    @Override
    public boolean validateArgs() {
        if (checkArgs())
            return false;

        arena = plugin.getArenaManager().getArena(args[0]);
        if (arena == null) {
            messageHandler.sendMessage(player, "error.arena.name-not-exists", "&c The arena {name} does not exist", new Placeholder("{name}", args[0]));
            return false;
        }

        if (args.length == 1) {
            messageHandler.sendMessage(player, "error.arena.no-option-specified", "&c You must specify an option to update");
            return false;
        }

        switch (args[1]) {
            case "lobby":
            case "spawn":
            case "spectator":
            case "podium":
            case "loosers":
                option = new Option<>(args[1], player.getLocation());
                break;
            default:
                messageHandler.sendMessage(player, "error.invalid-option", "&c The option {option} is invalid", new Placeholder("{option}", args[1]));
                return false;
        }


        return true;
    }

    @Override
    public void execute() {
        arena.update(option);
        plugin.getDataManager().saveArena(arena);
        messageHandler.sendMessage(player, "success.arena.updated", "&a The arena {name} has been updated", new Placeholder("{name}", arena.getName()));
    }
}
