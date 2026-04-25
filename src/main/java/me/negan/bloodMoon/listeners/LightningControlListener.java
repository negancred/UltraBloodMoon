package me.negan.bloodMoon.listeners;

import me.negan.bloodMoon.utils.NightSwitchUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class LightningControlListener implements Listener {

    private final NightSwitchUtil nightSwitchUtil;

    public LightningControlListener(NightSwitchUtil nightSwitchUtil) {
        this.nightSwitchUtil = nightSwitchUtil;
    }

    @EventHandler
    public void onLightningIgnite(BlockIgniteEvent event) {

        if (!nightSwitchUtil.isBloodMoonActive()) return;

        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) {
            event.setCancelled(true);
        }
    }
}