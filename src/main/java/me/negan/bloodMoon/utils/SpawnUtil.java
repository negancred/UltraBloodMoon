package me.negan.bloodMoon.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnUtil {

    public static boolean isSpawnable(Location loc, int attempt, JavaPlugin plugin) {

        if (loc == null) {
            log(attempt, "spawn position is NULL");
            return false;
        }

        World world = loc.getWorld();

        Location groundLoc = loc.clone().subtract(0, 1, 0);
        Block ground = groundLoc.getBlock();

        if (!ground.getType().isSolid()) {
            log(attempt, "no solid ground at " + format(loc));
            return false;
        }
        if (!loc.getBlock().isEmpty() || !loc.clone().add(0, 1, 0).getBlock().isEmpty()) {
            log(attempt, "not enough space at " + format(loc));
            return false;
        }
        if (!world.isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            log(attempt, "chunk not loaded at " + format(loc));
            return false;
        }

        if (loc.getBlock().isLiquid()) {
            log(attempt, "fluid at " + format(loc));
            return false;
        }

        Block below = loc.clone().subtract(0, 1, 0).getBlock();
        if (!below.getType().isSolid()) {
            log(attempt, "no solid block below " + format(loc));
            return false;
        }


        boolean hasSpace =
                loc.getBlock().isEmpty() &&
                        loc.clone().add(0, 1, 0).getBlock().isEmpty();

        if (!hasSpace) {
            log(attempt, "not enough space at " + format(loc));
            return false;
        }

        int light = loc.getBlock().getLightLevel();
        if (light > plugin.getConfig().getInt("general.maximum_lightLevel_spawn")) {
            log(attempt, "light level too high (" + light + ") at " + format(loc));
            return false;
        }

        return true;
    }

    private static void log(int attempt, String msg) {
        Bukkit.getLogger().info("Attempt " + attempt + ": " + msg);
    }

    private static String format(Location loc) {
        return loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
    }
}