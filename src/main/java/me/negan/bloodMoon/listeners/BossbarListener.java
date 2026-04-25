package me.negan.bloodMoon.listeners;

import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.manager.RewardManager;
import me.negan.bloodMoon.utils.NightSwitchUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


public class BossbarListener implements Listener {

    private final BossbarManager bossBarManager;
    private final RewardManager rewardManager;
    private final JavaPlugin plugin;
    private final NightSwitchUtil nightSwitch;

    public BossbarListener(BossbarManager bossBarManager,
                           RewardManager rewardManager,
                           JavaPlugin plugin,
                           NightSwitchUtil nightSwitch) {

        this.bossBarManager = bossBarManager;
        this.rewardManager = rewardManager;
        this.plugin = plugin;
        this.nightSwitch = nightSwitch;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (nightSwitch.isBloodMoonActive()) {
            bossBarManager.handleJoin(player);
        } else {
            bossBarManager.handleQuit(player);
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        bossBarManager.handleQuit(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (nightSwitch.isBloodMoonActive()) {
            bossBarManager.handleWorldChange(player);
        } else {
            bossBarManager.handleQuit(player);
        }
    }
    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (!nightSwitch.isBloodMoonActive()) return;

        LivingEntity mob = event.getEntity();

        Player player = mob.getKiller();
        if (player == null) return;

        if (!isBloodMoonMob(event)) return;

        double maxHealth = 20.0;
        if (mob.getAttribute(Attribute.MAX_HEALTH) != null) {
            maxHealth = Objects.requireNonNull(mob.getAttribute(Attribute.MAX_HEALTH)).getValue();
        }

        int points = (int) Math.ceil(10 * Math.log(maxHealth));

        points = Math.max(1, Math.min(points, 50));

        rewardManager.addKill(player, points);
        bossBarManager.updateBossBar(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!nightSwitch.isBloodMoonActive()) return;

        Player player = event.getEntity();

        rewardManager.onDeath(player);
        bossBarManager.updateBossBar(player);
    }

    private boolean isBloodMoonMob(EntityDeathEvent e) {
        NamespacedKey key = new NamespacedKey(plugin, "bloodmoon_mob");

        return e.getEntity().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}