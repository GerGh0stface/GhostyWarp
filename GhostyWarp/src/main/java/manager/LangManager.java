package manager;
import ghostywarp.GhostyWarp;


import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Manages loading and accessing language messages.
 */
public class LangManager {

    private final GhostyWarp plugin;
    private FileConfiguration lang;
    private String prefix;

    public LangManager(GhostyWarp plugin) {
        this.plugin = plugin;
        reload();
    }

    /**
     * Loads (or reloads) the active language file.
     */
    public void reload() {
        String langCode = plugin.getConfig().getString("language", "en");
        File langFolder = new File(plugin.getDataFolder(), "lang");

        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        // Save bundled language files if they don't exist yet
        saveDefaultLang("en");
        saveDefaultLang("de");

        File langFile = new File(langFolder, langCode + ".yml");

        if (!langFile.exists()) {
            plugin.getLogger().warning("Language file '" + langCode + ".yml' not found! Falling back to 'en.yml'.");
            langFile = new File(langFolder, "en.yml");
        }

        if (!langFile.exists()) {
            plugin.getLogger().severe("No language file found! Please check your installation.");
            lang = new YamlConfiguration();
            prefix = "&8[&bGhostyWarp&8] &r";
            return;
        }

        lang = YamlConfiguration.loadConfiguration(langFile);

        // Merge defaults from jar
        InputStream defaultStream = plugin.getResource("lang/" + langCode + ".yml");
        if (defaultStream == null) {
            defaultStream = plugin.getResource("lang/en.yml");
        }
        if (defaultStream != null) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            lang.setDefaults(defaults);
        }

        prefix = color(lang.getString("prefix", "&8[&bGhostyWarp&8] &r"));
        plugin.getLogger().info("Loaded language: " + langCode);
    }

    /**
     * Saves a bundled language file to disk if not already present.
     */
    private void saveDefaultLang(String code) {
        File file = new File(plugin.getDataFolder(), "lang/" + code + ".yml");
        if (!file.exists()) {
            InputStream stream = plugin.getResource("lang/" + code + ".yml");
            if (stream != null) {
                plugin.saveResource("lang/" + code + ".yml", false);
            }
        }
    }

    /**
     * Gets a translated, colored message by key.
     * Supports placeholders via varargs: "key", value pairs.
     * Example: get("messages.addwarp-success", "{warp}", "Home")
     */
    public String get(String key, String... placeholders) {
        String msg = lang.getString(key, "&cMissing language key: " + key);
        msg = color(msg);

        if (placeholders != null && placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {
                msg = msg.replace(placeholders[i], placeholders[i + 1]);
            }
        }

        return prefix + msg;
    }

    /**
     * Gets a raw (non-prefixed) colored message.
     */
    public String getRaw(String key, String... placeholders) {
        String msg = lang.getString(key, "&cMissing language key: " + key);
        msg = color(msg);

        if (placeholders != null && placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {
                msg = msg.replace(placeholders[i], placeholders[i + 1]);
            }
        }

        return msg;
    }

    /**
     * Translates & color codes.
     */
    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getPrefix() {
        return prefix;
    }
}
