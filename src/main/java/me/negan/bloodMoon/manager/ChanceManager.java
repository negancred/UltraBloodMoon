package me.negan.bloodMoon.manager;

import org.bukkit.plugin.java.JavaPlugin;

public class ChanceManager {

    private final JavaPlugin plugin;

    public ChanceManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public double computeBloodMoonChance(int n) {

        int startNight = plugin.getConfig().getInt("pity_system.start_night");
        int linearEnd = plugin.getConfig().getInt("pity_system.linear_end");
        int maxNight = plugin.getConfig().getInt("pity_system.maximum_nights");

        double startChance = plugin.getConfig().getDouble("pity_system.start_percentage");
        double endChance = plugin.getConfig().getDouble("pity_system.linear_end_percentage");

        if (n < startNight) return 0.0;

        if (n <= linearEnd) {
            int range = linearEnd - startNight;
            if (range <= 0) return endChance;

            double t = (double) (n - startNight) / range;
            return startChance + t * (endChance - startChance);
        }

        int steps = maxNight - linearEnd;
        if (steps <= 0) return 1.0;

        double r = Math.pow(1.0 / endChance, 1.0 / steps);

        return Math.min(1.0, endChance * Math.pow(r, n - linearEnd));
    }
    public double computeVariantChance(int n) {

        int startNight = plugin.getConfig().getInt("pity_system.start_night");
        int linearEnd = plugin.getConfig().getInt("pity_system.linear_end");
        int maxNight = plugin.getConfig().getInt("pity_system.maximum_nights");

        int variantStart = startNight + (linearEnd - startNight) / 2;

        if (n < variantStart) return 0.0;

        double bloodMoonChance = computeBloodMoonChance(n);

        int range = maxNight - variantStart;
        if (range <= 0) return bloodMoonChance * 0.07;

        double progress = (double) (n - variantStart) / range;
        progress = Math.min(1.0, progress);

        double scaling = 0.20   ;

        return bloodMoonChance * progress * scaling;
    }
}