package manager;
import ghostywarp.GhostyWarp;


import model.WarpData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manages all warps: loading, saving, creating, and deleting.
 */
public class WarpManager {

    private final GhostyWarp plugin;
    private final Map<String, WarpData> warps = new LinkedHashMap<>();
    private File warpsFile;
    private FileConfiguration warpsConfig;

    public WarpManager(GhostyWarp plugin) {
        this.plugin = plugin;
        load();
    }

    // ── Load / Save ──────────────────────────────────────────────────────────

    /**
     * Loads warps from warps.yml
     */
    public void load() {
        warpsFile = new File(plugin.getDataFolder(), "warps.yml");

        if (!warpsFile.exists()) {
            plugin.saveResource("warps.yml", false);
        }

        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
        warps.clear();

        ConfigurationSection section = warpsConfig.getConfigurationSection("warps");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection ws = section.getConfigurationSection(key);
            if (ws == null) continue;

            String world = ws.getString("world", "world");
            double x = ws.getDouble("x", 0);
            double y = ws.getDouble("y", 64);
            double z = ws.getDouble("z", 0);
            float yaw = (float) ws.getDouble("yaw", 0);
            float pitch = (float) ws.getDouble("pitch", 0);

            String matName = ws.getString("material", "ENDER_PEARL");
            Material material = Material.matchMaterial(matName);
            if (material == null) material = Material.ENDER_PEARL;

            String displayName = ws.getString("display-name",
                    plugin.getConfig().getString("default-warp-item.display-name", "&b✦ &f{warp}")
                            .replace("{warp}", key));

            List<String> lore = ws.getStringList("lore");
            if (lore.isEmpty()) {
                lore = buildDefaultLore(key, world, x, y, z);
            }

            int slot = ws.getInt("slot", -1);

            warps.put(key.toLowerCase(), new WarpData(key, world, x, y, z, yaw, pitch,
                    material, displayName, lore, slot));
        }

        plugin.getLogger().info("Loaded " + warps.size() + " warp(s).");
    }

    /**
     * Saves all warps to warps.yml
     */
    public void save() {
        warpsConfig.set("warps", null); // Clear

        for (WarpData warp : warps.values()) {
            String path = "warps." + warp.getName();
            warpsConfig.set(path + ".world", warp.getWorld());
            warpsConfig.set(path + ".x", warp.getX());
            warpsConfig.set(path + ".y", warp.getY());
            warpsConfig.set(path + ".z", warp.getZ());
            warpsConfig.set(path + ".yaw", warp.getYaw());
            warpsConfig.set(path + ".pitch", warp.getPitch());
            warpsConfig.set(path + ".material", warp.getMaterial().name());
            warpsConfig.set(path + ".display-name", warp.getDisplayName());
            warpsConfig.set(path + ".lore", warp.getLore());
            warpsConfig.set(path + ".slot", warp.getSlot());
        }

        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save warps.yml: " + e.getMessage());
        }
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public boolean createWarp(String name, Location location) {
        if (warps.containsKey(name.toLowerCase())) return false;

        String world = location.getWorld() != null ? location.getWorld().getName() : "world";

        // Build default display name from config
        String displayName = plugin.getConfig()
                .getString("default-warp-item.display-name", "&b✦ &f{warp}")
                .replace("{warp}", name);

        // Build default lore from config
        List<String> defaultLore = plugin.getConfig().getStringList("default-warp-item.lore");
        List<String> lore = new ArrayList<>();
        for (String line : defaultLore) {
            lore.add(line
                    .replace("{warp}", name)
                    .replace("{world}", world)
                    .replace("{x}", String.valueOf((int) location.getX()))
                    .replace("{y}", String.valueOf((int) location.getY()))
                    .replace("{z}", String.valueOf((int) location.getZ())));
        }

        String matName = plugin.getConfig().getString("default-warp-item.material", "ENDER_PEARL");
        Material material = Material.matchMaterial(matName);
        if (material == null) material = Material.ENDER_PEARL;

        WarpData warp = new WarpData(
                name, world,
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch(),
                material, displayName, lore, -1
        );

        warps.put(name.toLowerCase(), warp);
        save();
        return true;
    }

    public boolean deleteWarp(String name) {
        WarpData removed = warps.remove(name.toLowerCase());
        if (removed == null) return false;
        save();
        return true;
    }

    public WarpData getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public boolean exists(String name) {
        return warps.containsKey(name.toLowerCase());
    }

    public Collection<WarpData> getAllWarps() {
        return warps.values();
    }

    public int getWarpCount() {
        return warps.size();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private List<String> buildDefaultLore(String warp, String world, double x, double y, double z) {
        List<String> lore = new ArrayList<>();
        List<String> template = plugin.getConfig().getStringList("default-warp-item.lore");
        for (String line : template) {
            lore.add(line
                    .replace("{warp}", warp)
                    .replace("{world}", world)
                    .replace("{x}", String.valueOf((int) x))
                    .replace("{y}", String.valueOf((int) y))
                    .replace("{z}", String.valueOf((int) z)));
        }
        return lore;
    }

    /**
     * Validates a warp name (alphanumeric, hyphens, underscores only).
     */
    public static boolean isValidName(String name) {
        return name != null && name.matches("[a-zA-Z0-9_\\-]+");
    }
}
