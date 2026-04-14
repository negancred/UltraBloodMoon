package me.negan.bloodMoon.moons.types;

import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.moons.Moon;
import me.negan.bloodMoon.utils.BroadcastUtil;
import me.negan.bloodMoon.utils.SoundUtil;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ArcaneMoon extends Moon implements Listener {

    private final JavaPlugin plugin;
    private final BossbarManager bossBarManager;
    private final Random random = new Random();

    private int mechanicIntervalSeconds;
    private int crippleDurationSeconds;
    private int cripplePunishDuration;
    private int crippleGraceSeconds;
    private int decayDurationSeconds;
    private double maxDecayDamage;



    private boolean active = false;
    private boolean mechanicRunning = false;

    private boolean crippleActive = false;
    private boolean crippleGrace = false;
    private final Set<UUID> punished = new HashSet<>();

    private final Map<UUID, Double> healMap = new HashMap<>();

    public ArcaneMoon(JavaPlugin plugin, BossbarManager bossBarManager) {
        this.plugin = plugin;
        this.bossBarManager = bossBarManager;
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
        bossBarManager.start(BarColor.PURPLE, "§5Arcane Moon");
        SoundUtil.playGlobalSound(Sound.AMBIENT_CAVE, 1.2f, 1.8f);
        SoundUtil.playGlobalSound(Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 0.7f);

        startMechanicLoop();
    }

    @Override
    public void onNightEnd() {
        active = false;
        HandlerList.unregisterAll(this);

        bossBarManager.rewardPlayers();
        bossBarManager.stop();

        List<NamespacedKey> keys = List.of(
                new NamespacedKey(plugin, "arcane_evoker"),
                new NamespacedKey(plugin, "arcane_illusioner")
        );

        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {

                for (NamespacedKey key : keys) {
                    if (entity.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {

                        world.spawnParticle(Particle.SOUL, entity.getLocation(), 40);
                        entity.remove();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onNightTick() {

    }

    @Override
    public void onMobSpawn(LivingEntity entity) {}

    @Override
    public LivingEntity spawnMob(World world, Location loc) {

        if (random.nextBoolean()) {
            Evoker evoker = world.spawn(loc, Evoker.class);
            me.negan.bloodMoon.variants.variant.ArcaneEvoker.apply(evoker, plugin);
            return evoker;
        } else {
            Illusioner illusioner = world.spawn(loc, Illusioner.class);
            me.negan.bloodMoon.variants.variant.ArcaneIllusioner.apply(illusioner, plugin);
            return illusioner;
        }
    }


    private void startMechanicLoop() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            if (!active || mechanicRunning) return;

            mechanicRunning = true;

            if (random.nextBoolean()) {
                startCrippledCurse();
            } else {
                startArcaneDecay();
            }

        }, 20L * mechanicIntervalSeconds, 20L * mechanicIntervalSeconds);
    }

    private void startCrippledCurse() {

        BroadcastUtil.broadcast("§5The Arcane Moon binds your soul... §cDo not move.");

        punished.clear();

        crippleGrace = true;
        crippleActive = false;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            crippleGrace = false;
            crippleActive = true;

            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                crippleActive = false;
                mechanicRunning = false;

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(
                            p.getLocation(),
                            Sound.BLOCK_BEACON_ACTIVATE,
                            1f,
                            1.2f
                    );
                }

            }, 20L * crippleDurationSeconds);

        }, 20L * crippleGraceSeconds);
    }


    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        if (!active || !mechanicRunning) return;
        if (!crippleActive) return;
        if (crippleGrace) return;

        if (event.getFrom().getX() != event.getTo().getX()
                || event.getFrom().getZ() != event.getTo().getZ()) {

            Player p = event.getPlayer();

            if (punished.contains(p.getUniqueId())) return;
            punished.add(p.getUniqueId());

            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * cripplePunishDuration, 4));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * cripplePunishDuration, 4));
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * cripplePunishDuration, 4));
            p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * cripplePunishDuration - 1, 1));
            p.damage(12.0, DamageSource.builder(DamageType.MAGIC).build());

            p.playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1f, 0.5f);
        }
    }

    private void startArcaneDecay() {

        BroadcastUtil.broadcast("§5Healing will be repaid...");

        healMap.clear();

        long durationTicks = 20L * decayDurationSeconds;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            for (UUID uuid : healMap.keySet()) {
                Player p = Bukkit.getPlayer(uuid);
                if (p == null) continue;

                double healed = healMap.getOrDefault(uuid, 0.0);
                double damage = Math.min(healed * 1.5, maxDecayDamage);

                if (damage > 0) {
                    p.damage(damage);

                    p.sendMessage("§5You healed §d" + String.format("%.1f", healed / 2) + "... The Arcane Moon reclaims it.");
                    p.playSound(p.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1f, 0.8f);
                }
            }

            mechanicRunning = false;

        }, durationTicks);
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent event) {

        if (!active || !mechanicRunning) return;
        if (!(event.getEntity() instanceof Player player)) return;

        healMap.put(
                player.getUniqueId(),
                healMap.getOrDefault(player.getUniqueId(), 0.0) + event.getAmount()
        );
    }

    @EventHandler
    public void onEvokerCast(org.bukkit.event.entity.EntitySpellCastEvent event) {

        if (!active) return;
        if (!(event.getEntity() instanceof Evoker evoker)) return;

        NamespacedKey key = new NamespacedKey(plugin, "arcane_evoker");

        if (!evoker.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;

        if (event.getSpell() == org.bukkit.entity.Spellcaster.Spell.SUMMON_VEX) {
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
    public long getSpawnInterval() {
        return 240L;
    }

}