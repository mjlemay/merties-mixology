package com.mertie.mixology.effect;

import com.hypixel.hytale.server.core.entity.entities.Player;
import java.util.logging.Logger;

/**
 * Handles the consumption effects of mixology drinks.
 * Applies health restoration when a drink is consumed.
 *
 * TODO: Health is now managed through EntityStatMap component system.
 *       Reimplement using entityStatMap.get("health") / entityStatMap.setStatValue()
 *       once the stat key names are confirmed from modding docs.
 *       See: https://hytalemodding.dev
 */
public class DrinkEffect {
    private static final Logger LOGGER = Logger.getLogger(DrinkEffect.class.getName());

    /**
     * Apply a health boost to a player when they consume a drink.
     * The boost is calculated as a percentage of max health.
     *
     * @param player The player consuming the drink
     * @param healthMultiplier The multiplier for health restoration (e.g., 1.15 for 15% boost)
     */
    public static void applyHealthBoost(Player player, float healthMultiplier) {
        if (player == null) {
            LOGGER.warning("Attempted to apply health boost to null player");
            return;
        }

        // TODO: Reimplement with new EntityStatMap health API
        // Old API used player.getHealth() / player.setHealth() / player.getMaxHealth()
        // New API uses EntityStatMap component: entityStatMap.get("health"), entityStatMap.setStatValue(...)
        LOGGER.warning("DrinkEffect.applyHealthBoost is stubbed — needs EntityStatMap integration");
    }

    /**
     * Apply a health boost with logging for debugging.
     * Useful during testing to verify effect application.
     *
     * @param player The player consuming the drink
     * @param drinkName The name of the drink being consumed
     * @param healthMultiplier The multiplier for health restoration
     */
    public static void applyHealthBoostWithLogging(Player player, String drinkName, float healthMultiplier) {
        if (player == null) {
            return;
        }

        // TODO: Reimplement with new EntityStatMap health API
        LOGGER.info(String.format("Player consumed %s (stubbed — health effect not applied)", drinkName));
        applyHealthBoost(player, healthMultiplier);
    }
}
