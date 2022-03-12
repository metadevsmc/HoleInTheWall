package org.metadevs.holeinthewall.arena.commands.scproviders.manage;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;

public class DeleteCmdProvider extends SubCommand {
    private String name;

    public DeleteCmdProvider(HoleInTheWall plugin, Player player, String[] args) {
        super(plugin, player,true, args);
    }

    public boolean validateArgs() {
        if (!checkArgs()) {
            return false;
        }
        String name = args[0];
        if (!plugin.getArenaManager().exists(name)) {
            messageHandler.sendMessage(player, "error.arena.name-not-exists", "The arena {name} does not exists.", new Placeholder("name", name));
            return false;
        }

        return true;
    }

    public void execute() {

        plugin.getArenaManager().deleteArena(name);
        messageHandler.sendMessage(player, "success.arena.deleted", "&a The arena {name} has been updated", new Placeholder("{name}", name));

    }
}
