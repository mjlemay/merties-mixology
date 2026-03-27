package com.mertie.mixology.recipe;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Handles the crafting logic for mixology recipes.
 * Bridges the bench system with the recipe registry.
 */
public class MixologyRecipeHandler {
    private static final Logger LOGGER = Logger.getLogger(MixologyRecipeHandler.class.getName());

    /**
     * Attempt to craft a drink from an input fruit item.
     * Returns an ItemStack of the crafted drink, or null if no recipe is found.
     *
     * @param inputItemId The fruit item ID (e.g., "Apple")
     * @return ItemStack of the crafted drink (quantity 1), or null if no recipe exists
     */
    public static ItemStack craft(String inputItemId) {
        if (inputItemId == null || inputItemId.isEmpty()) {
            LOGGER.warning("Attempted to craft with null or empty input item ID");
            return null;
        }

        Optional<Recipe> recipe = RecipeRegistry.getRecipe(inputItemId);

        if (recipe.isPresent()) {
            String drinkId = recipe.get().getOutputItemId();
            try {
                ItemStack result = new ItemStack(drinkId, 1);
                LOGGER.fine(String.format("Crafted drink: %s from %s", drinkId, inputItemId));
                return result;
            } catch (Exception e) {
                LOGGER.warning("Error crafting drink from " + inputItemId + ": " + e.getMessage());
                return null;
            }
        }

        LOGGER.fine("No recipe found for input: " + inputItemId);
        return null;
    }

    /**
     * Check if a recipe exists for the given input without crafting.
     *
     * @param inputItemId The fruit item ID to check
     * @return True if a recipe exists, false otherwise
     */
    public static boolean canCraft(String inputItemId) {
        return RecipeRegistry.hasRecipe(inputItemId);
    }

    /**
     * Get the output drink ID for a given input fruit.
     * Useful for UI or validation purposes.
     *
     * @param inputItemId The fruit item ID
     * @return Optional containing the output drink ID if a recipe exists
     */
    public static Optional<String> getOutputDrinkId(String inputItemId) {
        return RecipeRegistry.getRecipe(inputItemId)
            .map(Recipe::getOutputItemId);
    }

    /**
     * Get the health multiplier for a given input fruit.
     *
     * @param inputItemId The fruit item ID
     * @return Optional containing the health multiplier if a recipe exists
     */
    public static Optional<Float> getHealthMultiplier(String inputItemId) {
        return RecipeRegistry.getRecipe(inputItemId)
            .map(Recipe::getHealthMultiplier);
    }
}
