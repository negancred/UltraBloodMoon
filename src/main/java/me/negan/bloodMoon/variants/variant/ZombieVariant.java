package me.negan.bloodMoon.variants.variant;

import me.negan.bloodMoon.variants.SpawnableVariant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

public class ZombieVariant implements SpawnableVariant {
    @Override
    public String getName() {
        return "zombie_variant";
    }

    @Override
    public void spawn(Location loc, JavaPlugin plugin) {
        var zombie = loc.getWorld().spawn(loc, org.bukkit.entity.Zombie.class);
        apply(zombie, plugin);
    }

    private static double chance_spawn_with_weapon = 0.4;

    private static final Random random = new Random();

    public static void apply(Zombie zombie, JavaPlugin plugin) {

        NamespacedKey moonMobKey = new NamespacedKey(plugin, "bloodmoon_mob");

        zombie.getPersistentDataContainer().set(
                moonMobKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        double multiplier = plugin.getConfig().getDouble("variants.zombie.hp", 1.4);

        if (zombie.getAttribute(Attribute.MAX_HEALTH) != null) {
            double base = Objects.requireNonNull(zombie.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
            double newHealth = base * multiplier;

            Objects.requireNonNull(zombie.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(newHealth);
            zombie.setHealth(newHealth);
        }

        if (random.nextDouble() <= chance_spawn_with_weapon) {

            Material[] weapons = {
                    Material.COPPER_SWORD,
                    Material.STONE_AXE
            };

            Material chosen = weapons[random.nextInt(weapons.length)];

            zombie.getEquipment().setItemInMainHand(new ItemStack(chosen));
            zombie.getEquipment().setItemInMainHandDropChance(0.0f);
        }
    }
}