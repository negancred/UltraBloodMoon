package me.negan.bloodMoon;

import me.negan.bloodMoon.commands.BloodMoonCommand;
import me.negan.bloodMoon.listeners.*;
import me.negan.bloodMoon.manager.*;
import me.negan.bloodMoon.moons.MoonManager;
import me.negan.bloodMoon.utils.NightSwitchUtil;
import me.negan.bloodMoon.utils.SleepBlockUtil;
import me.negan.bloodMoon.variants.VariantManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        getLogger().info("ULTRA BLOOD MOON v1.4.0-BETA by: POLACREDE");

        initManagers();
        initSystems();

        startServices();

        registerCommands();
        registerListeners();

        startSchedulers();
        checkUpdates();
    }

    private void initManagers() {
        dataManager = new DataManager(this);
        rewardManager = new RewardManager(this);
        bossBarManager = new BossbarManager(this, rewardManager);
        moonManager = new MoonManager(this, bossBarManager, rewardManager);

        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBarManager.removeBossBar(player);
        }
    }

    private void initSystems() {
        nightSwitch = new NightSwitchUtil(this, dataManager, moonManager);
        nightManager = new NightManager(this, nightSwitch, moonManager);
        nightManager.loadWorld();
    }

    private void startServices() {
        nightManager.start();

        VariantManager variantManager = new VariantManager(this, nightSwitch);
        variantManager.start();
    }

    private void registerCommands() {
        BloodMoonCommand cmd = new BloodMoonCommand(
                nightSwitch,
                this,
                rewardManager,
                bossBarManager
        );

        Objects.requireNonNull(getCommand("bloodmoon")).setExecutor(cmd);
        Objects.requireNonNull(getCommand("bloodmoon")).setTabCompleter(cmd);
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();

        pm.registerEvents(new SleepBlockUtil(this, nightSwitch), this);
        pm.registerEvents(new SpookyHitListener(this), this);
        pm.registerEvents(new SpookRevealListener(this), this);
        pm.registerEvents(new BossbarListener(bossBarManager, rewardManager, this, nightSwitch), this);
        pm.registerEvents(new VariantSpawnListener(this, dataManager), this);
        pm.registerEvents(new EnvironmentControlListener(this, nightSwitch), this);
        pm.registerEvents(new FaceZombieListener(this), this);
        pm.registerEvents(new ZombieBruteHitListener(this), this);
    }

    private void startSchedulers() {
        new GeneralScheduler(this).start();
        new ScoreScheduler(this, rewardManager, bossBarManager, moonManager).start();
    }

    private void checkUpdates() {
        updateManager = new UpdateManager(this);
        updateManager.checkForUpdates();
    }

    @Override
    public void onDisable() {
        getLogger().info("BloodMoon disabled!");
    }
}