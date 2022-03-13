package org.metadevs.holeinthewall.utils.abstracts;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.utils.interfaces.CommandProvider;
import org.metadevs.holeinthewall.metalib.messages.MessageHandler;

public abstract class SubCommand implements CommandProvider {

    protected final HoleInTheWall plugin;
    protected final Player player;
    protected final String[] args;
    protected final MessageHandler<HoleInTheWall> messageHandler;

    protected final boolean requireAdmin;

    public SubCommand(final HoleInTheWall plugin, final Player player,boolean requireAdmin, final String... args) {
        this.plugin = plugin;
        this.player = player;
        this.args = args;
        this.requireAdmin = requireAdmin;
        this.messageHandler = plugin.getMetaLibs().messageHandler();
    }

    public boolean checkArgs() {
        if (requireAdmin) {
            if (!player.isOp()) {
                messageHandler.sendMessage(player, "error.only-admins" ,"I'm sorry, but only admins can use this commands.");
                return false;
            }
        }
        if (args.length < 1) {
            messageHandler.sendMessage(player, "error.arena.name-not-specified", "You must specify a name for the arena.");
            return  false;
        }
        return true;
    }


}

