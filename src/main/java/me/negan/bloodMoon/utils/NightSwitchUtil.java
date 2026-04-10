package me.negan.bloodMoon.utils;

import me.negan.bloodMoon.manager.DataManager;
import me.negan.bloodMoon.moons.MoonManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class NightSwitchUtil {

    private final JavaPlugin plugin;
    private final DataManager dataManager;
    private final MoonManager moonManager;
    private final Random random = new Random();

    private boolean bloodMoonActive = false;
    public boolean forceBloodMoon = false;

    public NightSwitchUtil(JavaPlugin plugin, DataManager dataManager, MoonManager moonManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.moonManager = moonManager;
    }

    public void handleNightStart() {
        dataManager.addNight();

        int nights = dataManager.getNights();
        double chance = computeBloodMoonChance(nights);

        if (forceBloodMoon) {
            startBloodMoon(true, chance, -1);
            return;
        }

        double roll = random.nextDouble();

        if (roll <= chance) {
            startBloodMoon(false, chance, roll);
        } else {
            plugin.getLogger().info("No Blood Moon tonight. Chance: " + chance + " || Roll: " + roll);
        }
    }

    public void handleDayStart() {
        if (!bloodMoonActive) return;

        bloodMoonActive = false;

        moonManager.endNight();

        World world = Bukkit.getWorlds().get(0);

        world.setStorm(false);
        world.setThundering(false);
        world.setClearWeatherDuration(10000);

        Bukkit.broadcastMessage("§7The night is calm...");
        plugin.getLogger().info("Blood Moon ended!");
    }

    private void startBloodMoon(boolean forced, double chance, double roll) {
        bloodMoonActive = true;
        forceBloodMoon = false;

        moonManager.startNight();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            World world = Bukkit.getWorlds().get(0);

            world.setStorm(true);
            world.setThundering(true);
            world.setWeatherDuration(10200);
        }, 20L);

        dataManager.resetNights();


        if (forced) {
            plugin.getLogger().info("Forced Blood Moon started!");
        } else {
            plugin.getLogger().info("Blood Moon started! Chance: " + chance + " || Roll: " + roll);
        }
    }

    public double computeBloodMoonChance(int n) {

        int startNight = plugin.getConfig().getInt("pity_system.start_night");
        int linearEnd = plugin.getConfig().getInt("pity_system.linear_end");
        int maxNight = plugin.getConfig().getInt("pity_system.maximum_nights");

        double startChance = plugin.getConfig().getDouble("pity_system.start_percentage");
        double endChance = plugin.getConfig().getDouble("pity_system.linear_end_percentage");

        if (n < startNight) return 0.0;

        if (n <= linearEnd) {
            double t = (double) (n - startNight) / (linearEnd - startNight);
            return startChance + t * (endChance - startChance);
        }

        int steps = maxNight - linearEnd;
        double r = Math.pow(1.0 / endChance, 1.0 / steps);

        return Math.min(1.0, endChance * Math.pow(r, n - linearEnd));
    }

    public boolean isBloodMoonActive() {
        return bloodMoonActive;
    }

    public MoonManager getMoonManager() {
        return moonManager;
    }
    public void setForceBloodMoon(boolean value) {
        this.forceBloodMoon = value;
    }
    public int getNightsSinceLastBloodMoon() {
        return dataManager.getNights();
    }
    public double getCurrentChance() {
        return computeBloodMoonChance(getNightsSinceLastBloodMoon());
    }
}