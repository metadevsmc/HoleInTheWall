package org.metadevs.holeinthewall.arena.commands.scproviders;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.enums.Status;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;

public class StopCmdProvider extends SubCommand {

    private Arena arena;

    public StopCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, true, args);
    }

    @Override
    public boolean validateArgs() {
        if (checkArgs()) {
            return false;
        }
        String name = args[0];
        if (!plugin.getArenaManager().exists(name)) {
            messageHandler.sendMessage(player, "error.arena.name-not-exists", "The arena {name} does not exists.", new Placeholder("{name}", name));
            return false;
        }
        arena = plugin.getArenaManager().getArena(name);

        if(!arena.getStatus().equals(Status.PLAYING)) {
            messageHandler.sendMessage(player, "error.arena.not-started", "The arena {name} is not started yet", new Placeholder("{name}", name));
            return false;
        }

        return true;
    }

    @Override
    public void execute() {
        plugin.getArenaManager().endGame(arena);
        messageHandler.sendMessage(player, "success.arena.stop", "The arena {name} has been stopped", new Placeholder("{name}", arena.getName()));
    }


}
