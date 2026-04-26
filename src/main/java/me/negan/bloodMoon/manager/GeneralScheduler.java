package me.negan.bloodMoon.manager;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class GeneralScheduler {

    private final JavaPlugin plugin;
    private final NamespacedKey faceZombieKey;

    public GeneralScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.faceZombieKey = new NamespacedKey(plugin, "face_zombie");
    }

    public void start() {

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            for (World world : Bukkit.getWorlds()) {

                if (world.getEnvironment() != World.Environment.NORMAL) continue;

                long time = world.getTime();
                boolean isDay = time >= 0 && time < 12300;

                if (!isDay) continue;

                handleFaceZombieSunlight(world);
            }

        }, 0L, 100L);
    }

    private void handleFaceZombieSunlight(World world) {

        for (LivingEntity entity : world.getLivingEntities()) {

            if (!(entity instanceof Zombie zombie)) continue;

            if (!zombie.getPersistentDataContainer().has(faceZombieKey, PersistentDataType.BYTE)) continue;

            if (zombie.getEquipment() == null) continue;

            if (zombie.getEquipment().getHelmet() != null) {
                zombie.getEquipment().setHelmet(null);
            }
        }
    }
}