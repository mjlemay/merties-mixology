package com.mertie.mixology.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Central registry for all mixology recipes.
 * Manages fruit-to-drink mappings and provides lookup functionality.
 */
public class RecipeRegistry {
    private static final Logger LOGGER = Logger.getLogger(RecipeRegistry.class.getName());
    private static final Map<String, Recipe> RECIPES = new HashMap<>();

    /**
     * Register a single recipe mapping.
     *
     * @param recipe The recipe to register
     */
    public static void register(Recipe recipe) {
        if (recipe == null) {
            LOGGER.warning("Attempted to register null recipe");
            return;
        }
        RECIPES.put(recipe.getInputItemId(), recipe);
        LOGGER.fine("Registered recipe: " + recipe);
    }

    /**
     * Look up a recipe by input item ID.
     *
     * @param inputItemId The fruit item ID to look up
     * @return Optional containing the recipe if found
     */
    public static Optional<Recipe> getRecipe(String inputItemId) {
        return Optional.ofNullable(RECIPES.get(inputItemId));
    }

    /**
     * Check if a recipe exists for the given input item.
     *
     * @param inputItemId The fruit item ID to check
     * @return True if a recipe exists, false otherwise
     */
    public static boolean hasRecipe(String inputItemId) {
        return RECIPES.containsKey(inputItemId);
    }

    /**
     * Initialize all default recipes.
     * Call this once during plugin setup.
     */
    public static void initializeRecipes() {
        clear(); // Clear any existing recipes first
        
        register(new Recipe("Apple", "Apple_Martini", 1.15f));
        register(new Recipe("Wild_Berry", "Berry_Punch", 1.15f));
        register(new Recipe("Corn", "Corn_Cooler", 1.15f));
        register(new Recipe("Pumpkin", "Autumn_Sip", 1.15f));
        
        LOGGER.info("Initialized " + RECIPES.size() + " mixology recipes");
    }

    /**
     * Clear all registered recipes (useful for testing/reloading).
     */
    public static void clear() {
        RECIPES.clear();
    }

    /**
     * Get the total number of registered recipes.
     *
     * @return Number of recipes
     */
    public static int getRecipeCount() {
        return RECIPES.size();
    }

    /**
     * Get a copy of all registered recipes (for debugging).
     *
     * @return Map of all recipes
     */
    public static Map<String, Recipe> getAll() {
        return new HashMap<>(RECIPES);
    }
}
