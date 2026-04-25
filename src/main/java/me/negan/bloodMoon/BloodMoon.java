package me.negan.bloodMoon;

import me.negan.bloodMoon.commands.BloodMoonCommand;
import me.negan.bloodMoon.listeners.*;
import me.negan.bloodMoon.manager.*;
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
    private RewardManager rewardManager;
    private UpdateManager updateManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("ULTRA BLOOD MOON v1.3.1-BETA by: POLACREDE");

        dataManager = new DataManager(this);

        rewardManager = new RewardManager(this, null, bossBarManager);
        bossBarManager = new BossbarManager(this, rewardManager);
        moonManager = new MoonManager(this, bossBarManager, rewardManager);


        rewardManager.setMoonManager(moonManager);
        moonManager.setRewardManager(rewardManager);

        nightSwitch = new NightSwitchUtil(this, dataManager, moonManager);
        nightManager = new NightManager(this, nightSwitch, moonManager);

        nightManager.start();


        Objects.requireNonNull(getCommand("bloodmoon"))
                .setExecutor(new BloodMoonCommand(
                        nightSwitch,
                        this,
                        rewardManager,
                        bossBarManager
                ));

        VariantManager variantManager = new VariantManager(this, nightSwitch);
        variantManager.start();

        getServer().getPluginManager().registerEvents(
                new SleepBlockUtil(this, nightSwitch),
                this
        );

        getServer().getPluginManager().registerEvents(
                new SpookyHitListener(this), this
        );
        getServer().getPluginManager().registerEvents(
                new SpookRevealListener(this), this
        );
        getServer().getPluginManager().registerEvents(
                new BossbarListener(bossBarManager, rewardManager, this, nightSwitch),
                this
        );
        getServer().getPluginManager().registerEvents(
                new VariantSpawnListener(this, dataManager), this
        );

        getServer().getPluginManager().registerEvents(
                new LightningControlListener(nightSwitch),
                this
        );

        updateManager = new UpdateManager(this);
        updateManager.checkForUpdates();
    }

    @Override
    public void onDisable() {
        getLogger().info("BloodMoon disabled!");
    }

    public BossbarManager getBossBarManager() {
        return bossBarManager;
    }
}