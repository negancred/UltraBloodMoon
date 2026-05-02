package me.negan.bloodMoon.variants.variant;

import me.negan.bloodMoon.variants.SpawnableVariant;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Skeleton;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Spook implements SpawnableVariant {
    @Override
    public String getName() {
        return "spook";
    }

    @Override
    public void spawn(Location loc, JavaPlugin plugin) {
        var skeleton = loc.getWorld().spawn(loc, org.bukkit.entity.Skeleton.class);
        apply(skeleton, plugin);
    }


    public static void apply(Skeleton skeleton, JavaPlugin plugin) {

        NamespacedKey spookKey = new NamespacedKey(plugin, "spook");
        NamespacedKey moonMobKey = new NamespacedKey(plugin, "bloodmoon_mob");

        skeleton.getPersistentDataContainer().set(moonMobKey, PersistentDataType.BYTE, (byte) 1);
        skeleton.getPersistentDataContainer().set(spookKey, PersistentDataType.BYTE, (byte) 1);

        double multiplier = plugin.getConfig().getDouble("variants.spook.hp", 1.2);

        if (skeleton.getAttribute(Attribute.MAX_HEALTH) != null) {
            double base = Objects.requireNonNull(skeleton.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
            double newHealth = base * multiplier;

            Objects.requireNonNull(skeleton.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(newHealth);
            skeleton.setHealth(newHealth);
        }

        skeleton.getEquipment().clear();

        Objects.requireNonNull(skeleton.getAttribute(Attribute.SCALE)).setBaseValue(0.85);

        skeleton.addPotionEffect(new PotionEffect(
                PotionEffectType.INVISIBILITY,
                Integer.MAX_VALUE,
                0,
                false,
                false
        ));

    }
}