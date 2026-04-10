package me.negan.bloodMoon.moons.types;

import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.moons.Moon;
import me.negan.bloodMoon.utils.BroadcastUtil;
import me.negan.bloodMoon.variants.variant.SpookyArcher;
import me.negan.bloodMoon.variants.variant.SpookySkeleton;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class HallowedMoon extends Moon {

    private final JavaPlugin plugin;
    private final BossbarManager bossBarManager;

    public HallowedMoon(JavaPlugin plugin, BossbarManager bossBarManager) {
        this.plugin = plugin;
        this.bossBarManager = bossBarManager;
    }

    @Override
    public String getName() {
        return "Hallowed Moon";
    }

    @Override
    public void onNightStart() {

        String[] messages = {
                "§6A strange calm fills the air...",
                "§6The Hallowed Moon watches silently...",
                "§6You hear a faint laugh coming from above...",
                "§6Something feels... different tonight."
        };

        BroadcastUtil.broadcastRandom(messages);
        bossBarManager.start(BarColor.YELLOW, "§6Hallowed Moon");
    }

    @Override
    public void onNightTick() {}

    @Override
    public void onNightEnd() {
        bossBarManager.rewardPlayers();
        bossBarManager.stop();

        List<NamespacedKey> keys = List.of(
                new NamespacedKey(plugin, "spooky_skeleton"),
                new NamespacedKey(plugin, "spooky_archer")
        );

        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {

                for (NamespacedKey key : keys) {
                    if (entity.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {

                        world.spawnParticle(Particle.SOUL, entity.getLocation(), 20);
                        entity.remove();
                        break;
                    }
                }
            }
        }


    }

    @Override
    public void onMobSpawn(LivingEntity entity) {}


    @Override
    public LivingEntity spawnMob(World world, Location loc) {

        Skeleton skeleton = world.spawn(loc, Skeleton.class);

        int roll = new java.util.Random().nextInt(100);

        if (roll < 10) {
            SpookyArcher.apply(skeleton, plugin);
        } else {
            SpookySkeleton.apply(skeleton, plugin);
        }

        return skeleton;
    }

}