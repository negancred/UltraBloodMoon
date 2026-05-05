package me.negan.bloodMoon.listeners;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZombieBruteHitListener implements Listener {

    private final NamespacedKey bruteKey;

    private final Map<UUID, Long> lastAbilityUse = new HashMap<>();

    private final long COOLDOWN = 5000;

    public ZombieBruteHitListener(JavaPlugin plugin) {
        this.bruteKey = new NamespacedKey(plugin, "zombie_brute");
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {

        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (!(damager instanceof Zombie zombie)) return;
        if (!(victim instanceof Player player)) return;

        if (!zombie.getPersistentDataContainer().has(bruteKey, PersistentDataType.BYTE)) {
            return;
        }
        UUID bruteId = zombie.getUniqueId();
        long now = System.currentTimeMillis();
        long lastUsed = lastAbilityUse.getOrDefault(bruteId, 0L);

        if (now - lastUsed < COOLDOWN) {
            return;
        }



        lastAbilityUse.put(bruteId, now);


        Vector velocity = player.getVelocity();
        velocity.setY(1.1);
        player.setVelocity(velocity);

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.WEAKNESS,
                100,
                0,
                false,
                false
        ));

        Location loc = player.getLocation();


        player.getWorld().spawnParticle(Particle.CRIT, loc, 20, 0.5, 0.5, 0.5, 0.2);
        player.getWorld().spawnParticle(Particle.SMOKE, loc, 15, 0.3, 0.3, 0.3, 0.05);

        player.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.2f, 0.6f);
        player.getWorld().playSound(loc, Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 0.5f);
    }
}