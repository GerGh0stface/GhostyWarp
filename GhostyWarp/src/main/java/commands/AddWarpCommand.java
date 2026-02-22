package commands;
import ghostywarp.GhostyWarp;


import manager.WarpManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /addwarp <name> - Creates a warp at the player's current location.
 */
public class AddWarpCommand implements CommandExecutor {

    private final GhostyWarp plugin;

    public AddWarpCommand(GhostyWarp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLangManager().get("messages.player-only"));
            return true;
        }

        if (!player.hasPermission("ghostywarp.addwarp")) {
            player.sendMessage(plugin.getLangManager().get("messages.no-permission"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getLangManager().get("messages.addwarp-usage"));
            return true;
        }

        String name = args[0];

        if (!WarpManager.isValidName(name)) {
            player.sendMessage(plugin.getLangManager().get("messages.addwarp-invalid-name"));
            return true;
        }

        if (plugin.getWarpManager().exists(name)) {
            player.sendMessage(plugin.getLangManager().get("messages.addwarp-exists", "{warp}", name));
            return true;
        }

        boolean created = plugin.getWarpManager().createWarp(name, player.getLocation());
        if (created) {
            player.sendMessage(plugin.getLangManager().get("messages.addwarp-success", "{warp}", name));
        }

        return true;
    }
}
