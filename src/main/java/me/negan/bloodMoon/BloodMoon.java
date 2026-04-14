package me.negan.bloodMoon;

import me.negan.bloodMoon.commands.BloodMoonCommand;
import me.negan.bloodMoon.listeners.BossbarListener;
import me.negan.bloodMoon.listeners.SpookyHitListener;
import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.manager.NightManager;
import me.negan.bloodMoon.manager.DataManager;
import me.negan.bloodMoon.moons.MoonManager;
import me.negan.bloodMoon.utils.NightSwitchUtil;
import me.negan.bloodMoon.utils.SleepBlockUtil;
import me.negan.bloodMoon.variants.VariantManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class BloodMoon extends JavaPlugin {

    private NightSwitchUtil nightSwitch;
    private NightManager nightManager;
    private DataManager dataManager;
    private MoonManager moonManager;
    private BossbarManager bossBarManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("ULTRA BLOOD MOON v1.1.2 BETA by: POLACREDE");

        dataManager = new DataManager(this);
        moonManager = new MoonManager(this);
        nightSwitch = new NightSwitchUtil(this, dataManager, moonManager);
        nightManager = new NightManager(this, nightSwitch, moonManager);
        bossBarManager = new BossbarManager(this);

        nightManager.start();

        Objects.requireNonNull(getCommand("bloodmoon"))
                .setExecutor(new BloodMoonCommand(nightSwitch, this));

        VariantManager variantManager = new VariantManager(this, nightSwitch);
        variantManager.start();

        getServer().getPluginManager().registerEvents(
                new SleepBlockUtil(this, nightSwitch),
                this
        );

        getServer().getPluginManager().registerEvents(
                new SpookyHitListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new BossbarListener(bossBarManager, this, nightSwitch),
                this
        );
    }

    @Override
    public void onDisable() {
        getLogger().info("BloodMoon disabled!");
    }

    public BossbarManager getBossBarManager() {
        return bossBarManager;
    }
}