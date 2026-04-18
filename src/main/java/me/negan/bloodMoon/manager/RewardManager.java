package me.negan.bloodMoon.manager;

import me.negan.bloodMoon.moons.MoonManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewardManager {

    private final JavaPlugin plugin;

    private final Map<UUID, Integer> scores = new HashMap<>();
    private final Map<UUID, Integer> inactivity = new HashMap<>();

    private final int MAX_SCORE = 1000;
    private final int KILL_POINTS = 20;
    private final int DEATH_PENALTY = 60;
    private final int INACTIVITY_PENALTY = 20;

    private MoonManager moonManager;

    public RewardManager(JavaPlugin plugin, MoonManager moonManager) {
        this.plugin = plugin;
        this.moonManager = moonManager;
        startInactivityTask();
    }

    public void setMoonManager(MoonManager moonManager) {
        this.moonManager = moonManager;
    }


    private void modifyScore(Player player, int amount) {
        if (!isBloodMoonActive()) return;

        UUID id = player.getUniqueId();

        int newScore = scores.getOrDefault(id, 0) + amount;
        newScore = clampScore(newScore);

        scores.put(id, newScore);
    }

    private int clampScore(int value) {
        return Math.max(0, Math.min(MAX_SCORE, value));
    }


    public void addKill(Player player) {
        modifyScore(player, KILL_POINTS);
        inactivity.put(player.getUniqueId(), 0);
    }

    public void onDeath(Player player) {
        modifyScore(player, -DEATH_PENALTY);
    }

    public void addScore(Player player, int amount) {
        modifyScore(player, amount);
    }

    public void setScore(Player player, int amount) {
        UUID id = player.getUniqueId();
        scores.put(id, clampScore(amount));
    }

    public int getScore(Player player) {
        return scores.getOrDefault(player.getUniqueId(), 0);
    }

    public void resetScores() {
        scores.clear();
        inactivity.clear();
    }
    public void rewardPlayers() {
        for (UUID id : scores.keySet()) {

            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;

            int score = scores.getOrDefault(id, 0);

            long worldTime = player.getWorld().getFullTime();
            int day = (int) (worldTime / 24000L);

            double dayMultiplier = 1.0 + (day / 2000.0) * 3.0;
            dayMultiplier = Math.min(dayMultiplier, 4.0);

            int totalXP = player.getTotalExperience();
            double bonusXP = Math.log(totalXP + 1) * 5;

            int xp = (int) ((score * 0.8 * dayMultiplier) + bonusXP);
            xp = Math.max(5, xp);

            player.giveExp(xp);

            player.sendActionBar(Component.text(
                    "§a+" + xp + " XP §7(Score: " + score +
                            ", Day x" + String.format("%.2f", dayMultiplier) + ")"
            ));
        }
    }

    private void startInactivityTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            if (!isBloodMoonActive()) return;

            for (UUID id : scores.keySet()) {

                int time = inactivity.getOrDefault(id, 0) + 1;
                inactivity.put(id, time);

                if (time >= 60) {
                    int newScore = clampScore(scores.getOrDefault(id, 0) - INACTIVITY_PENALTY);
                    scores.put(id, newScore);
                    inactivity.put(id, 0);
                }
            }

        }, 20L, 20L);
    }


    private boolean isBloodMoonActive() {
        return moonManager != null && moonManager.isBloodMoonActive();
    }
}