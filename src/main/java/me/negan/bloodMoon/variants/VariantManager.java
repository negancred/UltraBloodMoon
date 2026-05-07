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
        runLoop();

    }
    private void runLoop() {
        var moon = nightSwitch.getMoonManager().getCurrentMoon();
        long delay = (moon != null) ? moon.getSpawnInterval() : 100L;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            if (nightSwitch.isBloodMoonActive() && moon != null) {



                for (Player player : Bukkit.getOnlinePlayers()) {
                    spawnHostileNearPlayer(player);
                }
            }

            runLoop();

        }, delay);
    }

    private void spawnHostileNearPlayer(Player player) {
        boolean isLogTrue = plugin.getConfig().getBoolean("general.console_spawn_messages", false);

        if (!isValidWorld(player)) return;

        for (int i = 0; i < 6; i++) {

            if (isMobCapReached()) return;

            Location spawnLoc = findSpawnLocation(player);
            if (spawnLoc == null) continue;

            if (!SpawnUtil.isSpawnable(spawnLoc, i, plugin)) continue;

            LivingEntity entity = spawnMoonMob(spawnLoc);
            if (entity == null) return;

            tagBloodMoonMob(entity);
            handleAggro(entity);


            if (isLogTrue) {
                logSpawn(player, entity, spawnLoc);
            }

            return;
        }
        if (isLogTrue) {
            logFailedSpawn(player);
        }
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
                "[UltraBloodMoon] " + type + " at " +
                        loc.getBlockX() + " " +
                        loc.getBlockY() + " " +
                        loc.getBlockZ()
        );
    }

    private boolean isValidWorld(Player player) {
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            Bukkit.getLogger().info("[UltraBloodMoon] World is not overworld. Skipping spawn.");
            return false;
        }
        return true;
    }

    private boolean isMobCapReached() {
        if (getBloodMoonMobCount() >= MAX_MOBS) {
            Bukkit.getLogger().info("[UltraBloodMoon] Cap reached (70). Skipping spawn.");
            return true;
        }
        return false;
    }

    private Location findSpawnLocation(Player player) {

        World world = player.getWorld();

        int dist = 35 + random.nextInt(41);
        double angle = random.nextDouble() * Math.PI * 2;

        int x = player.getLocation().getBlockX() + (int) (Math.cos(angle) * dist);
        int z = player.getLocation().getBlockZ() + (int) (Math.sin(angle) * dist);

        try {
            int y = world.getHighestBlockYAt(x, z) + 1;
            return new Location(world, x, y, z);
        } catch (Exception e) {
            return null;
        }
    }

    private LivingEntity spawnMoonMob(Location loc) {

        var moon = nightSwitch.getMoonManager().getCurrentMoon();
        if (moon == null) return null;

        return moon.spawnMob(loc.getWorld(), loc);
    }
    private void tagBloodMoonMob(LivingEntity entity) {
        entity.getPersistentDataContainer().set(
                bloodMoonKey,
                PersistentDataType.BYTE,
                (byte) 1
        );
    }
    private void handleAggro(LivingEntity entity) {

        double aggroChance = plugin.getConfig().getDouble("general.aggro-chance", 0.7);

        if (random.nextDouble() < aggroChance) {
            AggroUtil.targetNearestPlayer(entity, plugin, 100);
        }
    }
    private void logSpawn(Player player, LivingEntity entity, Location spawnLoc) {

        double distance = player.getLocation().distance(spawnLoc);

        Bukkit.getLogger().info(
                "[UltraBloodMoon] Spawned " + entity.getType().name() + " " +
                        String.format("%.2f", distance) +
                        " blocks away from " + player.getName()
        );
    }
    private void logFailedSpawn(Player player) {
        Bukkit.getLogger().info("[UltraBloodMoon] Failed all attempts for " + player.getName());
    }
}