package me.negan.bloodMoon.commands;

import me.negan.bloodMoon.BloodMoon;
import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.manager.RewardManager;
import me.negan.bloodMoon.moons.Moon;
import me.negan.bloodMoon.moons.MoonManager;
import me.negan.bloodMoon.moons.types.ArcaneMoon;
import me.negan.bloodMoon.moons.types.DefaultBloodMoon;
import me.negan.bloodMoon.moons.types.HallowedMoon;
import me.negan.bloodMoon.utils.HelpMessageUtil;
import me.negan.bloodMoon.utils.NightSwitchUtil;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class BloodMoonCommand implements CommandExecutor {

    private final NightSwitchUtil nightSwitch;
    private final BloodMoon plugin;
    private final RewardManager rewardManager;
    private final BossbarManager bossBarManager;
    private final ScoreCommand scoreCommand;

    private final Random random = new Random();

    public BloodMoonCommand(NightSwitchUtil nightSwitch,
                            BloodMoon plugin,
                            RewardManager rewardManager,
                            BossbarManager bossBarManager) {

        this.nightSwitch = nightSwitch;
        this.plugin = plugin;
        this.rewardManager = rewardManager;
        this.bossBarManager = bossBarManager;

        this.scoreCommand = new ScoreCommand(rewardManager, bossBarManager, nightSwitch);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("score")) {

            if (!sender.isOp()) {
                sender.sendMessage("§cOnly operators can use this command.");
                return true;
            }

            if (!nightSwitch.isBloodMoonActive()) {
                sender.sendMessage("§cYou can only use this command during a Blood Moon.");
                return true;
            }

            return scoreCommand.onCommand(sender, command, label, args);
        }


        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            HelpMessageUtil.sendBloodMoonHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (!sender.isOp()) {
                sender.sendMessage("§cOnly operators can start a Blood Moon.");
                return true;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cPlayers only.");
                return true;
            }

            MoonManager moonManager = nightSwitch.getMoonManager();

            Moon selectedMoon;

            if (args.length == 1) {
                selectedMoon = getRandomMoon();
            } else {
                selectedMoon = switch (args[1].toLowerCase()) {
                    case "blood", "default" ->
                            new DefaultBloodMoon(plugin, bossBarManager, rewardManager);
                    case "hallowed" ->
                            new HallowedMoon(plugin, bossBarManager, rewardManager);
                    case "arcane" ->
                            new ArcaneMoon(plugin, bossBarManager, rewardManager);
                    default -> null;
                };

                if (selectedMoon == null) {
                    player.sendMessage("§7Unknown moon type.");
                    return true;
                }
            }

            World world = player.getWorld();
            long time = world.getTime();

            if (time > 13000) {
                player.sendMessage("§cCannot start Blood Moon during night time.");
                return true;
            }

            moonManager.forceMoon(selectedMoon);
            nightSwitch.setForceBloodMoon(true);

            player.sendMessage("§7Tonight's moon has been set to: §e" + selectedMoon.getName());

            return true;
        }

        if (args[0].equalsIgnoreCase("chance")) {

            int n = nightSwitch.getNightsSinceLastBloodMoon();
            double chance = nightSwitch.getCurrentChance();

            String percent = String.format("%.2f", chance * 100);

            sender.sendMessage("§7Nights since last Blood Moon: §e" + n);
            sender.sendMessage("§7Current chance: §c" + percent + "%");
            return true;
        }

        if (args[0].equalsIgnoreCase("current")) {

            if (!nightSwitch.isBloodMoonActive()) {
                sender.sendMessage("§7There is no active Blood Moon.");
                return true;
            }

            Moon current = nightSwitch.getMoonManager().getCurrentMoon();

            if (current != null) {
                sender.sendMessage("§cCurrent Moon: §e" + current.getName());
            } else {
                sender.sendMessage("§7There is no active Blood Moon.");
            }

            return true;
        }

        sender.sendMessage("§cUsage: /bloodmoon <start | chance | current | score>");
        return true;
    }

    private Moon getRandomMoon() {
        int roll = random.nextInt(3);

        return switch (roll) {
            case 0 -> new DefaultBloodMoon(plugin, bossBarManager, rewardManager);
            case 1 -> new HallowedMoon(plugin, bossBarManager, rewardManager);
            case 2 -> new ArcaneMoon(plugin, bossBarManager, rewardManager);
            default -> null;
        };
    }
}