package me.negan.bloodMoon.listeners;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class FaceZombieListener implements Listener {

    private final JavaPlugin plugin;
    private final NamespacedKey faceZombieKey;

    public FaceZombieListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.faceZombieKey = new NamespacedKey(plugin, "face_zombie");
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        if (!(event.getEntity() instanceof Player)) return;

        if (!attacker.getPersistentDataContainer().has(faceZombieKey, PersistentDataType.BYTE)) return;

        double damage = event.getFinalDamage();

        double lifestealPercent = plugin.getConfig().getDouble("variants.face_zombie.lifesteal", 1.0);

        double healAmount = damage * lifestealPercent;

        double maxHealth = Objects.requireNonNull(attacker.getAttribute(Attribute.MAX_HEALTH)).getValue();
        double newHealth = Math.min(attacker.getHealth() + healAmount, maxHealth);
        attacker.setHealth(newHealth);
    }
}