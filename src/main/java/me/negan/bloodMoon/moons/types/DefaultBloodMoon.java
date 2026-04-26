package me.negan.bloodMoon.moons.types;

import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.manager.RewardManager;
import me.negan.bloodMoon.moons.Moon;
import me.negan.bloodMoon.utils.BroadcastUtil;
import me.negan.bloodMoon.utils.SoundUtil;
import me.negan.bloodMoon.utils.VariantUtil;
import me.negan.bloodMoon.variants.variant.FaceZombieVariant;
import me.negan.bloodMoon.variants.variant.ZombieBrute;
import me.negan.bloodMoon.variants.variant.ZombieVariant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.Random;

public class DefaultBloodMoon extends Moon {

    private final JavaPlugin plugin;
    private final BossbarManager bossBarManager;
    private final RewardManager rewardManager;
    private final Random random = new Random();

    public DefaultBloodMoon(JavaPlugin plugin,
                            BossbarManager bossBarManager,
                            RewardManager rewardManager) {

        this.plugin = plugin;
        this.bossBarManager = bossBarManager;
        this.rewardManager = rewardManager;
    }

    @Override
    public String getName() {
        return "§cBlood Moon";
    }

    @Override
    public void onNightStart() {

        String[] messages = {
                "§cBlood Moon is rising...",
                "§cThe sky bleeds as darkness awakens...",
                "§cThe night glows crimson..."
        };

        BroadcastUtil.broadcastRandom(messages);

        SoundUtil.playGlobalSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 0.6f);
        SoundUtil.playGlobalSound(Sound.ENTITY_WITHER_SPAWN, 0.6f, 1.2f);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(
                    Title.title(
                            Component.text("Blood Moon").color(NamedTextColor.RED),
                            Component.text("Zombies will spawn in massive hordes.").color(NamedTextColor.DARK_RED),
                            Title.Times.times(
                                    Duration.ofMillis(500),
                                    Duration.ofSeconds(3),
                                    Duration.ofMillis(1000)
                            )
                    )
            );
        }

        bossBarManager.start(BarColor.RED, "§cBlood Moon");
    }
    @Override
    public void onNightEnd() {
        bossBarManager.stop();
    }

    @Override
    public void onNightTick() {}

    @Override
    public void onMobSpawn(LivingEntity entity) {}

    @Override
    public LivingEntity spawnMob(World world, Location loc) {

        Zombie zombie = world.spawn(loc, Zombie.class);

        int variant = VariantUtil.pick(
                75, 0,  // normal zombie
                15, 1,  // brute
                10, 2   // face zombie
        );

        switch (variant) {
            case 0 -> ZombieVariant.apply(zombie, plugin);
            case 1 -> ZombieBrute.apply(zombie, plugin);
            case 2 -> FaceZombieVariant.apply(zombie, plugin);
        }

        return zombie;
    }
}