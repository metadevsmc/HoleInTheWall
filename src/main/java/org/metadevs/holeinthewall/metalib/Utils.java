package org.metadevs.holeinthewall.metalib;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class Utils {


    private Utils() {
        throw new UnsupportedOperationException("Utils class. Can't be instantiated.");
    }

    public static String color(String toColor) {
        return ChatColor.translateAlternateColorCodes('&', toColor);
    }

    public static Region retrieveRegion(Player player) throws IncompleteRegionException {
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt((Player) player);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);
        Region region;

        World selectionWorld = localSession.getSelectionWorld();

        if (selectionWorld == null) throw new IncompleteRegionException();
        return region = localSession.getSelection(selectionWorld);
    }
}
