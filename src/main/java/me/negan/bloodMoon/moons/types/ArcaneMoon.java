package me.negan.bloodMoon.moons.types;

import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.manager.RewardManager;
import me.negan.bloodMoon.moons.Moon;
import me.negan.bloodMoon.moons.nightAbilities.arcane.CrippleMechanic;
import me.negan.bloodMoon.moons.nightAbilities.arcane.ArcaneDecayMechanic;
import me.negan.bloodMoon.utils.*;
import me.negan.bloodMoon.variants.variant.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.*;

public class ArcaneMoon extends Moon implements Listener {

    private final JavaPlugin plugin;
    private final BossbarManager bossBarManager;
    private final RewardManager rewardManager;
    private final Random random = new Random();

    private int mechanicIntervalSeconds;
    private int crippleDurationSeconds;
    private int cripplePunishDuration;
    private int crippleGraceSeconds;
    private int decayDurationSeconds;
    private double maxDecayDamage;

    private boolean active = false;
    private boolean mechanicRunning = false;

    private CrippleMechanic crippleMechanic;
    private ArcaneDecayMechanic decayMechanic;

    public ArcaneMoon(JavaPlugin plugin, BossbarManager bossBarManager, RewardManager rewardManager) {
        this.plugin = plugin;
        this.bossBarManager = bossBarManager;
        this.rewardManager = rewardManager;
    }

    @Override
    public String getName() {
        return "§5Arcane Moon";
    }

    private void loadConfig() {
        var config = plugin.getConfig();

        mechanicIntervalSeconds = config.getInt("moons.arcane_moon.mechanic_interval", 55);
        crippleDurationSeconds = config.getInt("moons.arcane_moon.cripple.duration", 4);
        cripplePunishDuration = config.getInt("moons.arcane_moon.cripple.punish_duration", 8);
        crippleGraceSeconds = config.getInt("moons.arcane_moon.cripple.grace_period", 2);
        decayDurationSeconds = config.getInt("moons.arcane_moon.decay.duration", 10);
        maxDecayDamage = config.getDouble("moons.arcane_moon.decay.max_damage", 30.0);
    }

    @Override
    public void onNightStart() {
        loadConfig();
        active = true;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        String[] messages = {
                "§5The Arcane Moon rises... reality begins to twist.",
                "§5Magic bends the laws of life itself...",
                "§5The night hums with unstable energy..."
        };

        BroadcastUtil.broadcastRandom(messages);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(
                    Title.title(
                            Component.text("Arcane Moon").color(NamedTextColor.LIGHT_PURPLE),
                            Component.text("Spellcasters rise..").color(NamedTextColor.DARK_PURPLE),
                            Title.Times.times(
                                    Duration.ofMillis(500),
                                    Duration.ofSeconds(3),
                                    Duration.ofMillis(1000)
                            )
                    )
            );
        }

        bossBarManager.start(BarColor.PURPLE, "§5Arcane Moon");

        SoundUtil.playGlobalSound(Sound.AMBIENT_CAVE, 1.2f, 1.8f);
        SoundUtil.playGlobalSound(Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 0.7f);

        startMechanicLoop();
    }
    @Override
    public void onNightTick() {

    }

    @Override
    public void onNightEnd() {
        active = false;
        HandlerList.unregisterAll(this);
        bossBarManager.stop();

        List<NamespacedKey> keys = List.of(
                new NamespacedKey(plugin, "arcane_illusioner"),
                new NamespacedKey(plugin, "arcane_evoker")
        );

        EntityCleanupUtil.removeTaggedEntities(plugin, keys);
    }

    @Override
    public void onMobSpawn(LivingEntity entity) {

    }

    private void startMechanicLoop() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            if (!active || mechanicRunning) return;

            mechanicRunning = true;

            if (random.nextBoolean()) {
                startCripple();
            } else {
                startDecay();
            }

        }, 20L * mechanicIntervalSeconds, 20L * mechanicIntervalSeconds);
    }


    private void startCripple() {
        crippleMechanic = new CrippleMechanic(
                plugin,
                crippleDurationSeconds,
                cripplePunishDuration,
                crippleGraceSeconds
        );

        crippleMechanic.start();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            mechanicRunning = false;
        }, 20L * (crippleGraceSeconds + crippleDurationSeconds));
    }

    private void startDecay() {
        decayMechanic = new ArcaneDecayMechanic(
                plugin,
                decayDurationSeconds,
                maxDecayDamage
        );

        decayMechanic.start();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            mechanicRunning = false;
        }, 20L * decayDurationSeconds);
    }


    @EventHandler
    public void onEvokerCast(EntitySpellCastEvent event) {

        if (!active) return;
        if (!(event.getEntity() instanceof Evoker evoker)) return;

        NamespacedKey key = new NamespacedKey(plugin, "arcane_evoker");

        if (!evoker.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;

        if (event.getSpell() == Spellcaster.Spell.SUMMON_VEX) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onIllusionerCast(EntitySpellCastEvent event) {

        if (!active) return;
        if (!(event.getEntity() instanceof Illusioner illusioner)) return;

        NamespacedKey key = new NamespacedKey(plugin, "arcane_illusioner");

        if (!illusioner.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;

        if (event.getSpell() == Spellcaster.Spell.BLINDNESS) {
            event.setCancelled(true);
        }
    }

    @Override
    public LivingEntity spawnMob(World world, Location loc) {

        EntityType type = VariantUtil.pick(
                30, EntityType.EVOKER,
                10, EntityType.ILLUSIONER,
                60, EntityType.ZOMBIE
        );

        LivingEntity entity = (LivingEntity) world.spawnEntity(loc, type);

        switch (type) {
            case EVOKER -> ArcaneEvoker.apply((Evoker) entity, plugin);
            case ILLUSIONER -> ArcaneIllusioner.apply((Illusioner) entity, plugin);
            case ZOMBIE -> ZombieVariant.apply((Zombie) entity, plugin);
        }

        return entity;
    }
}