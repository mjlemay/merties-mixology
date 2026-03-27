package com.mertie.mixology;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.mertie.mixology.recipe.RecipeRegistry;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * Merties Mixology Plugin
 * 
 * Adds mixology workbenches and custom cocktail crafting to Hytale.
 * Features:
 * - Custom drink items with unique glassware
 * - Fruit-to-drink recipe mappings
 * - Health restoration effects on consumption
 *
 * @author Mertie
 * @version 1.0.0
 */
public class MixologyPlugin extends JavaPlugin {

    private static MixologyPlugin instance;

    public MixologyPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        getLogger().at(Level.INFO).log("[Mixology] Plugin instantiated!");
    }

    public static MixologyPlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("[Mixology] Plugin setup!");

        // Initialize all recipes (fruit -> drink mappings)
        try {
            RecipeRegistry.initializeRecipes();
            getLogger().at(Level.INFO).log("[Mixology] Initialized " + RecipeRegistry.getRecipeCount() + " recipes");
        } catch (Exception e) {
            getLogger().at(Level.SEVERE).log("[Mixology] Failed to initialize recipes: " + e.getMessage());
            e.printStackTrace();
        }

        // Register commands
        registerCommands();

        // TODO: Hook into bench crafting system
        // The exact implementation depends on Hytale's CraftingSystem API
        // Example pattern (adjust based on actual API):
        // 
        // getCraftingSystem().registerBenchListener("Mixology_Bar", (player, input) -> {
        //     ItemStack result = MixologyRecipeHandler.craft(input.getId());
        //     if (result != null) {
        //         player.giveItem(result);
        //         player.playSound("alchemy_success");
        //     }
        // });

        // TODO: Hook into drink consumption
        // The exact implementation depends on Hytale's ItemUseSystem API
        // Example pattern (adjust based on actual API):
        //
        // getItemUseSystem().registerItemUseListener((player, item) -> {
        //     if (isDrinkItem(item.getId())) {
        //         RecipeRegistry.getRecipe(item.getId()).ifPresent(recipe -> {
        //             DrinkEffect.applyHealthBoost(player, recipe.getHealthMultiplier());
        //             player.consumeItem(item, 1);
        //         });
        //     }
        // });
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("[Mixology] Plugin enabled!");
    }

    @Override
    public void shutdown() {
        getLogger().at(Level.INFO).log("[Mixology] Plugin disabled!");
        RecipeRegistry.clear();
    }

    private void registerCommands() {
        getCommandRegistry().registerCommand(new MixologyCommand());
    }

    /**
     * Check if an item ID represents a drinkable item.
     * Useful for filtering in event listeners.
     *
     * @param itemId The item ID to check
     * @return True if the item is a mixology drink
     */
    private boolean isDrinkItem(String itemId) {
        return itemId != null && (
            itemId.contains("Martini") ||
            itemId.contains("Punch") ||
            itemId.contains("Cooler") ||
            itemId.contains("Sip")
        );
    }
}
