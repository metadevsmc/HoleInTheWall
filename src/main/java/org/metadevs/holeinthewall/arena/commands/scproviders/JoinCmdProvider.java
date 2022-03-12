package org.metadevs.holeinthewall.arena.commands.scproviders;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.enums.Status;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;

public class JoinCmdProvider extends SubCommand {

    private Arena arena;

    //hitw arena join <name>
    public JoinCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, false, args);
    }

    @Override
    public boolean validateArgs() {
        if (!checkArgs()) {
            return false;
        }

        arena = plugin.getArenaManager().getArena(args[0]);
        if (arena == null) {
            messageHandler.sendMessage(player, "error.arena.name-not-found", "&c The arena {name} does not exist", new Placeholder("{name}", args[0]));
            return false;
        }


        if (plugin.getPlayerManager().isPlaying(player)) {
            messageHandler.sendMessage(player, "error.arena.already-playing", "&c You are already playing in an arena", new Placeholder("{name}", args[0]));
            return false;
        }

        if (Status.ENDING.equals(arena.getStatus())|| Status.PLAYING.equals(arena.getStatus())) {
            messageHandler.sendMessage(player, "error.arena.already-playing", "&c The arena {name} is already started", new Placeholder("{name}", args[0]));
            return false;
        }

        if (arena.isFull()) {
            messageHandler.sendMessage(player, "error.arena.full", "&c The arena {name} is full", new Placeholder("{name}", args[0]));
            return false;
        }

        return true;
    }

    @Override
    public void execute() {
        plugin.getPlayerManager().joinArena(player, arena);
        messageHandler.sendMessage(player, "success.arena.joined", "&a You joined the arena {name}", new Placeholder("{name}", args[0]));
    }
}
