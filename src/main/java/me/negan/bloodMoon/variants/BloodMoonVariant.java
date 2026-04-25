package me.negan.bloodMoon.variants;

import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum BloodMoonVariant {

    ZOMBIE(EntityType.ZOMBIE),
    ZOMBIE_BRUTE(EntityType.ZOMBIE),
    SPOOKY_SKELETON(EntityType.SKELETON),
    SPOOKY_ARCHER(EntityType.SKELETON),
    SPOOK(EntityType.SKELETON);

    private final EntityType type;

    BloodMoonVariant(EntityType type) {
        this.type = type;
    }

    public EntityType getType() {
        return type;
    }

    private static final Random RANDOM = new Random();

    public static BloodMoonVariant getRandomFor(EntityType entityType) {
        List<BloodMoonVariant> valid = new ArrayList<>();

        for (BloodMoonVariant v : values()) {
            if (v.type == entityType) {
                valid.add(v);
            }
        }

        if (valid.isEmpty()) return null;

        return valid.get(RANDOM.nextInt(valid.size()));
    }
}