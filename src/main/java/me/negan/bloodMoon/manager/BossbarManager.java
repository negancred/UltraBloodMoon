package me.negan.bloodMoon.manager;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BossbarManager {

    private final JavaPlugin plugin;
    private BossBar bossBar;

    private final Map<UUID, BossBar> bossBars = new HashMap<>();
    private final Map<UUID, Integer> scores = new HashMap<>();
    private final Map<UUID, Integer> inactivity = new HashMap<>();
    // will make these configurable soon.

    private final int MAX_SCORE = 1000;
    private final int KILL_POINTS = 20;
    private final int DEATH_PENALTY = 60;
    private final int INACTIVITY_PENALTY = 20;
    private String currentTitle;

    public BossbarManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start(BarColor color, String title) {

        this.currentTitle = title;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isOverworld(player)) continue;

            BossBar bar = Bukkit.createBossBar("", color, BarStyle.SEGMENTED_10);

            bar.addPlayer(player);

            bossBars.put(player.getUniqueId(), bar);
            scores.put(player.getUniqueId(), 0);
            inactivity.put(player.getUniqueId(), 0);

            updateBossBar(player);
        }

        startInactivityTask();
    }

    public void stop() {
        for (BossBar bar : bossBars.values()) {
            bar.removeAll();
        }

        bossBars.clear();
        scores.clear();
        inactivity.clear();
    }

    private boolean isOverworld(Player player) {
        return player.getWorld().getEnvironment() == World.Environment.NORMAL;
    }

    private void updateBossBar(Player player) {
        BossBar bar = bossBars.get(player.getUniqueId());
        if (bar == null) return;

        int score = scores.getOrDefault(player.getUniqueId(), 0);

        double progress = Math.min(1.0, score / (double) MAX_SCORE);

        bar.setProgress(progress);
        bar.setTitle(currentTitle + " §7Score: " + score + "/" + MAX_SCORE);
    }
    public void addKill(Player player) {
        UUID id = player.getUniqueId();

        int score = Math.min(MAX_SCORE, scores.getOrDefault(id, 0) + KILL_POINTS);
        scores.put(id, score);

        inactivity.put(id, 0);

        updateBossBar(player);
    }

    public void onDeath(Player player) {
        UUID id = player.getUniqueId();

        int score = Math.max(0, scores.getOrDefault(id, 0) - DEATH_PENALTY);
        scores.put(id, score);

        updateBossBar(player);
    }

    private void startInactivityTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            for (UUID id : bossBars.keySet()) {

                int time = inactivity.getOrDefault(id, 0) + 1;
                inactivity.put(id, time);

                if (time >= 60) {
                    int score = Math.max(0, scores.get(id) - INACTIVITY_PENALTY);
                    scores.put(id, score);
                    inactivity.put(id, 0);

                    Player player = Bukkit.getPlayer(id);
                    if (player != null) updateBossBar(player);
                }
            }

        }, 20L, 20L);
    }

    public void handleJoin(Player player) {
        if (!isOverworld(player)) return;

        BossBar bar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_10);

        bar.addPlayer(player);

        bossBars.put(player.getUniqueId(), bar);
        scores.putIfAbsent(player.getUniqueId(), 0);
        inactivity.put(player.getUniqueId(), 0);

        updateBossBar(player);
    }
    public void handleQuit(Player player) {
        BossBar bar = bossBars.remove(player.getUniqueId());
        if (bar != null) {
            bar.removeAll();
        }
    }

    public void handleWorldChange(Player player) {
        BossBar bar = bossBars.get(player.getUniqueId());
        if (bar == null) return;

        if (isOverworld(player)) {
            bar.addPlayer(player);
        } else {
            bar.removePlayer(player);
        }
    }

    public void rewardPlayers() {
        for (UUID id : scores.keySet()) {

            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;

            int score = scores.getOrDefault(id, 0);
            int xp = score / 2;

            player.giveExp(xp);
            player.sendActionBar(Component.text("§a+" + xp + " XP from Blood Moon"));
        }
    }

    public int getScore(Player player) {
        return scores.getOrDefault(player.getUniqueId(), 0);
    }
}