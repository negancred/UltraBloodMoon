package me.negan.bloodMoon.utils;

import me.negan.bloodMoon.manager.ChanceManager;
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
    private final ChanceManager chanceManager;
    private final Random random = new Random();

    private boolean bloodMoonActive = false;
    public boolean forceBloodMoon = false;

    public NightSwitchUtil(JavaPlugin plugin, DataManager dataManager, MoonManager moonManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.moonManager = moonManager;
        this.chanceManager = new ChanceManager(plugin);
    }

    public void handleNightStart() {
        dataManager.addNight();

        int nights = dataManager.getNights();
        double chance = chanceManager.computeBloodMoonChance(nights);

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

        Bukkit.broadcastMessage("§7The night is calm...");
        plugin.getLogger().info("Blood Moon ended!");
    }

    private void startBloodMoon(boolean forced, double chance, double roll) {
        bloodMoonActive = true;
        forceBloodMoon = false;

        moonManager.startNight();

        //setThunderOnBloodMoon(10200);
        dataManager.resetNights();

        if (forced) {
            plugin.getLogger().info("Forced Blood Moon started!");
        } else {
            plugin.getLogger().info("Blood Moon started! Chance: " + chance + " || Roll: " + roll);
        }
    }

    public boolean isBloodMoonActive() {
        return bloodMoonActive;
    }

    private void setThunderOnBloodMoon(int WeatherDuration){
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            World world = Bukkit.getWorlds().get(0);

            world.setStorm(true);
            world.setThundering(true);
            world.setWeatherDuration(WeatherDuration);
        }, 20L);
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
        return chanceManager.computeBloodMoonChance(getNightsSinceLastBloodMoon());
    }
}