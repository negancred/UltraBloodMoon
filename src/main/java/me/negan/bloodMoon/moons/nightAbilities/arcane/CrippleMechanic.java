package me.negan.bloodMoon.moons.nightAbilities.arcane;

import me.negan.bloodMoon.utils.BroadcastUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CrippleMechanic implements Listener {

    private final JavaPlugin plugin;

    private final int durationSeconds;
    private final int punishDurationSeconds;
    private final int graceSeconds;

    private boolean active = false;
    private boolean crippleActive = false;
    private boolean crippleGrace = false;

    private final Set<UUID> punished = new HashSet<>();

    public CrippleMechanic(JavaPlugin plugin,
                           int durationSeconds,
                           int punishDurationSeconds,
                           int graceSeconds) {
        this.plugin = plugin;
        this.durationSeconds = durationSeconds;
        this.punishDurationSeconds = punishDurationSeconds;
        this.graceSeconds = graceSeconds;
    }

    public void start() {

        active = true;
        punished.clear();

        BroadcastUtil.broadcast("§5The Arcane Moon binds your soul... §cDo not move.");

        crippleGrace = true;
        crippleActive = false;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            crippleGrace = false;
            crippleActive = true;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                crippleActive = false;
                active = false;

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!isInOverworld(p)) continue;

                    p.playSound(
                            p.getLocation(),
                            Sound.BLOCK_BEACON_ACTIVATE,
                            1f,
                            1.2f
                    );
                }

            }, 20L * durationSeconds);

        }, 20L * graceSeconds);
    }

    public boolean isRunning() {
        return active;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        if (!active) return;
        if (!crippleActive) return;
        if (crippleGrace) return;

        if (event.getFrom().getX() != event.getTo().getX()
                || event.getFrom().getZ() != event.getTo().getZ()) {

            Player p = event.getPlayer();

            if (!isInOverworld(p)) return;
            if (punished.contains(p.getUniqueId())) return;

            punished.add(p.getUniqueId());

            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * punishDurationSeconds, 4));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * punishDurationSeconds, 4));
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * punishDurationSeconds, 4));
            p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * punishDurationSeconds - 1, 1));

            p.damage(12.0, DamageSource.builder(DamageType.MAGIC).build());

            p.playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1f, 0.5f);
        }
    }

    private boolean isInOverworld(Player player) {
        return player.getWorld().getEnvironment() == World.Environment.NORMAL;
    }
}