package me.negan.bloodMoon.variants;

import me.negan.bloodMoon.utils.NightSwitchUtil;
import me.negan.bloodMoon.utils.SpawnUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.NamespacedKey;
import me.negan.bloodMoon.utils.AggroUtil;

import java.util.Random;

public class VariantManager {

    private final JavaPlugin plugin;
    private final NightSwitchUtil nightSwitch;
    private final Random random = new Random();

    private final NamespacedKey bloodMoonKey;
    private final int MAX_MOBS = 70;

    public VariantManager(JavaPlugin plugin, NightSwitchUtil nightSwitch) {
        this.plugin = plugin;
        this.nightSwitch = nightSwitch;

        this.bloodMoonKey = new NamespacedKey(plugin, "bloodmoon_mob");
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            if (!nightSwitch.isBloodMoonActive()) return;

            var moon = nightSwitch.getMoonManager().getCurrentMoon();
            if (moon == null) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                spawnHostileNearPlayer(player);
            }
            System.out.println("Interval right now is: " + getCurrentInterval());

        }, 0L, getCurrentInterval());
    }

    private void spawnHostileNearPlayer(Player player) {

        World world = player.getWorld();

        for (int i = 0; i < 6; i++) {

            if (getBloodMoonMobCount() >= MAX_MOBS) {
                Bukkit.getLogger().info("[Spawn] Cap reached (70). Skipping spawn.");
                return;
            }

            int dist = 40 + random.nextInt(11);
            double angle = random.nextDouble() * Math.PI * 2;

            int x = player.getLocation().getBlockX() + (int) (Math.cos(angle) * dist);
            int z = player.getLocation().getBlockZ() + (int) (Math.sin(angle) * dist);

            int y;
            try {
                y = world.getHighestBlockYAt(x, z) + 1;
            } catch (Exception e) {
                Bukkit.getLogger().info("Attempt " + i + ": height error " + e.getMessage());
                continue;
            }

            Location spawnLoc = new Location(world, x, y, z);

            if (!SpawnUtil.isSpawnable(spawnLoc, i, plugin)) continue;


            var moon = nightSwitch.getMoonManager().getCurrentMoon();
            if (moon == null) return;

            LivingEntity entity = moon.spawnMob(world, spawnLoc);
            if (entity == null) return;

            entity.getPersistentDataContainer().set(
                    bloodMoonKey,
                    PersistentDataType.BYTE,
                    (byte) 1
            );

            AggroUtil.targetNearestPlayer(entity, plugin, 100);

            debug(spawnLoc, entity.getType().name());

            return;
        }

        Bukkit.getLogger().info("[Spawn] Failed all attempts for " + player.getName());
    }

    private int getBloodMoonMobCount() {
        int count = 0;

        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {

                if (entity.getPersistentDataContainer().has(bloodMoonKey, PersistentDataType.BYTE)) {
                    count++;
                }
            }
        }

        return count;
    }

    private final boolean debug = true;

    private void debug(Location loc, String type) {
        if (!debug) return;

        Bukkit.getLogger().info(
                "[DefaultBloodMoon] " + type + " at " +
                        loc.getBlockX() + " " +
                        loc.getBlockY() + " " +
                        loc.getBlockZ()
        );
    }
    private long getCurrentInterval() {
        var moon = nightSwitch.getMoonManager().getCurrentMoon();
        if (moon == null) return 100L;

        return moon.getSpawnInterval();
    }
}