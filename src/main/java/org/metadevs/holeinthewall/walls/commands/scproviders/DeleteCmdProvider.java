package org.metadevs.holeinthewall.walls.commands.scproviders;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;

public class DeleteCmdProvider extends SubCommand {
    private String name;

    public DeleteCmdProvider(HoleInTheWall plugin, Player player, String[] args) {
        super(plugin, player,true, args);
    }

    public boolean validateArgs() {
        if (args.length < 1) {
            messageHandler.sendMessage(player, "error.wall.name-not-specified", "You must specify a name for the wall.");
            return  false;
        }

        name = args[0];
        if (!plugin.getWallsManager().exists(name)) {
            messageHandler.sendMessage(player, "error.wall.name-not-exists", "The wall {name} does not exists.", new Placeholder("{name}", name));
            return false;
        }

        return true;
    }

    public void execute() {

        plugin.getWallsManager().deleteWall(name);
        messageHandler.sendMessage(player, "success.wall.deleted", "&a The wall {name} has been deleted", new Placeholder("{name}", name));

    }
}
