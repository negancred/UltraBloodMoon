package me.negan.bloodMoon.listeners;

import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.utils.NightSwitchUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class BossbarListener implements Listener {

    private final BossbarManager bossBarManager;
    private final JavaPlugin plugin;
    private final NightSwitchUtil nightSwitch;

    public BossbarListener(BossbarManager bossBarManager, JavaPlugin plugin, NightSwitchUtil nightSwitch) {
        this.bossBarManager = bossBarManager;
        this.plugin = plugin;
        this.nightSwitch = nightSwitch;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!nightSwitch.isBloodMoonActive()) {
            bossBarManager.stop();
            return;
        }

        bossBarManager.handleJoin(event.getPlayer());

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        bossBarManager.handleQuit(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (!nightSwitch.isBloodMoonActive()){
            bossBarManager.stop();
            return;
        }

        bossBarManager.handleWorldChange(event.getPlayer());
    }


    @EventHandler
    public void onMobKill(EntityDeathEvent event) {

        if (event.getEntity().getKiller() == null) return;

        Player player = event.getEntity().getKiller();

        if (!isBloodMoonMob(event)) return;

        bossBarManager.addKill(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        bossBarManager.onDeath(event.getEntity());
    }


    private boolean isBloodMoonMob(EntityDeathEvent e) {
        NamespacedKey key = new NamespacedKey(plugin, "moon_mob");

        return e.getEntity().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}