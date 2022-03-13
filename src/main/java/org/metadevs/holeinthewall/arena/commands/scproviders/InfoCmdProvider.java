package org.metadevs.holeinthewall.arena.commands.scproviders;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;

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
        messageHandler.sendMessage(player, "&aLobby: " + arena.getLobby());
        for (String s : arena.getLocations().keySet()) {
            Location loc = arena.getLocations().get(s);
            messageHandler.sendMessage(player, "&a" + s + ": " + loc.getWorld().getName() + " " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
        }
    }
}
