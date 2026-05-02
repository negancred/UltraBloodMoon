package me.negan.bloodMoon.manager;

import me.negan.bloodMoon.moons.MoonManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreScheduler {

    private final JavaPlugin plugin;
    private final RewardManager rewardManager;
    private final BossbarManager bossbarManager;
    private final MoonManager moonManager;

    public ScoreScheduler(JavaPlugin plugin,
                          RewardManager rewardManager,
                          BossbarManager bossbarManager,
                          MoonManager moonManager) {

        this.plugin = plugin;
        this.rewardManager = rewardManager;
        this.bossbarManager = bossbarManager;
        this.moonManager = moonManager;
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            if (!moonManager.isBloodMoonActive()) return;

            rewardManager.tickInactivity(bossbarManager);

        }, 20L, 20L);
    }
}