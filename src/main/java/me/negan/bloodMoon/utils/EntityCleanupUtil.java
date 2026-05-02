package me.negan.bloodMoon.utils;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class EntityCleanupUtil {

    public static void removeTaggedEntities(JavaPlugin plugin, List<NamespacedKey> keys) {

        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {

                if (entity.isInsideVehicle()) continue;
                if (entity.getCustomName() != null) {
                    continue;
                }

                for (NamespacedKey key : keys) {
                    if (entity.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {

                        ParticleUtil.playRisingParticles(
                                plugin,
                                world,
                                entity.getLocation(),
                                Particle.SOUL,
                                10,
                                6,
                                0.1, 0.05, 0.1,
                                0.01,
                                0.08
                        );

                        entity.remove();
                        break;
                    }
                }
            }
        }
    }
}