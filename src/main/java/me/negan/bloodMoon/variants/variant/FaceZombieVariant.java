package me.negan.bloodMoon.variants.variant;

import org.bukkit.Bukkit;
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
import java.util.Random;

public class FaceZombieVariant {

    private static final Random random = new Random();

    public static void apply(Zombie zombie, JavaPlugin plugin) {

        double hpMultiplier = plugin.getConfig().getDouble("variants.face_zombie.hp", 0.9);
        NamespacedKey faceZombieKey = new NamespacedKey(plugin, "face_zombie");

        zombie.getPersistentDataContainer().set(
                faceZombieKey,
                PersistentDataType.BYTE,
                (byte) 1
        );


        double baseHealth = zombie.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        double newHealth = baseHealth * hpMultiplier;

        zombie.getAttribute(Attribute.MAX_HEALTH).setBaseValue(newHealth);
        zombie.setHealth(newHealth);

        zombie.getEquipment().setHelmet(createPlayerHead());
        zombie.getEquipment().setChestplate(createRedArmor(Material.LEATHER_CHESTPLATE));
        zombie.getEquipment().setLeggings(createRedArmor(Material.LEATHER_LEGGINGS));
        zombie.getEquipment().setBoots(createRedArmor(Material.LEATHER_BOOTS));



        ItemStack weapon;

        try {
            weapon = new ItemStack(Material.valueOf("COPPER_SWORD"));
        } catch (IllegalArgumentException e) {
            weapon = new ItemStack(Material.IRON_SWORD);
        }

        zombie.getEquipment().setItemInMainHand(weapon);

        zombie.getEquipment().setHelmetDropChance(0f);
        zombie.getEquipment().setChestplateDropChance(0f);
        zombie.getEquipment().setLeggingsDropChance(0f);
        zombie.getEquipment().setBootsDropChance(0f);
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

    private static ItemStack createRedArmor(Material material) {
        ItemStack item = new ItemStack(material);

        if (item.getItemMeta() instanceof org.bukkit.inventory.meta.LeatherArmorMeta meta) {
            meta.setColor(org.bukkit.Color.RED);
            item.setItemMeta(meta);
        }

        return item;
    }
}