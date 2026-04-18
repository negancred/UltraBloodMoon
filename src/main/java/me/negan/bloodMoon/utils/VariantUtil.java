package me.negan.bloodMoon.utils;

import java.util.Random;

public class VariantUtil {

    private static final Random random = new Random();

    public static <T> T pick(Object... weightedItems) {

        int totalWeight = 0;

        for (int i = 0; i < weightedItems.length; i += 2) {
            totalWeight += (int) weightedItems[i];
        }

        int roll = random.nextInt(totalWeight);

        int current = 0;

        for (int i = 0; i < weightedItems.length; i += 2) {
            int weight = (int) weightedItems[i];
            T item = (T) weightedItems[i + 1];

            current += weight;

            if (roll < current) {
                return item;
            }
        }

        throw new IllegalStateException("Variant pick failed.");
    }
}