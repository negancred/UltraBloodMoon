package me.negan.bloodMoon.commands;

import me.negan.bloodMoon.variants.variant.*;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import me.negan.bloodMoon.variants.SpawnableVariant;

import java.util.*;

public class VariantSpawnCommand implements TabExecutor {

    private final JavaPlugin plugin;
    private final Map<String, SpawnableVariant> variants = new HashMap<>();
    private final NamespacedKey bloodmoonKey;


    public VariantSpawnCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.bloodmoonKey = new NamespacedKey(plugin, "bloodmoon_mob");

        registerVariants();
    }

    private void registerVariants() {
        register(new ZombieBrute());
        register(new ZombieVariant());
        register(new FaceZombieVariant());
        register(new Spook());
        register(new SpookySkeleton());
        register(new SpookyArcher());
        register(new ArcaneEvoker());
        register(new ArcaneIllusioner());
    }

    private void register(SpawnableVariant variant) {
        variants.put(variant.getName(), variant);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only.");
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage("§cNo permission.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /bm spawn <variant>");
            return true;
        }

        String name = args[1].toLowerCase();
        SpawnableVariant variant = variants.get(name);

        if (variant == null) {
            player.sendMessage("§7Unknown variant.");
            return true;
        }

        variant.spawn(player.getLocation(), plugin);
        player.sendMessage("§7Spawned: §e" + name);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 2) {
            return variants.keySet().stream()
                    .filter(v -> v.startsWith(args[1].toLowerCase()))
                    .sorted()
                    .toList();
        }

        return Collections.emptyList();
    }
}