package me.negan.bloodMoon.moons;

import me.negan.bloodMoon.BloodMoon;
import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.manager.RewardManager;
import me.negan.bloodMoon.moons.types.ArcaneMoon;
import me.negan.bloodMoon.moons.types.DefaultBloodMoon;
import me.negan.bloodMoon.moons.types.HallowedMoon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.Random;

public class MoonManager {
    private final BossbarManager bossBarManager;
    private RewardManager rewardManager;

    private Moon currentMoon;
    private final Random random = new Random();

    private final BloodMoon plugin;

    public MoonManager(BloodMoon plugin,
                       BossbarManager bossBarManager,
                       RewardManager rewardManager) {

        this.plugin = plugin;
        this.bossBarManager = bossBarManager;
        this.rewardManager = rewardManager;
    }
    public void setRewardManager(RewardManager rewardManager) {
        this.rewardManager = rewardManager;
    }

    public void startNight() {
        System.out.println("Blood Moon STARTED");
        if (currentMoon == null) {
            currentMoon = pickMoon();
        }

        if (currentMoon != null) {
            currentMoon.onNightStart();
        }
    }

    public void tickNight() {
        if (currentMoon != null) {
            currentMoon.onNightTick();
        }
    }

    public void endNight() {
        if (currentMoon != null) {
            currentMoon.onNightEnd();

            rewardManager.rewardPlayers();
            rewardManager.resetScores();

            currentMoon = null;
        }
    }

    public void handleMobSpawn(LivingEntity entity) {
        if (currentMoon != null) {
            currentMoon.onMobSpawn(entity);
        }
    }

    private Moon pickMoon() {
        int roll = random.nextInt(100) + 1;

        if (roll <= 80) {
            return new DefaultBloodMoon(plugin, bossBarManager, rewardManager);
        } else if (roll <= 95) {
            return new HallowedMoon(plugin, bossBarManager, rewardManager);
        } else {
            return new ArcaneMoon(plugin, bossBarManager, rewardManager);
        }
    }

    public Moon getCurrentMoon() {
        return currentMoon;
    }
    public void forceMoon(Moon moon) {
        if (currentMoon != null) {
            currentMoon.onNightEnd();
        }

        currentMoon = moon;
        //currentMoon.onNightStart();
    }
    public boolean isBloodMoonActive() {
        return currentMoon != null;
    }
}