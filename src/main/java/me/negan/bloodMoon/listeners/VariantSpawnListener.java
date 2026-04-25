package me.negan.bloodMoon.listeners;

import me.negan.bloodMoon.manager.ChanceManager;
import me.negan.bloodMoon.manager.DataManager;
import me.negan.bloodMoon.variants.BloodMoonVariant;
import me.negan.bloodMoon.variants.variant.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class VariantSpawnListener implements Listener {

    private final JavaPlugin plugin;
    private final ChanceManager chanceManager;
    private final DataManager dataManager;

    public VariantSpawnListener(JavaPlugin plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.chanceManager = new ChanceManager(plugin);
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {

        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;

        LivingEntity entity = event.getEntity();

        int night = dataManager.getNights();

        double chance = chanceManager.computeVariantChance(night);

        if (Math.random() > chance) return;

        BloodMoonVariant variant = BloodMoonVariant.getRandomFor(entity.getType());
        if (variant == null) return;

        applyVariant(entity, variant);

        double bmChance = chanceManager.computeBloodMoonChance(night);

        Bukkit.getLogger().info(
                "[UltraBloodMoon] Variant: " + variant.name()
                        + " spawned with chance of: "
                        + String.format("%.4f", chance)
        );
    }

    private void applyVariant(LivingEntity entity, BloodMoonVariant variant) {

        switch (variant) {

            case ZOMBIE -> {
                if (entity instanceof Zombie zombie) {
                    ZombieVariant.apply(zombie, plugin);
                }
            }

            case ZOMBIE_BRUTE -> {
                if (entity instanceof Zombie zombie) {
                    ZombieBrute.apply(zombie, plugin);
                }
            }

            case SPOOKY_SKELETON -> {
                if (entity instanceof Skeleton skeleton) {
                    SpookySkeleton.apply(skeleton, plugin);
                }
            }

            case SPOOKY_ARCHER -> {
                if (entity instanceof Skeleton skeleton) {
                    SpookyArcher.apply(skeleton, plugin);
                }
            }

            case SPOOK -> {
                if (entity instanceof Skeleton skeleton) {
                    Spook.apply(skeleton, plugin);
                }
            }
        }
    }
}