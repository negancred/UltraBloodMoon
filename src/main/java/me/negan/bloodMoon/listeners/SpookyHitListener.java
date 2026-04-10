package me.negan.bloodMoon.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpookyHitListener implements Listener {

    private final NamespacedKey spookyKey;


    private final Map<UUID, Integer> hitCount = new HashMap<>();
    private final Map<UUID, Long> lastHitTime = new HashMap<>();

    private final long RESET_TIME = 3000;

    public SpookyHitListener(JavaPlugin plugin) {
        this.spookyKey = new NamespacedKey(plugin, "spooky_skeleton");
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {

        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (!(damager instanceof Skeleton skeleton)) return;
        if (!(victim instanceof Player player)) return;

        if (!skeleton.getPersistentDataContainer().has(spookyKey, PersistentDataType.BYTE)) {
            return;
        }

        UUID id = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (!lastHitTime.containsKey(id) || now - lastHitTime.get(id) > RESET_TIME) {
            hitCount.put(id, 0);
        }

        int hits = hitCount.getOrDefault(id, 0) + 1;
        hitCount.put(id, hits);
        lastHitTime.put(id, now);

        int amplifier;

        if (hits >= 13) {
            amplifier = 4;
        } else if (hits >= 8) {
            amplifier = 3;
        } else if (hits >= 5) {
            amplifier = 2;
        } else if (hits >= 3) {
            amplifier = 1;
        } else {
            amplifier = 0;
        }


        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOWNESS,
                40,
                amplifier,
                false,
                false
        ));


        if (hits >= 13) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.BLINDNESS,
                    80,
                    0,
                    false,
                    false
            ));
        }
    }
}