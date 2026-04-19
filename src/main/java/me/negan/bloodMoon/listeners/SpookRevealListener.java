package me.negan.bloodMoon.listeners;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpookRevealListener implements Listener {

    private final NamespacedKey spookKey;
    private final JavaPlugin plugin;

    private final Map<UUID, Long> lastSoundTime = new HashMap<>();

    public SpookRevealListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.spookKey = new NamespacedKey(plugin, "spook");

        startTask();
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {

                for (World world : plugin.getServer().getWorlds()) {
                    for (Entity entity : world.getEntities()) {

                        if (!(entity instanceof Skeleton skeleton)) continue;

                        if (!skeleton.getPersistentDataContainer().has(spookKey, PersistentDataType.BYTE)) {
                            continue;
                        }

                        boolean nearBlock = isNearSoulLight(skeleton.getLocation(), 13);
                        boolean nearPlayerSoul = isNearPlayerWithSoul(skeleton, 6);

                        boolean reveal = nearBlock || nearPlayerSoul;

                        if (reveal) {
                            skeleton.removePotionEffect(PotionEffectType.INVISIBILITY);
                        } else {
                            if (!skeleton.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                skeleton.addPotionEffect(new PotionEffect(
                                        PotionEffectType.INVISIBILITY,
                                        60,
                                        0,
                                        false,
                                        false
                                ));
                            }
                        }

                        Player nearest = getNearestPlayer(skeleton, 12);
                        if (nearest == null) continue;

                        long now = System.currentTimeMillis();
                        long last = lastSoundTime.getOrDefault(skeleton.getUniqueId(), 0L);

                        long cooldown = 2000 + (long)(Math.random() * 2000);

                        if (now - last >= cooldown) {

                            skeleton.getWorld().playSound(
                                    skeleton.getLocation(),
                                    Sound.ENTITY_SKELETON_AMBIENT,
                                    1.0f,
                                    0.8f + (float)Math.random() * 0.4f
                            );

                            lastSoundTime.put(skeleton.getUniqueId(), now);
                        }
                    }
                }

            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private Player getNearestPlayer(Skeleton skeleton, double range) {
        Player nearest = null;
        double closest = range;

        for (Player player : skeleton.getWorld().getPlayers()) {
            double dist = player.getLocation().distance(skeleton.getLocation());

            if (dist < closest) {
                closest = dist;
                nearest = player;
            }
        }

        return nearest;
    }

    private boolean isNearSoulLight(Location loc, int radius) {
        World world = loc.getWorld();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    Location check = loc.clone().add(x, y, z);
                    Material type = world.getBlockAt(check).getType();

                    if (type == Material.SOUL_TORCH ||
                            type == Material.SOUL_LANTERN ||
                            type == Material.SOUL_CAMPFIRE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean isNearPlayerWithSoul(Skeleton skeleton, double radius) {

        for (Player player : skeleton.getWorld().getPlayers()) {

            if (player.getLocation().distance(skeleton.getLocation()) <= radius) {
                if (isPlayerHoldingSoul(player)) {
                    return true;
                }
            }
        }

        return false;
    }
    private boolean isPlayerHoldingSoul(Player player) {

        Material main = player.getInventory().getItemInMainHand().getType();
        Material off = player.getInventory().getItemInOffHand().getType();

        return isSoulMaterial(main) || isSoulMaterial(off);
    }

    private boolean isSoulMaterial(Material mat) {
        return mat == Material.SOUL_TORCH ||
                mat == Material.SOUL_LANTERN ||
                mat == Material.SOUL_CAMPFIRE;
    }
}