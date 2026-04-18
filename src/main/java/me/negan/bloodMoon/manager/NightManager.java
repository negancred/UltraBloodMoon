package me.negan.bloodMoon.manager;

import me.negan.bloodMoon.moons.MoonManager;
import me.negan.bloodMoon.utils.NightSwitchUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class NightManager {

    private final JavaPlugin plugin;
    private final NightSwitchUtil nightSwitch;
    private final MoonManager moonManager;

    private boolean countedThisNight = false;
    private boolean countedThisDay = false;

    public NightManager(JavaPlugin plugin, NightSwitchUtil nightSwitch, MoonManager moonManager) {
        this.plugin = plugin;
        this.nightSwitch = nightSwitch;
        this.moonManager = moonManager;
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            World world = Bukkit.getWorlds().get(0);
            long time = world.getTime();

            if (time >= 13000) {

                countedThisDay = false;

                if (!countedThisNight) {
                    countedThisNight = true;
                    nightSwitch.handleNightStart();
                }

                if (nightSwitch.isBloodMoonActive()) {
                    moonManager.tickNight();
                }

            }
            else {

                countedThisNight = false;

                if (!countedThisDay) {
                    countedThisDay = true;
                    nightSwitch.handleDayStart();
                }
            }

        }, 0L, 20L);
    }
}