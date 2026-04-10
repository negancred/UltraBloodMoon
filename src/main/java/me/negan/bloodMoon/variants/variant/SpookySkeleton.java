package me.negan.bloodMoon.variants.variant;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SpookySkeleton {

    public static void apply(Skeleton skeleton, JavaPlugin plugin) {

        NamespacedKey spookyKey = new NamespacedKey(plugin, "spooky_skeleton");
        NamespacedKey moonMobKey = new NamespacedKey(plugin, "moon_mob");

        skeleton.getPersistentDataContainer().set(
                moonMobKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        skeleton.getPersistentDataContainer().set(
                spookyKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        double multiplier = plugin.getConfig().getDouble("variants.spooky_skeleton.hp", 1.4);

        if (skeleton.getAttribute(Attribute.MAX_HEALTH) != null) {
            double base = Objects.requireNonNull(skeleton.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
            double newHealth = base * multiplier;

            Objects.requireNonNull(skeleton.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(newHealth);
            skeleton.setHealth(newHealth);
        }

        skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));
        skeleton.getEquipment().setItemInMainHandDropChance(0f);


        skeleton.getEquipment().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
        skeleton.getEquipment().setHelmetDropChance(0f);

        Objects.requireNonNull(skeleton.getAttribute(Attribute.SCALE)).setBaseValue(0.8);
    }
}