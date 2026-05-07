package me.negan.bloodMoon.variants.variant;

import me.negan.bloodMoon.variants.SpawnableVariant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class FaceZombieVariant implements SpawnableVariant{
    @Override
    public String getName() {
        return "face_zombie";
    }

    @Override
    public void spawn(Location loc, JavaPlugin plugin) {
        var zombie = loc.getWorld().spawn(loc, org.bukkit.entity.Zombie.class);
        apply(zombie, plugin);
    }


    private static final Random random = new Random();

    public static void apply(Zombie zombie, JavaPlugin plugin) {
        NamespacedKey moonMobKey = new NamespacedKey(plugin, "bloodmoon_mob");

        zombie.getPersistentDataContainer().set(
                moonMobKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        double hpMultiplier = plugin.getConfig().getDouble("variants.face_zombie.hp", 0.9);
        NamespacedKey faceZombieKey = new NamespacedKey(plugin, "face_zombie");

        zombie.getPersistentDataContainer().set(
                faceZombieKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        zombie.setCanPickupItems(false);
        zombie.setSilent(true);

        double baseHealth = Objects.requireNonNull(zombie.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
        double newHealth = baseHealth * hpMultiplier;

        Objects.requireNonNull(zombie.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(newHealth);
        zombie.setHealth(newHealth);

        zombie.getEquipment().setHelmet(createPlayerHead());
        ItemStack weapon;

        try {
            weapon = new ItemStack(Material.valueOf("COPPER_SWORD"));
        } catch (IllegalArgumentException e) {
            weapon = new ItemStack(Material.IRON_SWORD);
        }

        zombie.getEquipment().setItemInMainHand(weapon);
        zombie.getEquipment().setHelmetDropChance(0f);
        zombie.getEquipment().setItemInMainHandDropChance(0f);

        zombie.setSilent(true);

    }

    private static ItemStack createPlayerHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        List<? extends Player> players = Bukkit.getOnlinePlayers().stream().toList();

        if (!players.isEmpty()) {
            Player randomPlayer = players.get(random.nextInt(players.size()));
            meta.setOwningPlayer(randomPlayer);
        }

        head.setItemMeta(meta);
        return head;
    }


}