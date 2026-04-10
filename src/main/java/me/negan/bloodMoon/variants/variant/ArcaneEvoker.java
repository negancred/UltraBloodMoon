package me.negan.bloodMoon.variants.variant;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Evoker;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ArcaneEvoker {

    public static void apply(Evoker evoker, JavaPlugin plugin) {

        NamespacedKey key = new NamespacedKey(plugin, "arcane_evoker");
        NamespacedKey moonMobKey = new NamespacedKey(plugin, "moon_mob");

        evoker.getPersistentDataContainer().set(
                moonMobKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        evoker.getPersistentDataContainer().set(
                key,
                PersistentDataType.BYTE,
                (byte) 1
        );

        double multiplier = 2.0;

        if (evoker.getAttribute(Attribute.MAX_HEALTH) != null) {
            double base = Objects.requireNonNull(evoker.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
            double newHealth = base * multiplier;

            Objects.requireNonNull(evoker.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(newHealth);
            evoker.setHealth(newHealth);
        }
    }
}