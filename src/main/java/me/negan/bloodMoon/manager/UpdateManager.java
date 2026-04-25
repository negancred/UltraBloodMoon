package me.negan.bloodMoon.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateManager {

    private final JavaPlugin plugin;

    private boolean updateAvailable = false;
    private String latestVersion = "";

    private static final String API_URL =
            "https://api.modrinth.com/v2/project/9qPrRUVo/version";

    public UpdateManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {


            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new URL(API_URL).openStream())
                );
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                String json = response.toString();
                String versionMarker = "\"version_number\":\"";
                int vIndex = json.indexOf(versionMarker);

                if (vIndex == -1) {
                    return;
                }

                int vStart = vIndex + versionMarker.length();
                int vEnd = json.indexOf("\"", vStart);

                String newestVersion = json.substring(vStart, vEnd);


                String current = plugin.getDescription().getVersion();

                if (!current.equalsIgnoreCase(newestVersion)) {
                    updateAvailable = true;
                    latestVersion = newestVersion;

                    plugin.getLogger().info("====================================");
                    plugin.getLogger().info("A new BloodMoon update is available!");
                    plugin.getLogger().info("Current: " + current);
                    plugin.getLogger().info("Latest: " + newestVersion);
                    plugin.getLogger().info("Download: https://modrinth.com/plugin/ultra-blood-moon");
                    plugin.getLogger().info("====================================");

                } else {
                    updateAvailable = false;
                    latestVersion = newestVersion;

                    plugin.getLogger().info("====================================");
                    plugin.getLogger().info("BloodMoon is up to date!");
                    plugin.getLogger().info("Version: " + current);
                    plugin.getLogger().info("====================================");
                }

            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check updates.");
                e.printStackTrace();
            }
        });
    }

    public void notifyPlayer(Player player) {
        player.sendMessage("§c§l[BloodMoon] §7Update available!");
        player.sendMessage("§7Current: §c" + plugin.getDescription().getVersion());
        player.sendMessage("§7Latest: §a" + latestVersion);
        player.sendMessage("§7Download: §bhttps://modrinth.com/plugin/bloodmoon");
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
}