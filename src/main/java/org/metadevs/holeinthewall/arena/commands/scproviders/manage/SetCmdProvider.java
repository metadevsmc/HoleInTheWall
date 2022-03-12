package org.metadevs.holeinthewall.arena.commands.scproviders.manage;

import org.bukkit.entity.Player;
import org.metadevs.holeinthewall.HoleInTheWall;
import org.metadevs.holeinthewall.arena.Arena;
import org.metadevs.holeinthewall.metalib.messages.Placeholder;
import org.metadevs.holeinthewall.utils.abstracts.SubCommand;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class SetCmdProvider extends SubCommand {

    private ConcurrentHashMap<String, Object> options;
    private Arena arena;
    //             -    -    0       1            2
    //usage hitw arena set <name> {option=value, ...}

    public SetCmdProvider(HoleInTheWall plugin, Player player, String... args) {
        super(plugin, player, true, args);
        options = new ConcurrentHashMap<>();
    }


    @Override
    public boolean validateArgs() {
        if (!checkArgs()) {
            messageHandler.sendMessage(player, "options: max-players, min-players, min-y");
            return false;
        }

        String arenaName = args[0];
        arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            messageHandler.sendMessage(player, "error.arena.name-not-exists", "&c The arena {name} does not exist", new Placeholder("{name}", arenaName));
            return false;
        }
        if (args.length == 1) {
            messageHandler.sendMessage(player, "error.no-option-specified", "&c No option specified");
            return false;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]);
        }

        for (String optionEntry : sb.toString().trim().split(",")) {


            String[] option = optionEntry.split("=");
            switch (option[0].toLowerCase(Locale.ROOT)) {
                case "max-players":
                    try {
                        options.put("max-players", Integer.parseInt(option[1]));
                    } catch (NumberFormatException e) {
                        messageHandler.sendMessage(player, "error.invalid-option-value", "&c {value} is not a valid value for {option}", new Placeholder("{value}", option[1]), new Placeholder("{option}", option[0]));
                        return false;
                    }
                    break;
                case "min-players":
                    try {
                        options.put("min-players", Integer.parseInt(option[1]));
                    } catch (NumberFormatException e) {
                        messageHandler.sendMessage(player, "error.invalid-option-value", "&c {value} is not a valid value for {option}", new Placeholder("{value}", option[1]), new Placeholder("{option}", option[0]));
                        return false;
                    }
                    break;
                case "min-y":
                    try {
                        options.put("min-y", Integer.parseInt(option[1]));
                    } catch (NumberFormatException e) {
                        messageHandler.sendMessage(player, "error.invalid-option-value", "&c {value} is not a valid value for {option}", new Placeholder("{value}", option[1]), new Placeholder("{option}", option[0]));
                        return false;
                    }
                    break;
                default:
                    messageHandler.sendMessage(player, "error.invalid-option", "&c {option} is not a valid option", new Placeholder("{option}", option[0]));
                    return false;
            }
        }
        return true;
    }

    @Override
    public void execute() {
        arena.setOptions(options);
        plugin.getDataManager().saveArena(arena);
        messageHandler.sendMessage(player, "success.arena.updated", "&a The arena {name} has been updated", new Placeholder("{name}", arena.getName()));

    }
}
