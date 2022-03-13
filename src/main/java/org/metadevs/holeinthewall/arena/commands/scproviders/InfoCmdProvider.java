package org.metadevs.holeinthewall.arena.commands.scproviders;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;
import org.metadevs.holeinthewall.enums.Direction;

public class InfoCmdProvider extends SubCommand {

    private Arena arena;

    public InfoCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, true, args);
    }

    @Override
    public boolean validateArgs() {
        if (!checkArgs()) {
            return false;
        }
        String name = args[0];
        if (!plugin.getArenaManager().exists(name)) {
            messageHandler.sendMessage(player, "error.arena.name-not-exists", "The arena {name} does not exists.", new Placeholder("name", name));
            return false;
        }
        arena = plugin.getArenaManager().getArena(name);
        return true;
    }

    @Override
    public void execute() {
        messageHandler.sendMessage(player, "&aInfos: ");
        messageHandler.sendMessage(player, "&aName: " + arena.getName());
        messageHandler.sendMessage(player, "&aStatus: " + arena.getStatus());
        messageHandler.sendMessage(player, "&aPlayers: " + arena.getPlayers().size());
        messageHandler.sendMessage(player, "&aSpectators: " + arena.getSpectators().size());
        messageHandler.sendMessage(player, "&aMax players: " + arena.getMaxPlayers());
        messageHandler.sendMessage(player, "&aMin players: " + arena.getMinPlayers());
        messageHandler.sendMessage(player, "&aTeleport points: ");
        messageHandler.sendMessage(player, (arena.getLocations().containsKey("lobby") ? "&a" : "&c") + "Lobby location");
        messageHandler.sendMessage(player, (arena.getLocations().containsKey("spectator") ? "&a" : "&c") + "Spectator location");
        messageHandler.sendMessage(player, (arena.getLocations().containsKey("spawn") ? "&a" : "&c") + "Spawn location");
        messageHandler.sendMessage(player, (arena.getLocations().containsKey("loosers") ? "&a" : "&c") + "loosers location");
        messageHandler.sendMessage(player, (arena.getLocations().containsKey("podium") ? "&a" : "&c") + "podium location");
        for (Direction direction : Direction.values()) {
            messageHandler.sendMessage(player, (arena.getWallSpawn(direction) != null ? "&a" : "&c") + direction.name() + " location");
        }
    }
}
