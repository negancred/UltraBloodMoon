package me.negan.bloodMoon.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class DataManager {

    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration config;

    public DataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        createFile();
    }

    private void createFile() {
        file = new File(plugin.getDataFolder(), "data.yml");

        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNights() {
        return config.getInt("nights_since_last_bloodmoon", 0);
    }

    public void setNights(int value) {
        config.set("nights_since_last_bloodmoon", value);
        save();
    }

    public void addNight() {
        int current = getNights();
        setNights(current + 1);
    }

    public void resetNights() {
        setNights(0);
    }
}