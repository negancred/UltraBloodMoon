package me.negan.bloodMoon.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;

public class SleepBlockUtil implements Listener {

    private final JavaPlugin plugin;
    private final NightSwitchUtil nightSwitch;

    public SleepBlockUtil(JavaPlugin plugin, NightSwitchUtil nightSwitch) {
        this.plugin = plugin;
        this.nightSwitch = nightSwitch;
    }

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();


        if (!nightSwitch.isBloodMoonActive()) return;
        event.setCancelled(true);
        player.sendActionBar(Component.text("§cYou feel uneasy..."));
    }
}