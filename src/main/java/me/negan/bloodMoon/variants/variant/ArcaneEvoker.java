package me.negan.bloodMoon.variants.variant;

import me.negan.bloodMoon.variants.SpawnableVariant;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Evoker;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ArcaneEvoker implements SpawnableVariant {
    @Override
    public String getName() {
        return "arcane_evoker";
    }

    @Override
    public void spawn(Location loc, JavaPlugin plugin) {
        var evoker = loc.getWorld().spawn(loc, org.bukkit.entity.Evoker.class);
        apply(evoker, plugin);
    }


    public static void apply(Evoker evoker, JavaPlugin plugin) {
        double multiplier = plugin.getConfig().getDouble("variants.arcane_evoker.hp", 2.0);

        NamespacedKey key = new NamespacedKey(plugin, "arcane_evoker");
        NamespacedKey moonMobKey = new NamespacedKey(plugin, "bloodmoon_mob");

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

        if (evoker.getAttribute(Attribute.MAX_HEALTH) != null) {
            double base = Objects.requireNonNull(evoker.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
            double newHealth = base * multiplier;

            Objects.requireNonNull(evoker.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(newHealth);
            evoker.setHealth(newHealth);
        }
    }
}