# Asset Pack Structure

This plugin uses a combined Plugin + Asset Pack structure.

## Folder Layout

```
src/main/resources/
├── manifest.json (IncludesAssetPack: true)
├── Icons/                       # Icons at ROOT (not in Common/)
│   ├── ItemsGenerated/          # Generated inventory icons
│   └── Items/                   # Custom item icons
├── Common/                      # Client & Server assets
│   ├── Models/
│   │   └── Items/               # .blockymodel files for items
│   ├── Blocks/
│   │   └── mixology_crate/        # Block models (optional)
│   ├── Textures/
│   │   └── Items/               # .png texture files for items
│   ├── BlockTextures/           # .png texture files for blocks
│   └── UI/
│       └── Custom/
│           └── Pages/           # .ui files
└── Server/                      # Server-only assets
    ├── Languages/
    │   └── en-US/
    │       └── items.lang       # Item/block translations
    ├── Item/
    │   ├── Items/
    │   │   ├── Consumable/      # Food/drink items
    │   │   ├── Weapon/          # Weapon items
    │   │   └── [Blocks]         # Block definitions (.json)
    │   ├── Interactions/        # Item interaction definitions
    │   └── RootInteractions/    # Root interaction definitions
    └── Drops/                   # Loot table definitions
```

**Important**:
- Icons/ must be at the ROOT (sibling to Common/, not inside it)
- Common/ and Server/ folders must be at the ROOT of src/main/resources/

## Resulting JAR Structure

When built, the JAR will contain:
```
MertiesMixologyPlugin-1.0.0.jar
├── manifest.json
├── com/mertie/mixology/          # Compiled Java classes
├── Icons/                        # Icons at root level
│   └── ItemsGenerated/
├── Common/                       # Client & Server assets
│   └── BlockTextures/
└── Server/                       # Server-only assets
    ├── Item/Items/
    └── Languages/
```

## Adding New Items

1. Create item definition in `Server/Item/Items/<Category>/YourItem.json`
2. Add texture in `Common/Textures/Items/your_item.png`
3. (Optional) Add model in `Common/Models/Items/your_item.blockymodel`

## Adding New Blocks

1. Create block definition in `Server/Item/Items/Your_Block.json`
2. Add translation in `Server/Languages/en-US/items.lang`
3. Add texture in `Common/BlockTextures/your_block.png`
4. Add icon in `Icons/ItemsGenerated/your_block.png` (at root, not in Common/)
5. (Optional) Add model in `Common/Blocks/your_block/model.blockymodel`

## Adding Workbenches and Recipes

### Creating a Workbench
1. Create block definition with `"Interaction": { "Type": "Crafting", "CraftingStation": "Your_Station_Name" }`
2. Set `"DrawType": "Model"` and reference existing model (e.g., `"Model": "Blocks/Items/Crafting_Table"`)

### Creating Recipes
Add a `Recipe` object to items that should be craftable:
```json
"Recipe": {
  "CraftingStation": "Your_Station_Name",
  "Ingredients": [
    { "ItemId": "Item_Name", "Count": 4 }
  ],
  "Output": {
    "ItemId": "Your_Item",
    "Count": 1
  }
}
```

## Examples

### Mixology Bar (Workbench)
- Definition: `Server/Item/Items/Mixology_Bar.json`
- Translation: `Server/Languages/en-US/items.lang`
- Category: `Blocks.Crafting` (appears with other workbenches)
- Uses placeholder Crafting Table model and icon
- Crafting station ID: `"Mixology_Bar"`

### Mixology Crate (Decorative Block)
- Definition: `Server/Item/Items/Mixology_Crate.json`
- Translation: `Server/Languages/en-US/items.lang`
- Texture: `Common/BlockTextures/mixology_crate_*.png` (multiple sides)
- Icon: `Icons/ItemsGenerated/mixology_crate.png` (copied from texture)
- Available in creative mode (Blocks > Deco)

### Asset References in JSON
In `Mixology_Crate.json`, assets are referenced with full paths including extensions:
```json
"Icon": "Icons/ItemsGenerated/mixology_crate.png"
"Textures": [
  {
    "Up": "BlockTextures/mixology_crate_top.png",
    "Down": "BlockTextures/mixology_crate_bottom.png",
    "North": "BlockTextures/mixology_crate_side.png",
    "South": "BlockTextures/mixology_crate_side.png",
    "East": "BlockTextures/mixology_crate_side.png",
    "West": "BlockTextures/mixology_crate_side.png"
  }
]
```

**Texture Options**:
- `"All": "path.png"` - Same texture on all 6 faces
- **3-way (shown above)**: Use `Up`/`Down` + all 4 cardinal directions with same texture for sides
- **6-way**: `"North"/"South"/"East"/"West"/"Up"/"Down"` - Individual texture for each face

**Property Names**:
- Use `Up` and `Down` (NOT "Top" and "Bottom")
- Use `North`, `South`, `East`, `West` for sides
- Use `All` for uniform textures

**Important**:
- Icon paths DO include `.png` extension
- Texture paths (BlockTextures) DO include `.png` extension
- Texture paths do NOT include `Common/` prefix
- Icon paths do NOT include `Common/` prefix (Icons/ is in Common/)

---

## Complete Schema Reference

### Item Schema

All possible fields for item definitions in `Server/Item/Items/`:

```json
{
    "Identifier": "string (required - matches filename)",
    "Parent": "string (optional - inherits from another Item)",

    "DisplayName": "string",
    "Description": "string",
    "Category": "string (Category asset ID)",
    "Quality": "string (Quality asset ID)",
    "Icon": "string (texture path)",

    "MaxStack": "integer (default 100)",
    "Unique": "boolean (if true, MaxStack=1)",

    "Interactions": {
        "Primary": "string (RootInteraction ID)",
        "Secondary": "string (RootInteraction ID)",
        "Ability1": "string (RootInteraction ID)",
        "Ability2": "string (RootInteraction ID)"
    },

    "InteractionVars": {
        "VarName": {
            "Interactions": [{
                "Parent": "string (Interaction ID to override)",
                "DamageCalculator": { },
                "...other fields to override..."
            }]
        }
    },

    "Recipe": {
        "TimeSeconds": "number",
        "Input": [
            { "ItemId": "string", "Quantity": "integer" },
            { "ResourceTypeId": "string", "Quantity": "integer" }
        ],
        "BenchRequirement": [{
            "Type": "Crafting|Smelting|Cooking",
            "Categories": ["string"],
            "Id": "string (bench Item ID)"
        }]
    },

    "BlockType": { "...see BlockType section..." },

    "EquipmentSlot": "Head|Chest|Legs|Feet|MainHand|OffHand",
    "ArmorValue": "integer",
    "Durability": "integer",

    "Consumable": {
        "EntityEffects": [{ "EffectId": "string", "Duration": "number" }],
        "RestoreHealth": "number",
        "RestoreHunger": "number"
    }
}
```

### BlockType Schema

All possible fields for the `BlockType` object within item definitions:

```json
{
    "Material": "Solid|Transparent|Liquid|Vegetation|Model",
    "DrawType": "Cube|Cross|Model|Custom",
    "Group": "string (for texture transitions)",

    "Textures": [{
        "All": "string (applies to all faces)",
        "Top": "string",
        "Bottom": "string",
        "North": "string",
        "South": "string",
        "East": "string",
        "West": "string",
        "Weight": "integer (for random variation)"
    }],

    "TransitionTexture": "string (edge blending texture)",
    "TransitionToGroups": ["string (groups to blend with)"],
    "Model": "string (blockymodel path)",
    "ParticleColor": "#hex",
    "Tint": "#hex",

    "Collidable": "boolean (default true)",
    "HitboxType": "Full|None|Custom",
    "CustomHitbox": { "Min": [x,y,z], "Max": [x,y,z] },

    "BlockSoundSetId": "string",
    "BlockBreakingDecalId": "string",

    "Gathering": {
        "Breaking": {
            "GatherType": "string",
            "Tool": "Pickaxe|Axe|Shovel|None",
            "Hardness": "number",
            "DropListId": "string (ItemDropList)"
        }
    },

    "State": {
        "Default": "string (default state name)",
        "Definitions": {
            "StateName": {
                "HitboxType": "string",
                "InteractionHitboxType": "string",
                "InteractionSoundEventId": "string",
                "CustomModelAnimation": "string"
            }
        }
    },

    "Interactions": {
        "Use": "string (Interaction ID)"
    },

    "ConnectedBlockRuleSet": {
        "Type": "CustomTemplate",
        "TemplateShapeAssetId": "string"
    },

    "VariantRotation": "None|NESW|Full",
    "IsDoor": "boolean",
    "IsLadder": "boolean",
    "LightLevel": "integer (0-15)",
    "Flammable": "boolean",
    "BlastResistance": "number"
}
```

### Key Notes

- **For Models**: Use `"Material": "Model"` and `"DrawType": "Model"` with a `"Model"` path
- **For Cube Blocks**: Use `"Material": "Solid"` and `"DrawType": "Cube"` with `"Textures"` array
- **Texture Paths**: Always include file extension (`.png`) and use relative paths without `Common/` prefix
- **Icon Paths**: Reference from root (e.g., `Icons/ItemsGenerated/item.png`)
