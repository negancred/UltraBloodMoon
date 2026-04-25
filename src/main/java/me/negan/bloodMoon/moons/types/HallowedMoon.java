package me.negan.bloodMoon.moons.types;

import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.manager.RewardManager;
import me.negan.bloodMoon.moons.Moon;
import me.negan.bloodMoon.utils.BroadcastUtil;
import me.negan.bloodMoon.utils.SoundUtil;
import me.negan.bloodMoon.utils.VariantUtil;
import me.negan.bloodMoon.variants.variant.Spook;
import me.negan.bloodMoon.variants.variant.SpookyArcher;
import me.negan.bloodMoon.variants.variant.SpookySkeleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.List;

public class HallowedMoon extends Moon {

    private final JavaPlugin plugin;
    private final BossbarManager bossBarManager;
    private final RewardManager rewardManager;
    public HallowedMoon(JavaPlugin plugin, BossbarManager bossBarManager, RewardManager rewardManager) {
        this.plugin = plugin;
        this.bossBarManager = bossBarManager;
        this.rewardManager = rewardManager;
    }

    @Override
    public String getName() {
        return "Hallowed Moon";
    }

    @Override
    public void onNightStart() {

        String[] messages = {
                "§6A strange calm fills the air...",
                "§6The Hallowed Moon watches silently...",
                "§6You hear a faint laugh coming from above...",
                "§6Something feels... different tonight."
        };

        BroadcastUtil.broadcastRandom(messages);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(
                    Title.title(
                            Component.text("Hallowed Moon").color(NamedTextColor.YELLOW),
                            Component.text("Skeletal horrors roam the land").color(NamedTextColor.GOLD),
                            Title.Times.times(
                                    Duration.ofMillis(500),
                                    Duration.ofSeconds(3),
                                    Duration.ofMillis(1000)
                            )
                    )
            );
        }
        SoundUtil.playGlobalSound(Sound.ENTITY_WITCH_CELEBRATE, 1.0f, 1.5f);
        SoundUtil.playGlobalSound(Sound.AMBIENT_CAVE, 1.2f, 1.8f);

        bossBarManager.start(BarColor.YELLOW, "§6Hallowed Moon");
    }

    @Override
    public void onNightTick() {}

    @Override
    public void onNightEnd() {
        bossBarManager.stop();

        List<NamespacedKey> keys = List.of(
                new NamespacedKey(plugin, "spooky_skeleton"),
                new NamespacedKey(plugin, "spooky_archer"),
                new NamespacedKey(plugin, "spook")
        );

        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {

                for (NamespacedKey key : keys) {
                    if (entity.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {

                        world.spawnParticle(Particle.SOUL, entity.getLocation(), 20);
                        entity.remove();
                        break;
                    }
                }
            }
        }


    }

    @Override
    public void onMobSpawn(LivingEntity entity) {}


    @Override
    public LivingEntity spawnMob(World world, Location loc) {

        Skeleton skeleton = world.spawn(loc, Skeleton.class);

        int variant = VariantUtil.pick(
                25, 0,   // spook
                30, 1,  // archer
                45, 2   // spooky skele
        );

        switch (variant) {
            case 0 -> Spook.apply(skeleton, plugin);
            case 1 -> SpookyArcher.apply(skeleton, plugin);
            case 2 -> SpookySkeleton.apply(skeleton, plugin);
        }

        return skeleton;
    }

}