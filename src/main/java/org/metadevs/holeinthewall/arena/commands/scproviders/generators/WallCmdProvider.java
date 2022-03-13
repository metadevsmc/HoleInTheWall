package org.metadevs.holeinthewall.arena.commands.scproviders.generators;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.arena.commands.scproviders.generators.sc.SetCmdProvider;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;
import org.metadevs.holeinthewall.enums.Direction;

import java.util.Arrays;

public class WallCmdProvider extends SubCommand {

    //hitw arena wall set <name> <direction>
    private Arena arena;
    private Direction direction;

    public WallCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, true, args);
    }

    @Override
    public boolean validateArgs() {
        if (requireAdmin) {
            if (!player.isOp()) {
                messageHandler.sendMessage(player, "error.only-admins" ,"I'm sorry, but only admins can use this commands.");
                return false;
            }
        }
        if (args.length < 1) {
            sendHelp();
            return  false;
        }

        return true;

    }

    private void sendHelp() {
        messageHandler.sendMessage(player, "&7&m&l]------------------[&r&aAena Wall&7&m&l]------------------[");
        player.sendMessage("");
        messageHandler.sendMessage(player, "\t&7- /hitw &carena &7wall set <name> <direction>");
        messageHandler.sendMessage(player, "   &7Directions: &anorth&7, &aeast&7, &asouth&7, &awest");
        messageHandler.sendMessage(player, "   &7Set the spawn of the arena wall for that direction");
        player.sendMessage("");
    }

    @Override
    public void execute() {

        String subCommand = args[0];
        switch (subCommand) {
            case "set":
                SetCmdProvider setCmdProvider = new SetCmdProvider(plugin, player, Arrays.copyOfRange(args, 1, args.length));
                if (setCmdProvider.validateArgs()) {
                    setCmdProvider.execute();
                }
                break;
            case "remove":

                break;
            default:
                sendHelp();
                break;
        }

    }
}
