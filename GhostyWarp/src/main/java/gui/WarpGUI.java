package gui;
import ghostywarp.GhostyWarp;


import manager.LangManager;
import manager.WarpManager;
import model.WarpData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Builds the warp GUI inventory and tracks which slot maps to which warp.
 */
public class WarpGUI {

    private final GhostyWarp plugin;
    private final WarpManager warpManager;

    // Maps inventory slot -> warp name (for click handling)
    private static final Map<UUID, Map<Integer, String>> openInventories = new HashMap<>();

    public WarpGUI(GhostyWarp plugin) {
        this.plugin = plugin;
        this.warpManager = plugin.getWarpManager();
    }

    /**
     * Opens the warp GUI for the given player.
     */
    public void openFor(Player player) {
        int rows = Math.min(6, Math.max(1, plugin.getConfig().getInt("gui.rows", 6)));
        int size = rows * 9;

        Collection<WarpData> allWarps = warpManager.getAllWarps();

        if (allWarps.isEmpty()) {
            player.sendMessage(plugin.getLangManager().get("messages.warps-empty"));
            return;
        }

        String title = LangManager.color(plugin.getConfig().getString("gui.title", "&8✦ &bGhostyWarp &8✦"));
        Inventory inv = Bukkit.createInventory(null, size, title);

        boolean borderOnly = plugin.getConfig().getBoolean("gui.border-only", true);
        boolean fillEmpty = plugin.getConfig().getBoolean("gui.fill-empty", true);

        // Fill with decoration
        if (fillEmpty) {
            ItemStack filler = buildFiller();
            if (borderOnly) {
                fillBorder(inv, filler, rows);
            } else {
                for (int i = 0; i < size; i++) {
                    inv.setItem(i, filler);
                }
            }
        }

        // Track slot->warp mapping for this player
        Map<Integer, String> slotMap = new HashMap<>();

        // Determine which slots are already "locked" by warps with explicit slots
        Set<Integer> usedSlots = new HashSet<>();
        if (fillEmpty && borderOnly) {
            addBorderSlots(usedSlots, rows);
        }

        // First pass: place warps with explicit slots
        List<WarpData> autoPlaceWarps = new ArrayList<>();
        for (WarpData warp : allWarps) {
            int slot = warp.getSlot();
            if (slot >= 0 && slot < size) {
                inv.setItem(slot, buildWarpItem(warp));
                slotMap.put(slot, warp.getName());
                usedSlots.add(slot);
            } else {
                autoPlaceWarps.add(warp);
            }
        }

        // Second pass: auto-place remaining warps in the first available slot
        int autoSlot = 0;
        for (WarpData warp : autoPlaceWarps) {
            while (autoSlot < size && usedSlots.contains(autoSlot)) {
                autoSlot++;
            }
            if (autoSlot >= size) break;
            inv.setItem(autoSlot, buildWarpItem(warp));
            slotMap.put(autoSlot, warp.getName());
            usedSlots.add(autoSlot);
            autoSlot++;
        }

        openInventories.put(player.getUniqueId(), slotMap);
        player.openInventory(inv);
    }

    /**
     * Returns the warp name at the clicked slot for this player, or null.
     */
    public static String getWarpAtSlot(UUID playerUUID, int slot) {
        Map<Integer, String> map = openInventories.get(playerUUID);
        return map != null ? map.get(slot) : null;
    }

    /**
     * Cleans up tracking data when a player closes the inventory.
     */
    public static void clearPlayer(UUID playerUUID) {
        openInventories.remove(playerUUID);
    }

    // ── Item builders ────────────────────────────────────────────────────────

    private ItemStack buildWarpItem(WarpData warp) {
        ItemStack item = new ItemStack(warp.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Display name
        String displayName = LangManager.color(warp.getDisplayName()
                .replace("{warp}", warp.getName())
                .replace("{world}", warp.getWorld())
                .replace("{x}", String.valueOf(warp.getBlockX()))
                .replace("{y}", String.valueOf(warp.getBlockY()))
                .replace("{z}", String.valueOf(warp.getBlockZ())));
        meta.setDisplayName(displayName);

        // Lore
        List<String> coloredLore = new ArrayList<>();
        for (String line : warp.getLore()) {
            coloredLore.add(LangManager.color(line
                    .replace("{warp}", warp.getName())
                    .replace("{world}", warp.getWorld())
                    .replace("{x}", String.valueOf(warp.getBlockX()))
                    .replace("{y}", String.valueOf(warp.getBlockY()))
                    .replace("{z}", String.valueOf(warp.getBlockZ()))));
        }
        meta.setLore(coloredLore);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildFiller() {
        String matName = plugin.getConfig().getString("gui.fill-material", "BLACK_STAINED_GLASS_PANE");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.BLACK_STAINED_GLASS_PANE;

        ItemStack filler = new ItemStack(mat);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            String name = plugin.getConfig().getString("gui.fill-name", " ");
            meta.setDisplayName(LangManager.color(name));
            meta.setLore(Collections.emptyList());
            filler.setItemMeta(meta);
        }
        return filler;
    }

    // ── Border helpers ────────────────────────────────────────────────────────

    private void fillBorder(Inventory inv, ItemStack filler, int rows) {
        int size = rows * 9;
        for (int i = 0; i < 9; i++) inv.setItem(i, filler);                      // Top row
        for (int i = size - 9; i < size; i++) inv.setItem(i, filler);             // Bottom row
        for (int row = 1; row < rows - 1; row++) {
            inv.setItem(row * 9, filler);           // Left column
            inv.setItem(row * 9 + 8, filler);       // Right column
        }
    }

    private void addBorderSlots(Set<Integer> used, int rows) {
        int size = rows * 9;
        for (int i = 0; i < 9; i++) used.add(i);
        for (int i = size - 9; i < size; i++) used.add(i);
        for (int row = 1; row < rows - 1; row++) {
            used.add(row * 9);
            used.add(row * 9 + 8);
        }
    }
}
