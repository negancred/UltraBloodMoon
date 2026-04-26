package me.negan.bloodMoon.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleUtil {

    public static void playRisingParticles(
            JavaPlugin plugin,
            World world,
            Location loc,
            Particle particle,
            int durationTicks,
            int countPerTick,
            double spreadX,
            double spreadY,
            double spreadZ,
            double speed,
            double risePerTick
    ) {

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (ticks > durationTicks) {
                    cancel();
                    return;
                }

                world.spawnParticle(
                        particle,
                        loc.clone().add(0, ticks * risePerTick, 0),
                        countPerTick,
                        spreadX, spreadY, spreadZ,
                        speed
                );

                ticks++;
            }

        }.runTaskTimer(plugin, 0L, 1L);
    }

}