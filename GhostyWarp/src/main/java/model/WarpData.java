package model;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single warp entry with all its data.
 */
public class WarpData {

    private final String name;
    private String world;
    private double x, y, z;
    private float yaw, pitch;

    // GUI item customization
    private Material material;
    private String displayName;
    private List<String> lore;
    private int slot; // -1 = auto assign

    public WarpData(String name, String world, double x, double y, double z, float yaw, float pitch,
                    Material material, String displayName, List<String> lore, int slot) {
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
        this.slot = slot;
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public String getName()        { return name; }
    public String getWorld()       { return world; }
    public double getX()           { return x; }
    public double getY()           { return y; }
    public double getZ()           { return z; }
    public float  getYaw()         { return yaw; }
    public float  getPitch()       { return pitch; }
    public Material getMaterial()  { return material; }
    public String getDisplayName() { return displayName; }
    public List<String> getLore()  { return lore; }
    public int getSlot()           { return slot; }

    // ── Setters ─────────────────────────────────────────────────────────────

    public void setWorld(String world)           { this.world = world; }
    public void setX(double x)                  { this.x = x; }
    public void setY(double y)                  { this.y = y; }
    public void setZ(double z)                  { this.z = z; }
    public void setYaw(float yaw)               { this.yaw = yaw; }
    public void setPitch(float pitch)           { this.pitch = pitch; }
    public void setMaterial(Material material)  { this.material = material; }
    public void setDisplayName(String name)     { this.displayName = name; }
    public void setLore(List<String> lore)      { this.lore = lore; }
    public void setSlot(int slot)               { this.slot = slot; }

    // ── Helpers ──────────────────────────────────────────────────────────────

    public int getBlockX() { return (int) Math.floor(x); }
    public int getBlockY() { return (int) Math.floor(y); }
    public int getBlockZ() { return (int) Math.floor(z); }
}
