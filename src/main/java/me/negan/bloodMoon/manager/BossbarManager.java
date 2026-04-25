package me.negan.bloodMoon.manager;

import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossbarManager {

    private final JavaPlugin plugin;
    private final RewardManager rewardManager;

    private final Map<UUID, BossBar> bossBars = new HashMap<>();

    private final int MAX_SCORE = 1000;
    private String currentTitle;
    private BarColor currentColor;

    public BossbarManager(JavaPlugin plugin, RewardManager rewardManager) {
        this.plugin = plugin;
        this.rewardManager = rewardManager;
    }

    public void start(BarColor color, String title) {

        this.currentTitle = title;
        this.currentColor = color;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isOverworld(player)) continue;

            BossBar bar = Bukkit.createBossBar(title, color, BarStyle.SEGMENTED_10);
            bar.setProgress(0.0);

            bar.addPlayer(player);

            bossBars.put(player.getUniqueId(), bar);

            updateBossBar(player);
        }
    }

    public void stop() {
        for (BossBar bar : bossBars.values()) {
            bar.removeAll();
        }

        bossBars.clear();
    }

    private boolean isOverworld(Player player) {
        return player.getWorld().getEnvironment() == World.Environment.NORMAL;
    }

    public void updateBossBar(Player player) {
        if (currentTitle == null || currentColor == null) return;
        if (!isOverworld(player)) return;


        BossBar bar = bossBars.get(player.getUniqueId());

        if (bar == null) {
            bar = Bukkit.createBossBar(currentTitle, currentColor, BarStyle.SEGMENTED_10);
            bar.addPlayer(player);
            bossBars.put(player.getUniqueId(), bar);
        }

        int score = rewardManager.getScore(player);
        double progress = Math.min(1.0, score / (double) MAX_SCORE);

        bar.setProgress(progress);
        bar.setTitle(currentTitle + " §7Score: " + score + "/" + MAX_SCORE);
    }

    public void handleJoin(Player player) {
        updateBossBar(player);
    }

    public void handleQuit(Player player) {
        BossBar bar = bossBars.remove(player.getUniqueId());
        if (bar != null) {
            bar.removeAll();
        }
    }

    public void handleWorldChange(Player player) {
        if (!isOverworld(player)) {
            handleQuit(player);
            return;
        }

        updateBossBar(player);
    }
}