package me.negan.bloodMoon.utils;

import org.bukkit.Bukkit;

import java.util.Random;

public class BroadcastUtil {

    private static final Random random = new Random();

    public static void broadcastRandom(String[] messages) {

        if (messages == null || messages.length == 0) return;

        int index = random.nextInt(messages.length);

        Bukkit.broadcastMessage("§o" + messages[index]);
    }

    public static void broadcast(String message) {

        if (message == null || message.isEmpty()) return;

        Bukkit.broadcastMessage("§o" + message);
    }
}