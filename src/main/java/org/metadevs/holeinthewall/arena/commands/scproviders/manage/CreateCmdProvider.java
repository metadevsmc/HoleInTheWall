package org.metadevs.holeinthewall.arena.commands.scproviders.manage;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;

public class CreateCmdProvider extends SubCommand {
    private String name;
    public CreateCmdProvider(HoleInTheWall plugin, Player player, String[] args) {
        super(plugin, player,true, args);
    }

    public boolean validateArgs() {
        if (!checkArgs()) {
            return false;
        }
        String name = args[0];
        if (plugin.getArenaManager().exists(name)) {
            messageHandler.sendMessage(player, "error.arena.name-already-exists", "The arena {name} already exists.", new Placeholder("name", name));
            return false;
        }
        this.name = name;
        return true;
    }

    public void execute() {

        plugin.getArenaManager().createArena(name);
        messageHandler.sendMessage(player, "success.arena.created", "&a The arena {name} has been created", new Placeholder("{name}", name));

    }
}
