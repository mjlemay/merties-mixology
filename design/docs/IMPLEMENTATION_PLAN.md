# Mixology Plugin - Drink Item & Recipe Implementation Plan

## Overview
Add 4-5 custom drink items (Apple Martini, Berry Punch, Corn Cooler, Autumn Sip) with recipe mappings and 1.15x health boost effects.

---

## Step-by-Step Implementation

### Phase 1: Create Drink Item JSONs (src/main/resources/Server/Item/Items/)
**Files to create:**
- `Apple_Martini.json`
- `Berry_Punch.json`
- `Corn_Cooler.json`
- `Autumn_Sip.json`

Each JSON defines:
- Display name/description
- Icon (unique per drink)
- Custom model (optional but recommended for visual flair)
- Consumable behavior (health restore on use)
- Stack size (small, e.g., 16)

**Template:** See `EXAMPLE_Apple_Martini.json` below.

---

### Phase 2: Create Recipe System (Java/Kotlin)
**Files to create:**
- `src/main/java/com/mertie/mixology/recipe/Recipe.java` - Data class
- `src/main/java/com/mertie/mixology/recipe/RecipeRegistry.java` - Recipe mappings & lookup
- `src/main/java/com/mertie/mixology/effect/DrinkEffect.java` - Health effect handler
- `src/main/java/com/mertie/mixology/MixologyRecipeHandler.java` - Bench integration

**Key classes:**

```java
// Recipe.java - Simple POJO
public class Recipe {
    private String inputItemId;      // "Apple", "Wild_Berry", etc.
    private String outputItemId;     // "Apple_Martini"
    private float healthMultiplier;  // 1.15f
}

// RecipeRegistry.java - Central recipe lookup
public class RecipeRegistry {
    private static final Map<String, Recipe> RECIPES = new HashMap<>();
    
    public static void register(Recipe recipe) { /* ... */ }
    public static Optional<Recipe> getRecipe(String inputItemId) { /* ... */ }
    public static void initializeRecipes() {
        register(new Recipe("Apple", "Apple_Martini", 1.15f));
        register(new Recipe("Wild_Berry", "Berry_Punch", 1.15f));
        // ... etc
    }
}

// DrinkEffect.java - Apply health on consume
public class DrinkEffect {
    public static void applyHealthBoost(Player player, float multiplier) {
        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float boost = maxHealth * multiplier;
        player.setHealth(Math.min(currentHealth + boost, maxHealth));
    }
}

// MixologyRecipeHandler.java - Bench hook
public class MixologyRecipeHandler {
    public static ItemStack craft(String inputItemId) {
        Optional<Recipe> recipe = RecipeRegistry.getRecipe(inputItemId);
        if (recipe.isPresent()) {
            return new ItemStack(recipe.get().getOutputItemId(), 1);
        }
        return null;
    }
}
```

---

### Phase 3: Integrate with Bench (Modify MixologyPlugin.java)
In `setup()` method:
1. Call `RecipeRegistry.initializeRecipes()`
2. Register a crafting listener on the Mixology_Bar bench
3. Hook the listener to `MixologyRecipeHandler.craft(inputItemId)`
4. When output is crafted, trigger `DrinkEffect.applyHealthBoost()` on consume

**Pseudocode:**
```java
@Override
protected void setup() {
    RecipeRegistry.initializeRecipes();
    
    // Listen for bench crafts
    getCraftingSystem().registerBenchListener("Mixology_Bar", (player, input) -> {
        ItemStack result = MixologyRecipeHandler.craft(input.getId());
        if (result != null) {
            player.giveItem(result);
        }
    });
    
    // Listen for drink consumption
    getItemUseSystem().registerItemUseListener((player, item) -> {
        if (isDrinkItem(item.getId())) {
            Recipe recipe = RecipeRegistry.getRecipe(item.getId());
            DrinkEffect.applyHealthBoost(player, recipe.getHealthMultiplier());
            item.consume(1); // Remove from inventory
        }
    });
    
    registerCommands();
}
```

---

### Phase 4: Asset Creation/Sourcing
**For each drink, you need:**

1. **Icon** (64x64 PNG)
   - `Icons/ItemsGenerated/apple_martini.png`
   - `Icons/ItemsGenerated/berry_punch.png`
   - etc.

2. **Custom Model (optional but recommended)** 
   - `Models/Items/apple_martini.blockymodel` (if using Hytale's Blocky format)
   - Or simpler: use a glass model with texture swaps
   - Texture: `BlockTextures/apple_martini.png`

3. **Update manifest.json** (if needed)
   - Ensure `IncludesAssetPack: true`

---

## Files to Create/Modify

### New Files:
```
src/main/java/com/mertie/mixology/recipe/
  ├── Recipe.java
  ├── RecipeRegistry.java
  └── MixologyRecipeHandler.java

src/main/java/com/mertie/mixology/effect/
  └── DrinkEffect.java

src/main/resources/Server/Item/Items/
  ├── Apple_Martini.json
  ├── Berry_Punch.json
  ├── Corn_Cooler.json
  └── Autumn_Sip.json

src/main/resources/Icons/ItemsGenerated/
  ├── apple_martini.png
  ├── berry_punch.png
  ├── corn_cooler.png
  └── autumn_sip.png

src/main/resources/BlockTextures/
  ├── apple_martini.png
  ├── berry_punch.png
  ├── corn_cooler.png
  └── autumn_sip.png

src/main/resources/Models/Items/  [OPTIONAL]
  ├── apple_martini.blockymodel
  ├── berry_punch.blockymodel
  └── ... etc
```

### Modified Files:
- `src/main/java/com/mertie/mixology/MixologyPlugin.java` - Add recipe & effect listeners

---

## Asset Requirements Checklist

- [ ] 4-5 drink icons (PNG, 64x64 minimum)
- [ ] 4-5 texture files for drinks (if using custom models)
- [ ] 4-5 custom models OR reuse generic potion bottle and retexture
- [ ] Source/create glass container visuals (martini glass, tiki mug, coupe, pint glass)

**Shortcut:** If model creation is time-consuming, you can use a generic "potion bottle" model with different textures. Focus on icons being visually distinct.

---

## Timeline Estimate

- Phase 1 (JSONs): 30 min
- Phase 2 (Code skeleton): 1 hour (you'll expand/refine)
- Phase 3 (Integration): 1 hour (debugging bench/effect hooks)
- Phase 4 (Assets): 2-3 hours (icons + basic models/textures)

**Total: ~5 hours** for a working MVP.

---

## Next Steps

1. Create the JSON files from the template below
2. Implement the Java classes (can start minimal, expand later)
3. Build and test the bench crafting
4. Create/source icons and models
5. Test potion consumption effect

Good luck! 🍹

---

*Last updated: 2026-03-26*
