package org.metadevs.holeinthewall.arena.commands.scproviders.generators.sc;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.Utils;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;
import org.metadevs.holeinthewall.walls.Direction;

public class SetCmdProvider extends SubCommand {

    private Arena arena;
    private Direction direction;
    private Region region;
    //hitw arena wall set <name> <direction>
    public SetCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, true, args);
    }

    @Override
    public boolean validateArgs() {
        if (!checkArgs()) {
            return false;
        }

        if ((arena = plugin.getArenaManager().getArena(args[0])) == null) {
            messageHandler.sendMessage(player, "error.arena.name-not-exists", "The arena {name} does not exists.", new Placeholder("name", args[0]));
            return false;
        }

        if (args.length < 2) {
            messageHandler.sendMessage(player, "error.direction.missing", "You must specify a direction");
            return false;
        }

        if ((direction = Direction.fromString(args[1])) == null) {
            messageHandler.sendMessage(player, "error.invalid-direction", "{direction} is not a valid direction", new Placeholder("{direction}",args[1]));
            return false;
        }

        try {
            region = Utils.retrieveRegion(player);
        } catch (IncompleteRegionException e) {
            messageHandler.sendMessage(player, "error.region.incomplete", "&c The region is incomplete");
            return false;
        }
        return true;
    }

    @Override
    public void execute() {

        arena.setWallSpawn(direction,region);
        plugin.getDataManager().saveArena(arena);
        messageHandler.sendMessage(player, "success.wall-spawn.set", "The spawn of the wall {direction} has been set", new Placeholder("{direction}", direction.toString()));

    }
}
