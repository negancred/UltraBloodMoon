package me.negan.bloodMoon.moons;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

public abstract class Moon {


    public abstract String getName();

    public abstract void onNightStart();
    public abstract void onNightTick();
    public abstract void onNightEnd();

    public abstract void onMobSpawn(LivingEntity entity);


    public abstract LivingEntity spawnMob(World world, Location loc);

    public double getSpawnMultiplier() {
        return 1.0;
    }
    public long getSpawnInterval() {
        return 100L;
    }
}