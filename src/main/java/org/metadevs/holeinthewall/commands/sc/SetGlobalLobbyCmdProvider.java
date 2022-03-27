package org.metadevs.holeinthewall.commands.sc;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;

public class SetGlobalLobbyCmdProvider extends SubCommand {
    public SetGlobalLobbyCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, true, args);
    }

    @Override
    public boolean validateArgs() {
        if (requireAdmin && !player.isOp()) {
            messageHandler.sendMessage(player, "error.only-admins", "&cOnly admins can use this command.");
            return false;
        }

        return true;
    }

    @Override
    public void execute() {
        plugin.getArenaManager().setGlobalLobby(player.getLocation());

        messageHandler.sendMessage(player, "info.global-lobby-set", "&aGlobal lobby set.");

    }
}
