package me.negan.bloodMoon.variants.variant;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ZombieBrute {

    public static void apply(Zombie zombie, JavaPlugin plugin) {

        NamespacedKey moonMobKey = new NamespacedKey(plugin, "moon_mob");

        zombie.getPersistentDataContainer().set(
                moonMobKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        double hpMultiplier = plugin.getConfig().getDouble("variants.zombie_brute.hp", 2.5);
        double scale = plugin.getConfig().getDouble("variants.zombie_brute.scale", 1.4);

        if (zombie.getAttribute(Attribute.SCALE) != null) {
            Objects.requireNonNull(zombie.getAttribute(Attribute.SCALE)).setBaseValue(scale);
        }

        if (zombie.getAttribute(Attribute.MAX_HEALTH) != null) {
            double base = Objects.requireNonNull(zombie.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
            double newHealth = base * hpMultiplier;

            Objects.requireNonNull(zombie.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(newHealth);
            zombie.setHealth(newHealth);
        }

        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
        zombie.getEquipment().setItemInMainHandDropChance(0.0f);

        if (zombie.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
            double baseSpeed = Objects.requireNonNull(zombie.getAttribute(Attribute.MOVEMENT_SPEED)).getBaseValue();
            Objects.requireNonNull(zombie.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(baseSpeed * 0.8);
        }

        if (zombie.getAttribute(Attribute.ATTACK_DAMAGE) != null) {
            double baseDamage = Objects.requireNonNull(zombie.getAttribute(Attribute.ATTACK_DAMAGE)).getBaseValue();
            Objects.requireNonNull(zombie.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(baseDamage * 1.5);
        }
    }
}