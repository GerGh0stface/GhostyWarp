package listener;
import ghostywarp.GhostyWarp;


import gui.WarpGUI;
import manager.LangManager;
import model.WarpData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Handles click events inside the warp GUI.
 */
public class GUIListener implements Listener {

    private final GhostyWarp plugin;

    public GUIListener(GhostyWarp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Check if this is the GhostyWarp GUI by matching the title
        String guiTitle = LangManager.color(plugin.getConfig().getString("gui.title", "&8✦ &bGhostyWarp &8✦"));
        String inventoryTitle = event.getView().getTitle();

        if (!inventoryTitle.equals(guiTitle)) return;

        // Always cancel clicks inside our GUI
        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(event.getView().getTopInventory())) return;

        int slot = event.getSlot();
        String warpName = WarpGUI.getWarpAtSlot(player.getUniqueId(), slot);
        if (warpName == null) return;

        WarpData warp = plugin.getWarpManager().getWarp(warpName);
        if (warp == null) {
            player.sendMessage(plugin.getLangManager().get("messages.warp-not-found",
                    "{warp}", warpName));
            player.closeInventory();
            return;
        }

        // Teleport
        World world = Bukkit.getWorld(warp.getWorld());
        if (world == null) {
            player.sendMessage(plugin.getLangManager().get("messages.teleport-world-not-found",
                    "{warp}", warp.getName()));
            player.closeInventory();
            return;
        }

        Location destination = new Location(world,
                warp.getX(), warp.getY(), warp.getZ(),
                warp.getYaw(), warp.getPitch());

        player.closeInventory();
        player.teleport(destination);
        player.sendMessage(plugin.getLangManager().get("messages.teleport-success",
                "{warp}", warp.getName()));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            // Only clean up if it was our GUI
            String guiTitle = LangManager.color(plugin.getConfig().getString("gui.title", "&8✦ &bGhostyWarp &8✦"));
            if (event.getView().getTitle().equals(guiTitle)) {
                WarpGUI.clearPlayer(player.getUniqueId());
            }
        }
    }
}
