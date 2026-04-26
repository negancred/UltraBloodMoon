package me.negan.bloodMoon.listeners;

import me.negan.bloodMoon.utils.NightSwitchUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EnvironmentControlListener implements Listener {

    private final NightSwitchUtil nightSwitchUtil;
    private final JavaPlugin plugin;

    public EnvironmentControlListener(JavaPlugin plugin, NightSwitchUtil nightSwitchUtil) {
        this.plugin = plugin;
        this.nightSwitchUtil = nightSwitchUtil;
    }

    @EventHandler
    public void onLightningIgnite(BlockIgniteEvent event) {

        if (!nightSwitchUtil.isBloodMoonActive()) return;

        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRiptide(PlayerRiptideEvent event) {

        if (!nightSwitchUtil.isBloodMoonActive()) return;

        Player player = event.getPlayer();
        World world = player.getWorld();

        double chance = 0.05;

        if (Math.random() > chance) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            if (!player.isValid() || player.isDead()) return;

            Location loc = player.getLocation();

            world.strikeLightning(loc);
            player.damage(17.0);

        }, 1L);
    }
}