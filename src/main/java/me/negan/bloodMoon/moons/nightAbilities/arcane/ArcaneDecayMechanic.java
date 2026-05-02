package me.negan.bloodMoon.moons.nightAbilities.arcane;

import me.negan.bloodMoon.utils.BroadcastUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArcaneDecayMechanic implements Listener {

    private final JavaPlugin plugin;

    private final int durationSeconds;
    private final double maxDecayDamage;

    private boolean active = false;

    private final Map<UUID, Double> healMap = new HashMap<>();

    public ArcaneDecayMechanic(JavaPlugin plugin,
                               int durationSeconds,
                               double maxDecayDamage) {
        this.plugin = plugin;
        this.durationSeconds = durationSeconds;
        this.maxDecayDamage = maxDecayDamage;
    }

    public void start() {

        active = true;
        healMap.clear();

        BroadcastUtil.broadcast("§5Healing will be repaid...");
        Bukkit.getPluginManager().registerEvents(this, plugin);

        long durationTicks = 20L * durationSeconds;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            for (UUID uuid : healMap.keySet()) {

                Player p = Bukkit.getPlayer(uuid);
                if (p == null || !isInOverworld(p)) continue;

                double healed = healMap.getOrDefault(uuid, 0.0);
                double damage = Math.min(healed * 1.5, maxDecayDamage);

                if (damage > 0) {
                    p.damage(damage);

                    p.sendMessage("§5You healed §d" + String.format("%.1f", healed / 2) +
                            "... §5The Arcane Moon reclaims it.");

                    p.playSound(
                            p.getLocation(),
                            Sound.ENTITY_EVOKER_PREPARE_ATTACK,
                            1f,
                            0.8f
                    );
                }
            }

            active = false;

        }, durationTicks);
    }

    public boolean isRunning() {
        return active;
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent event) {

        if (!active) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!isInOverworld(player)) return;

        healMap.put(
                player.getUniqueId(),
                healMap.getOrDefault(player.getUniqueId(), 0.0) + event.getAmount()
        );
    }

    private boolean isInOverworld(Player player) {
        return player.getWorld().getEnvironment() == World.Environment.NORMAL;
    }
}