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
│   │   └── mojito_block/        # Block models (optional)
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

## Examples

### Mojito Block (Placeable Block)
- Definition: `Server/Item/Items/Mojito_Block.json`
- Translation: `Server/Languages/en-US/items.lang`
- Texture: `Common/BlockTextures/mojito_block.png`
- Icon: `Icons/ItemsGenerated/mojito_block.png` (at root!)

### Asset References in JSON
In `Mojito_Block.json`, assets are referenced with full paths including extensions:
```json
"Icon": "Icons/ItemsGenerated/mojito_block.png"
"Textures": [{ "All": "BlockTextures/mojito_block.png" }]
```

**Important**:
- Icon paths DO include `.png` extension
- Texture paths (BlockTextures) DO include `.png` extension
- Texture paths do NOT include `Common/` prefix
- Icon paths do NOT include `Common/` prefix (Icons/ is at root)
