package me.negan.bloodMoon.variants.variant;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Skeleton;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SkeletonVariant {

    public static void apply(Skeleton skeleton, JavaPlugin plugin) {

        double multiplier = plugin.getConfig().getDouble("variants.skeleton.hp", 1.2);

        if (skeleton.getAttribute(Attribute.MAX_HEALTH) != null) {
            double base = Objects.requireNonNull(skeleton.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
            double newHealth = base * multiplier;

            Objects.requireNonNull(skeleton.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(newHealth);
            skeleton.setHealth(newHealth);
        }
    }
}