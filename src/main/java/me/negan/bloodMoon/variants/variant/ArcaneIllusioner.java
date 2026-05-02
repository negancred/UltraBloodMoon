package me.negan.bloodMoon.variants.variant;

import me.negan.bloodMoon.variants.SpawnableVariant;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Illusioner;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ArcaneIllusioner implements SpawnableVariant {
    @Override
    public String getName() {
        return "arcane_illusioner";
    }

    @Override
    public void spawn(Location loc, JavaPlugin plugin) {
        var illusioner = loc.getWorld().spawn(loc, org.bukkit.entity.Illusioner.class);
        apply(illusioner, plugin);
    }


    public static void apply(Illusioner illusioner, JavaPlugin plugin) {
        double multiplier = plugin.getConfig().getDouble("variants.arcane_illusioner.hp", 2.0);

        NamespacedKey key = new NamespacedKey(plugin, "arcane_illusioner");
        NamespacedKey moonMobKey = new NamespacedKey(plugin, "bloodmoon_mob");

        illusioner.getPersistentDataContainer().set(
                moonMobKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        illusioner.getPersistentDataContainer().set(
                key,
                PersistentDataType.BYTE,
                (byte) 1
        );

        if (illusioner.getAttribute(Attribute.MAX_HEALTH) != null) {
            double base = Objects.requireNonNull(illusioner.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
            double newHealth = base * multiplier;

            Objects.requireNonNull(illusioner.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(newHealth);
            illusioner.setHealth(newHealth);
        }
    }
}