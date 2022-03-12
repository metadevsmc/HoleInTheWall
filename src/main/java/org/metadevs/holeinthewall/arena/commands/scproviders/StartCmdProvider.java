package org.metadevs.holeinthewall.arena.commands.scproviders;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.enums.Status;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;

public class StartCmdProvider extends SubCommand {

    private Arena arena;

    public StartCmdProvider(HoleInTheWall plugin, Player player, String... args) {
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

        if(!arena.getStatus().equals(Status.LOBBY) && !arena.getStatus().equals(Status.STARTING)) {
            messageHandler.sendMessage(player, "error.arena.not-lobby", "The arena {name} is not in lobby or starting status.", new Placeholder("name", name));
            return false;
        }

        return true;
    }

    @Override
    public void execute() {
        arena.initCooldown(plugin);
        messageHandler.sendMessage(player, "success.arena.start", "The arena {name} has been started.", new Placeholder("name", arena.getName()));
    }
}
