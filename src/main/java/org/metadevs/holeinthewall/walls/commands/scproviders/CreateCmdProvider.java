package org.metadevs.holeinthewall.walls.commands.scproviders;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.metalib.Utils;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;


public class CreateCmdProvider extends SubCommand {

    private Region region;
    private String name;


    public CreateCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, false, args);
    }

    @Override
    public boolean validateArgs() {
        if (args.length < 1) {
            messageHandler.sendMessage(player, "error.wall.name-not-specified", "You must specify a name for the arena.");
            return  false;
        }
        name = args[0];
        if (plugin.getWallsManager().exists(name)) {
            messageHandler.sendMessage(player, "error.wall.name-already-exists", "&c The wall {name} already exists", new Placeholder("{name}", name));
            return false;
        }

        try {
            region = Utils.retrieveRegion(player);
        } catch (IncompleteRegionException e) {
            messageHandler.sendMessage(player, "error.region.incomplete");
            return false;
        }
        return true;
    }

    @Override
    public void execute() {

        plugin.getWallsManager().createWall(name, region);
        messageHandler.sendMessage(player, "success.wall.created", "&a The wall {name} has been created", new Placeholder("{name}", name));
    }


}
