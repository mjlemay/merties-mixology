// =============================================================================
// RECIPE SYSTEM SKELETON - Copy these into your plugin
// =============================================================================

// ============ Recipe.java ============
// Location: src/main/java/com/mertie/mixology/recipe/Recipe.java

package com.mertie.mixology.recipe;

public class Recipe {
    private final String inputItemId;
    private final String outputItemId;
    private final float healthMultiplier;

    public Recipe(String inputItemId, String outputItemId, float healthMultiplier) {
        this.inputItemId = inputItemId;
        this.outputItemId = outputItemId;
        this.healthMultiplier = healthMultiplier;
    }

    public String getInputItemId() {
        return inputItemId;
    }

    public String getOutputItemId() {
        return outputItemId;
    }

    public float getHealthMultiplier() {
        return healthMultiplier;
    }
}


// ============ RecipeRegistry.java ============
// Location: src/main/java/com/mertie/mixology/recipe/RecipeRegistry.java

package com.mertie.mixology.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RecipeRegistry {
    private static final Map<String, Recipe> RECIPES = new HashMap<>();

    /**
     * Register a single recipe mapping (fruit -> drink)
     */
    public static void register(Recipe recipe) {
        RECIPES.put(recipe.getInputItemId(), recipe);
    }

    /**
     * Look up a recipe by input item ID
     */
    public static Optional<Recipe> getRecipe(String inputItemId) {
        return Optional.ofNullable(RECIPES.get(inputItemId));
    }

    /**
     * Initialize all default recipes
     * Call this from MixologyPlugin.setup()
     */
    public static void initializeRecipes() {
        register(new Recipe("Apple", "Apple_Martini", 1.15f));
        register(new Recipe("Wild_Berry", "Berry_Punch", 1.15f));
        register(new Recipe("Corn", "Corn_Cooler", 1.15f));
        register(new Recipe("Pumpkin", "Autumn_Sip", 1.15f));
    }

    /**
     * Clear all recipes (useful for testing/reloading)
     */
    public static void clear() {
        RECIPES.clear();
    }

    /**
     * Get all registered recipes (for debugging)
     */
    public static Map<String, Recipe> getAll() {
        return new HashMap<>(RECIPES);
    }
}


// ============ DrinkEffect.java ============
// Location: src/main/java/com/mertie/mixology/effect/DrinkEffect.java

package com.mertie.mixology.effect;

import com.hypixel.hytale.server.core.entity.player.ServerPlayer;

public class DrinkEffect {
    /**
     * Apply health boost to player when drink is consumed
     * 
     * @param player The player consuming the drink
     * @param healthMultiplier The multiplier (e.g., 1.15 = 15% boost)
     */
    public static void applyHealthBoost(ServerPlayer player, float healthMultiplier) {
        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();
        
        // Calculate health to restore (e.g., max health * 1.15 - but cap at max)
        float boost = maxHealth * (healthMultiplier - 1.0f); // Just the boost amount
        float newHealth = Math.min(currentHealth + boost, maxHealth);
        
        player.setHealth(newHealth);
    }
}


// ============ MixologyRecipeHandler.java ============
// Location: src/main/java/com/mertie/mixology/recipe/MixologyRecipeHandler.java

package com.mertie.mixology.recipe;

import com.hypixel.hytale.server.core.item.ItemStack;
import java.util.Optional;

public class MixologyRecipeHandler {
    /**
     * Attempt to craft a drink from an input fruit item
     * 
     * @param inputItemId The fruit ID (e.g., "Apple")
     * @return ItemStack of the crafted drink, or null if no recipe found
     */
    public static ItemStack craft(String inputItemId) {
        Optional<Recipe> recipe = RecipeRegistry.getRecipe(inputItemId);
        
        if (recipe.isPresent()) {
            String drinkId = recipe.get().getOutputItemId();
            return new ItemStack(drinkId, 1); // Return 1 of the drink
        }
        
        return null; // No recipe found
    }
}


// ============ Integration in MixologyPlugin.java ============
// Add this to your existing MixologyPlugin.java setup() method:

/*

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("[Mixology] Plugin setup!");

        // Initialize recipes
        RecipeRegistry.initializeRecipes();

        // Register the /mixology command
        registerCommands();

        // TODO: Hook into bench crafting system
        // getCraftingSystem().registerBenchListener("Mixology_Bar", (player, input) -> {
        //     ItemStack result = MixologyRecipeHandler.craft(input.getId());
        //     if (result != null) {
        //         player.giveItem(result);
        //         player.playSound("alchemy_success"); // Optional
        //     }
        // });

        // TODO: Hook into drink consumption
        // getItemUseSystem().registerItemUseListener((player, item) -> {
        //     if (isDrinkItem(item.getId())) {
        //         Optional<Recipe> recipe = RecipeRegistry.getRecipe(item.getId());
        //         if (recipe.isPresent()) {
        //             DrinkEffect.applyHealthBoost(player, recipe.get().getHealthMultiplier());
        //             player.consumeItem(item, 1);
        //         }
        //     }
        // });
    }

    private boolean isDrinkItem(String itemId) {
        return itemId.contains("Martini") || 
               itemId.contains("Punch") || 
               itemId.contains("Cooler") || 
               itemId.contains("Sip");
    }

*/

// =============================================================================
// NOTES FOR IMPLEMENTATION
// =============================================================================

/*
1. The bench integration (getCraftingSystem()) may vary depending on Hytale's API.
   Check the official Hytale modding docs for exact method names.

2. Item consumption hooks might be "ItemUseEvent" or similar. Adjust based on
   the actual Hytale Server API.

3. The health boost applies a flat percentage of max health. Adjust if needed:
   - Currently: newHealth = currentHealth + (maxHealth * 0.15)
   - You could also do: newHealth = currentHealth * 1.15 (multiplicative)

4. Consider adding:
   - Cooldown on drink consumption (can't spam)
   - Particle effects when drinking
   - Custom sound effects
   - Chat message feedback

5. Test thoroughly:
   - Ensure recipes register on startup
   - Test crafting in the bench
   - Test drinking and health restoration
   - Test edge cases (already at max health, etc.)
*/
