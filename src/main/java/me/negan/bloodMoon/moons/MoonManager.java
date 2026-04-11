package me.negan.bloodMoon.moons;

import me.negan.bloodMoon.BloodMoon;
import me.negan.bloodMoon.moons.types.ArcaneMoon;
import me.negan.bloodMoon.moons.types.DefaultBloodMoon;
import me.negan.bloodMoon.moons.types.HallowedMoon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.Random;

public class MoonManager {

    private Moon currentMoon;
    private final Random random = new Random();

    private final BloodMoon plugin;

    public MoonManager(BloodMoon plugin) {
        this.plugin = plugin;
    }

    public void startNight() {


        currentMoon = pickMoon();

        currentMoon.onNightStart();


    }

    public void tickNight() {
        if (currentMoon != null) {
            currentMoon.onNightTick();
        }
    }

    public void endNight() {
        if (currentMoon != null) {
            currentMoon.onNightEnd();
            currentMoon = null;
        }
    }

    public void handleMobSpawn(LivingEntity entity) {
        if (currentMoon != null) {
            currentMoon.onMobSpawn(entity);
        }
    }

    private Moon pickMoon() {
        int roll = random.nextInt(3);

        return switch (roll) {
            case 0 -> new DefaultBloodMoon(plugin, plugin.getBossBarManager());
            case 1 -> new HallowedMoon(plugin, plugin.getBossBarManager());
            case 2 -> new ArcaneMoon(plugin, plugin.getBossBarManager());
            default -> new DefaultBloodMoon(plugin, plugin.getBossBarManager());
        };
    }

    public Moon getCurrentMoon() {
        return currentMoon;
    }
    public void forceMoon(Moon moon) {
        if (currentMoon != null) {
            currentMoon.onNightEnd();
        }

        currentMoon = moon;
        currentMoon.onNightStart();
    }
}