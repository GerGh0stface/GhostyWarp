package commands;
import ghostywarp.GhostyWarp;


import manager.LangManager;
import model.WarpData;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * /gw <list|lore|slot|item|name|reload> - Admin sub-commands.
 */
public class GWCommand implements CommandExecutor, TabCompleter {

    private final GhostyWarp plugin;

    public GWCommand(GhostyWarp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("ghostywarp.admin")) {
            sender.sendMessage(plugin.getLangManager().get("messages.no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {

            // ── /gw list ────────────────────────────────────────────────────
            case "list" -> {
                Collection<WarpData> warps = plugin.getWarpManager().getAllWarps();
                sender.sendMessage(LangManager.color(plugin.getLangManager().getRaw("messages.gw-list-header")));
                if (warps.isEmpty()) {
                    sender.sendMessage(LangManager.color(plugin.getLangManager().getRaw("messages.gw-list-empty")));
                } else {
                    for (WarpData w : warps) {
                        sender.sendMessage(plugin.getLangManager().getRaw("messages.gw-list-entry",
                                "{warp}", w.getName(),
                                "{world}", w.getWorld(),
                                "{x}", String.valueOf(w.getBlockX()),
                                "{y}", String.valueOf(w.getBlockY()),
                                "{z}", String.valueOf(w.getBlockZ())));
                    }
                }
                sender.sendMessage(LangManager.color(plugin.getLangManager().getRaw("messages.gw-list-footer")));
            }

            // ── /gw lore <warp> <line1|line2|...> ──────────────────────────
            case "lore" -> {
                if (args.length < 3) {
                    sender.sendMessage(plugin.getLangManager().get("messages.gw-lore-usage"));
                    return true;
                }
                String warpName = args[1];
                WarpData warp = plugin.getWarpManager().getWarp(warpName);
                if (warp == null) {
                    sender.sendMessage(plugin.getLangManager().get("messages.warp-not-found", "{warp}", warpName));
                    return true;
                }
                // Join remaining args and split by |
                String loreFull = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                List<String> loreLines = Arrays.asList(loreFull.split("\\|"));
                warp.setLore(loreLines);
                plugin.getWarpManager().save();
                sender.sendMessage(plugin.getLangManager().get("messages.gw-lore-success", "{warp}", warp.getName()));
            }

            // ── /gw slot <warp> <slot> ──────────────────────────────────────
            case "slot" -> {
                if (args.length < 3) {
                    sender.sendMessage(plugin.getLangManager().get("messages.gw-slot-usage"));
                    return true;
                }
                String warpName = args[1];
                WarpData warp = plugin.getWarpManager().getWarp(warpName);
                if (warp == null) {
                    sender.sendMessage(plugin.getLangManager().get("messages.warp-not-found", "{warp}", warpName));
                    return true;
                }
                int maxSlot = (plugin.getConfig().getInt("gui.rows", 6) * 9) - 1;
                int slot;
                try {
                    slot = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getLangManager().get("messages.gw-slot-invalid",
                            "{max}", String.valueOf(maxSlot)));
                    return true;
                }
                if (slot < -1 || slot > maxSlot) {
                    sender.sendMessage(plugin.getLangManager().get("messages.gw-slot-invalid",
                            "{max}", String.valueOf(maxSlot)));
                    return true;
                }
                warp.setSlot(slot);
                plugin.getWarpManager().save();
                sender.sendMessage(plugin.getLangManager().get("messages.gw-slot-success",
                        "{warp}", warp.getName(), "{slot}", String.valueOf(slot)));
            }

            // ── /gw item <warp> <material> ──────────────────────────────────
            case "item" -> {
                if (args.length < 3) {
                    sender.sendMessage(plugin.getLangManager().get("messages.gw-item-usage"));
                    return true;
                }
                String warpName = args[1];
                WarpData warp = plugin.getWarpManager().getWarp(warpName);
                if (warp == null) {
                    sender.sendMessage(plugin.getLangManager().get("messages.warp-not-found", "{warp}", warpName));
                    return true;
                }
                Material mat = Material.matchMaterial(args[2].toUpperCase());
                if (mat == null || mat.isAir()) {
                    sender.sendMessage(plugin.getLangManager().get("messages.gw-item-invalid",
                            "{material}", args[2]));
                    return true;
                }
                warp.setMaterial(mat);
                plugin.getWarpManager().save();
                sender.sendMessage(plugin.getLangManager().get("messages.gw-item-success",
                        "{warp}", warp.getName(), "{material}", mat.name()));
            }

            // ── /gw name <warp> <display name...> ──────────────────────────
            case "name" -> {
                if (args.length < 3) {
                    sender.sendMessage(plugin.getLangManager().get("messages.gw-name-usage"));
                    return true;
                }
                String warpName = args[1];
                WarpData warp = plugin.getWarpManager().getWarp(warpName);
                if (warp == null) {
                    sender.sendMessage(plugin.getLangManager().get("messages.warp-not-found", "{warp}", warpName));
                    return true;
                }
                String newName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                warp.setDisplayName(newName);
                plugin.getWarpManager().save();
                sender.sendMessage(plugin.getLangManager().get("messages.gw-name-success",
                        "{warp}", warp.getName()));
            }

            // ── /gw reload ──────────────────────────────────────────────────
            case "reload" -> {
                plugin.reloadPlugin();
                sender.sendMessage(plugin.getLangManager().get("messages.reload"));
            }

            default -> sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        LangManager lm = plugin.getLangManager();
        sender.sendMessage(lm.getRaw("messages.gw-help-header"));
        sender.sendMessage(lm.getRaw("messages.gw-help-list"));
        sender.sendMessage(lm.getRaw("messages.gw-help-lore"));
        sender.sendMessage(lm.getRaw("messages.gw-help-slot"));
        sender.sendMessage(lm.getRaw("messages.gw-help-item"));
        sender.sendMessage(lm.getRaw("messages.gw-help-name"));
        sender.sendMessage(lm.getRaw("messages.gw-help-reload"));
        sender.sendMessage(lm.getRaw("messages.gw-help-footer"));
    }

    // ── Tab Completion ────────────────────────────────────────────────────────

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!sender.hasPermission("ghostywarp.admin")) return Collections.emptyList();

        if (args.length == 1) {
            return filterSuggestions(List.of("list", "lore", "slot", "item", "name", "reload"), args[0]);
        }

        if (args.length == 2 && !args[0].equalsIgnoreCase("list") && !args[0].equalsIgnoreCase("reload")) {
            List<String> warpNames = plugin.getWarpManager().getAllWarps().stream()
                    .map(WarpData::getName).collect(Collectors.toList());
            return filterSuggestions(warpNames, args[1]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("item")) {
            return filterSuggestions(Arrays.stream(Material.values())
                    .filter(m -> !m.isAir() && m.isItem())
                    .map(Material::name)
                    .collect(Collectors.toList()), args[2]);
        }

        return Collections.emptyList();
    }

    private List<String> filterSuggestions(List<String> options, String input) {
        String lower = input.toLowerCase();
        return options.stream()
                .filter(s -> s.toLowerCase().startsWith(lower))
                .collect(Collectors.toList());
    }
}
