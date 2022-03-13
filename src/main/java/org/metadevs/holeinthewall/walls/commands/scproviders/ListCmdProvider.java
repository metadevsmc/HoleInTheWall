package org.metadevs.holeinthewall.walls.commands.scproviders;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;
import org.metadevs.holeinthewall.walls.Wall;

import java.util.Collection;

public class ListCmdProvider extends SubCommand {

    private Collection<Wall> walls;

    public ListCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, true, args);
    }

    @Override
    public boolean validateArgs() {
        if ((walls = plugin.getWallsManager().getWalls()).isEmpty()) {
            messageHandler.sendMessage(player, "error.arena.no-arenas", "There are no arenas yet");
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        messageHandler.sendMessage(player, "general.wall.list-header", "&7Walls");
        for (Wall wall : walls) {
            //todo just in case the teleport
            String mex = messageHandler.getMessage("general.wall.list-entry", "&7{name}", new Placeholder("{name}", wall.getName()));
            Component component = Component.text("");
            for (String line : wall.getPattern()) {
                component = component.append(Component.text(line+"\n"));

            }
            messageHandler.sendMessage(player, Component.text(mex).hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, component)));

        }
    }
}
