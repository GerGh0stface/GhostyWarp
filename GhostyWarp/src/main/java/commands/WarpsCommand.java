package commands;
import ghostywarp.GhostyWarp;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /warps - Opens the warp GUI.
 */
public class WarpsCommand implements CommandExecutor {

    private final GhostyWarp plugin;

    public WarpsCommand(GhostyWarp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLangManager().get("messages.player-only"));
            return true;
        }

        if (!player.hasPermission("ghostywarp.use")) {
            player.sendMessage(plugin.getLangManager().get("messages.no-permission"));
            return true;
        }

        plugin.getWarpGUI().openFor(player);
        return true;
    }
}
