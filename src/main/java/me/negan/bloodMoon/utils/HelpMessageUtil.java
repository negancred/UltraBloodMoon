package me.negan.bloodMoon.utils;

import org.bukkit.command.CommandSender;

public class HelpMessageUtil {

    public static void sendBloodMoonHelp(CommandSender sender) {
        sender.sendMessage("§7§m----------------------------------------");
        sender.sendMessage("§c§l[ BloodMoon ]");
        sender.sendMessage("§7§m----------------------------------------");

        sender.sendMessage("§e/bloodmoon help §7- Displays this help menu.");
        sender.sendMessage("§e/bloodmoon start [moon] §7- Start or schedule a Blood Moon.");
        sender.sendMessage("§e/bloodmoon chance §7- View current Blood Moon chance.");
        sender.sendMessage("§e/bloodmoon current §7- View active Blood Moon.");

        sender.sendMessage("§7");
        sender.sendMessage("§6§lMoon Variants:");
        sender.sendMessage("§eBlood");
        sender.sendMessage("§eHallowed");
        sender.sendMessage("§eArcane");

        sender.sendMessage("§7§m----------------------------------------");
    }
}