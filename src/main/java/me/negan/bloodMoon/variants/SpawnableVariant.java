package me.negan.bloodMoon.variants;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public interface SpawnableVariant {

    String getName();

    void spawn(Location loc, JavaPlugin plugin);
}