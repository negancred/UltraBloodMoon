package me.negan.bloodMoon.commands;

import me.negan.bloodMoon.manager.BossbarManager;
import me.negan.bloodMoon.manager.RewardManager;
import me.negan.bloodMoon.utils.NightSwitchUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScoreCommand implements CommandExecutor {

    private final RewardManager rewardManager;
    private final BossbarManager bossBarManager;
    private final NightSwitchUtil nightSwitch;

    public ScoreCommand(RewardManager rewardManager,
                        BossbarManager bossBarManager,
                        NightSwitchUtil nightSwitch) {

        this.rewardManager = rewardManager;
        this.bossBarManager = bossBarManager;
        this.nightSwitch = nightSwitch;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (args.length < 4) {
            sender.sendMessage("§cUsage: /bloodmoon score <add|set> <player> <amount>");
            return true;
        }

        String action = args[1].toLowerCase();

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number.");
            return true;
        }

        switch (action) {

            case "add" -> {
                rewardManager.addScore(target, amount);
                bossBarManager.updateBossBar(target);

                sender.sendMessage("§7Added §e" + amount + " §ato " + target.getName());
            }

            case "set" -> {
                rewardManager.setScore(target, amount);
                bossBarManager.updateBossBar(target);

                sender.sendMessage("§aSet §e" + target.getName() + "§a's score to §e" + amount);
            }

            default -> sender.sendMessage("§cInvalid action. Use add or set.");
        }

        return true;
    }
}