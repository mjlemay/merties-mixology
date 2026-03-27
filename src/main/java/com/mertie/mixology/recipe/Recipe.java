package com.mertie.mixology.recipe;

/**
 * Represents a recipe mapping from input fruit to output drink.
 * Stores the health multiplier for effect application.
 */
public class Recipe {
    private final String inputItemId;
    private final String outputItemId;
    private final float healthMultiplier;

    /**
     * Create a new recipe.
     *
     * @param inputItemId The fruit item ID (e.g., "Apple")
     * @param outputItemId The drink item ID (e.g., "Apple_Martini")
     * @param healthMultiplier The multiplier for health restoration (e.g., 1.15f for 15% boost)
     */
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

    @Override
    public String toString() {
        return String.format("Recipe{%s -> %s (%.2fx health)}", inputItemId, outputItemId, healthMultiplier);
    }
}
