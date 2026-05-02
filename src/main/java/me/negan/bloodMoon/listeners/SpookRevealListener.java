package me.negan.bloodMoon.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SpookRevealListener implements Listener {

    private final NamespacedKey spookKey;
    private final JavaPlugin plugin;

    private final Map<UUID, Long> lastSoundTime = new HashMap<>();

    private final Set<Location> soulLights = new HashSet<>();

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

                    for (Skeleton skeleton : world.getEntitiesByClass(Skeleton.class)) {

                        if (!skeleton.getPersistentDataContainer().has(spookKey, PersistentDataType.BYTE)) {
                            continue;
                        }

                        int soulRadius = plugin.getConfig().getInt("spook.reveal.soul_light_radius", 13);
                        int playerRadius = plugin.getConfig().getInt("spook.reveal.player_soul_radius", 6);

                        boolean nearBlock = isNearSoulLight(skeleton.getLocation(), soulRadius);
                        boolean nearPlayerSoul = isNearPlayerWithSoul(skeleton, playerRadius);

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

                        handleSound(skeleton);
                    }
                }

            }
        }.runTaskTimer(plugin, 0L, 20L);
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (isSoulMaterial(event.getBlock().getType())) {
            soulLights.add(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        soulLights.remove(event.getBlock().getLocation());
    }

    private boolean isNearSoulLight(Location loc, int radius) {
        double r2 = radius * radius;

        for (Location soul : soulLights) {
            if (!soul.getWorld().equals(loc.getWorld())) continue;

            if (soul.distanceSquared(loc) <= r2) {
                return true;
            }
        }

        return false;
    }


    private boolean isNearPlayerWithSoul(Skeleton skeleton, double radius) {

        for (Player player : skeleton.getWorld().getPlayers()) {

            if (player.getLocation().distanceSquared(skeleton.getLocation()) <= radius * radius) {
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


    private void handleSound(Skeleton skeleton) {
        Player nearest = getNearestPlayer(skeleton, 12);
        if (nearest == null) return;

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

    private Player getNearestPlayer(Skeleton skeleton, double range) {
        Player nearest = null;
        double closest = range * range;

        for (Player player : skeleton.getWorld().getPlayers()) {
            double dist = player.getLocation().distanceSquared(skeleton.getLocation());

            if (dist < closest) {
                closest = dist;
                nearest = player;
            }
        }

        return nearest;
    }


    private boolean isSoulMaterial(Material mat) {
        return mat == Material.SOUL_TORCH ||
                mat == Material.SOUL_WALL_TORCH ||
                mat == Material.SOUL_LANTERN ||
                mat == Material.SOUL_CAMPFIRE;
    }
}