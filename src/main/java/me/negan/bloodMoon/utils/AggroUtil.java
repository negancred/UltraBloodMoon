package me.negan.bloodMoon.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class AggroUtil {

    public static Player getNearestPlayer(Location location, double radius) {
        if (location == null || location.getWorld() == null) return null;

        Player nearest = null;
        double closestDistance = radius * radius;

        for (Player player : location.getWorld().getPlayers()) {
            if (!player.isOnline() || player.isDead()) continue;

            double distance = player.getLocation().distanceSquared(location);

            if (distance <= closestDistance) {
                closestDistance = distance;
                nearest = player;
            }
        }

        return nearest;
    }

    public static void targetNearestPlayer(LivingEntity entity, Plugin plugin, double radius) {
        if (entity == null || plugin == null) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!entity.isValid() || entity.isDead()) return;

            if (!(entity instanceof Mob mob)) return;

            Player target = getNearestPlayer(entity.getLocation(), radius);

            if (target != null) {
                mob.setTarget(target);
                mob.setAware(true);
            }

        }, 1L);
    }
}