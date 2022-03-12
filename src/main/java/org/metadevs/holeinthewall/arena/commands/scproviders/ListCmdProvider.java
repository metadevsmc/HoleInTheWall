package org.metadevs.holeinthewall.arena.commands.scproviders;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;

import java.util.Collection;

public class ListCmdProvider extends SubCommand {

    private Collection<Arena> arenas;

    public ListCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, false, args);
    }

    @Override
    public boolean validateArgs() {
        if ((arenas = plugin.getArenaManager().getArenas()).isEmpty()) {
            messageHandler.sendMessage(player, "error.arena.no-arenas", "There are no arenas yet");
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        messageHandler.sendMessage(player, "general.arena.list-header", "Arenas");
        for (Arena arena : arenas) {
            //todo just in case the teleport
            messageHandler.sendMessage(player, "general.arena.list-entry", "&7{name} &7- &7{minPlayers}/{maxPlayers} &7- &7{status}",
                    new Placeholder("{name}", arena.getName()),
                    new Placeholder("{minPlayers}", ""+arena.getMinPlayers()),
                    new Placeholder("{maxPlayers}", ""+arena.getMaxPlayers()),
                    new Placeholder("{status}", arena.getStatus().getName()));
        }
    }
}
