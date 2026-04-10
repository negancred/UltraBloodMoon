package me.negan.bloodMoon.commands;

import me.negan.bloodMoon.BloodMoon;
import me.negan.bloodMoon.moons.Moon;
import me.negan.bloodMoon.moons.MoonManager;
import me.negan.bloodMoon.moons.types.DefaultBloodMoon;
import me.negan.bloodMoon.moons.types.HallowedMoon;
import me.negan.bloodMoon.utils.NightSwitchUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BloodMoonCommand implements CommandExecutor {

    private final NightSwitchUtil nightSwitch;
    private final BloodMoon plugin;

    public BloodMoonCommand(NightSwitchUtil nightSwitch, BloodMoon plugin) {
        this.nightSwitch = nightSwitch;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /bloodmoon <force | chance | current>");
            return true;
        }

        if (args[0].equalsIgnoreCase("force")) {

            if (!sender.isOp()) {
                sender.sendMessage("§cYou do not have permission.");
                return true;
            }

            MoonManager moonManager = nightSwitch.getMoonManager();

            if (args.length == 1) {
                nightSwitch.setForceBloodMoon(true);
                sender.sendMessage("§cRandom Blood Moon will be forced tonight.");
                return true;
            }

            Moon forcedMoon = switch (args[1].toLowerCase()) {
                case "blood" -> new DefaultBloodMoon(plugin, plugin.getBossBarManager());
                case "hallowed" -> new HallowedMoon(plugin, plugin.getBossBarManager());
                default -> null;
            };

            if (forcedMoon == null) {
                sender.sendMessage("§cUnknown moon type.");
                sender.sendMessage("§7Available: blood, hallowed");
                return true;
            }

            moonManager.forceMoon(forcedMoon);

            sender.sendMessage("§cForced moon type: §e" + forcedMoon.getName());
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
                sender.sendMessage("§7No active moon.");
            }

            return true;
        }

        sender.sendMessage("§cUsage: /bloodmoon <force | chance | current>");
        return true;
    }
}