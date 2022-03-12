package org.metadevs.holeinthewall.arena.commands.scproviders;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;

public class LeaveCmdProvider extends SubCommand {

    public LeaveCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, false, args);
    }

    @Override
    public boolean validateArgs() {
        if (!plugin.getPlayerManager().isPlaying(player)) {
            messageHandler.sendMessage(player, "error.not-playing", "&cYou are not currently playing any game.");
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        plugin.getPlayerManager().leaveArena(player);
        //todo teleport to lobby

        messageHandler.sendMessage(player, "success.arena.left", "&aYou have left the game . You will be teleported to the lobby.");
    }
}
