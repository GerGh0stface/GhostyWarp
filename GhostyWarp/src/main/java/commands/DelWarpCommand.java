package commands;
import ghostywarp.GhostyWarp;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * /delwarp <n> - Deletes a warp.
 */
public class DelWarpCommand implements CommandExecutor {

    private final GhostyWarp plugin;

    public DelWarpCommand(GhostyWarp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("ghostywarp.delwarp")) {
            sender.sendMessage(plugin.getLangManager().get("messages.no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getLangManager().get("messages.delwarp-usage"));
            return true;
        }

        String name = args[0];

        if (!plugin.getWarpManager().exists(name)) {
            sender.sendMessage(plugin.getLangManager().get("messages.delwarp-not-found", "{warp}", name));
            return true;
        }

        plugin.getWarpManager().deleteWarp(name);
        sender.sendMessage(plugin.getLangManager().get("messages.delwarp-success", "{warp}", name));
        return true;
    }
}
