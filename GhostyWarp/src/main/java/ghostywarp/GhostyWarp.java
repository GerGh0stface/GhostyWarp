package ghostywarp;


import commands.AddWarpCommand;
import commands.DelWarpCommand;
import commands.GWCommand;
import commands.WarpsCommand;
import gui.WarpGUI;
import listener.GUIListener;
import manager.LangManager;
import manager.WarpManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * GhostyWarp - Main plugin class.
 *
 *  Commands:
 *    /warps          - Open warp GUI
 *    /addwarp <n>   - Create warp
 *    /delwarp <n>   - Delete warp
 *    /gw <sub>       - Admin commands (list, lore, slot, item, name, reload)
 */
public class GhostyWarp extends JavaPlugin {

    private LangManager langManager;
    private WarpManager warpManager;
    private WarpGUI warpGUI;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize managers
        langManager = new LangManager(this);
        warpManager = new WarpManager(this);
        warpGUI = new WarpGUI(this);

        // Register commands
        GWCommand gwCommand = new GWCommand(this);
        getCommand("warps").setExecutor(new WarpsCommand(this));
        getCommand("addwarp").setExecutor(new AddWarpCommand(this));
        getCommand("delwarp").setExecutor(new DelWarpCommand(this));
        getCommand("gw").setExecutor(gwCommand);
        getCommand("gw").setTabCompleter(gwCommand);

        // Register listeners
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        getLogger().info("╔════════════════════════════╗");
        getLogger().info("║     GhostyWarp enabled!     ║");
        getLogger().info("║  Warps: " + warpManager.getWarpCount() + "                    ║");
        getLogger().info("╚════════════════════════════╝");
    }

    @Override
    public void onDisable() {
        if (warpManager != null) {
            warpManager.save();
        }
        getLogger().info("GhostyWarp disabled. Warps saved.");
    }

    /**
     * Reloads config, language and warps.
     */
    public void reloadPlugin() {
        reloadConfig();
        langManager.reload();
        warpManager.load();
        warpGUI = new WarpGUI(this);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public LangManager getLangManager() { return langManager; }
    public WarpManager getWarpManager() { return warpManager; }
    public WarpGUI getWarpGUI()         { return warpGUI; }
}
