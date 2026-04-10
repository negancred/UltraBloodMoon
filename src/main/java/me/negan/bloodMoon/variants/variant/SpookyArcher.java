package me.negan.bloodMoon.variants.variant;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SpookyArcher {

    public static void apply(Skeleton skeleton, JavaPlugin plugin) {

        NamespacedKey spookyKey = new NamespacedKey(plugin, "spooky_archer");
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



        double multiplier = plugin.getConfig().getDouble("variants.spooky_archer.hp", 1.3);

        if (skeleton.getAttribute(Attribute.MAX_HEALTH) != null) {
            double base = Objects.requireNonNull(skeleton.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
            double newHealth = base * multiplier;

            skeleton.getAttribute(Attribute.MAX_HEALTH).setBaseValue(newHealth);
            skeleton.setHealth(newHealth);
        }

        skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
        skeleton.getEquipment().setItemInMainHandDropChance(0f);

        skeleton.getEquipment().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
        skeleton.getEquipment().setHelmetDropChance(0f);

        Objects.requireNonNull(skeleton.getAttribute(Attribute.SCALE)).setBaseValue(0.8);
    }
}