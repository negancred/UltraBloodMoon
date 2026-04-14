package me.negan.bloodMoon.moons.types;

import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.moons.Moon;
import me.negan.bloodMoon.utils.BroadcastUtil;
import me.negan.bloodMoon.utils.SoundUtil;
import me.negan.bloodMoon.variants.variant.ZombieBrute;
import me.negan.bloodMoon.variants.variant.ZombieVariant;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class DefaultBloodMoon extends Moon {

    private final JavaPlugin plugin;
    private final BossbarManager bossBarManager;
    private final Random random = new Random();

    public DefaultBloodMoon(JavaPlugin plugin, BossbarManager bossBarManager) {
        this.plugin = plugin;
        this.bossBarManager = bossBarManager;
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

        bossBarManager.start(BarColor.RED, "§cBlood Moon");
    }

    @Override
    public void onNightEnd() {
        bossBarManager.rewardPlayers();
        bossBarManager.stop();
    }

    @Override
    public void onNightTick() {}

    @Override
    public void onMobSpawn(LivingEntity entity) {}

    @Override
    public LivingEntity spawnMob(World world, Location loc) {

        Zombie zombie = world.spawn(loc, Zombie.class);

        if (random.nextInt(100) < 80) {
            ZombieVariant.apply(zombie, plugin);
        } else {
            ZombieBrute.apply(zombie, plugin);
        }

        return zombie;
    }
}