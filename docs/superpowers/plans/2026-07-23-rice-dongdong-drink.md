# Rice Dongdong Drink Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add Rice Dongdong, a dual-wielded (cup in each hand) consumable rice drink that renders as a bottle+cup serving set when placed, per the approved spec at `docs/superpowers/specs/2026-07-23-rice-dongdong-drink-design.md`.

**Architecture:** Pure JSON asset work in a Hytale mod (no Java). Three spike tests prove two unproven engine behaviors on a throwaway item, then the real item is built with placeholder art, then final user-authored art is swapped in. Every deploy uses `./deploy.sh`; every launch is verified via the client log and in-game by the user.

**Tech Stack:** Hytale asset JSON (`Server/Item/Items/`), blockymodel references, Gradle via `./deploy.sh`.

**CRITICAL CONTEXT for the executor:**
- Working dir: `/Users/mertie/Developer/hytale/merties-mixology`, branch `dev`. Commit after each task. NEVER push without the user's explicit OK.
- You cannot launch Hytale or see the game. Every step marked **USER CHECKPOINT** means: stop, tell the user exactly what to verify in-game, and wait for their answer before continuing.
- Asset validation is all-or-nothing: ONE dangling path reference in any JSON fails the entire mod ("Failed to connect to server"). Every path you reference must exist in `src/main/resources/` or vanilla.
- Log check after every deploy+launch: the client log lives in `~/Library/Application Support/Hytale/UserData/Logs/`, newest `*_client.log`. A `FAIL:` line names the exact missing/broken asset.
- The spike decision matrix is in the spec. If spike 2 fails → STOP, switch to the two-item fallback (consult user). If spike 1 fails → STOP, regroup with user.

**File map:**

| File | Role |
|------|------|
| `src/main/resources/Server/Item/Items/Spike_Dongdong.json` | Throwaway spike item (created Task 1, deleted Task 4) |
| `src/main/resources/Server/Item/Items/Rice_Dongdong.json` | The real drink (created Task 5) |
| `docs/superpowers/specs/2026-07-23-rice-dongdong-drink-design.md` | Spec — spike results get recorded here (Task 4) |
| `Common/Items/cocktail_rice_dongdong_cup.blockymodel` etc. | Final art, user-authored (Task 6) |

Placeholder assets already in the pack (verified present): `Common/Items/cocktail_berry_solo_cup.blockymodel`, `Common/BlockTextures/cocktail_solo_cup.png`, `Common/Items/cocktail_martini.blockymodel`, `Common/BlockTextures/cocktail_martini.png`, `Common/Icons/ItemsGenerated/cocktail_berry_punch.png`.

---

### Task 1: Spike 1 — dual-wield flag on a consumable

**Files:**
- Create: `src/main/resources/Server/Item/Items/Spike_Dongdong.json`

- [ ] **Step 1: Create the spike item JSON**

Write exactly this content to `src/main/resources/Server/Item/Items/Spike_Dongdong.json`:

```json
{
  "TranslationProperties": {
    "Name": "Spike Dongdong",
    "Description": "Throwaway spike test item. Delete before ship."
  },
  "Parent": "Template_Food",
  "Icon": "Icons/ItemsGenerated/cocktail_berry_punch.png",
  "Categories": [
    "Items.Foods"
  ],
  "Quality": "Common",
  "MaxStack": 16,
  "Consumable": true,
  "PlayerAnimationsId": "Daggers",
  "Model": "Items/cocktail_berry_solo_cup.blockymodel",
  "Texture": "BlockTextures/cocktail_solo_cup.png",
  "Weapon": {
    "RenderDualWielded": true
  },
  "InteractionVars": {
    "Consume_Charge": {
      "Interactions": [
        {
          "Parent": "Consume_Charge_Food_T1_Inner",
          "Effects": {
            "ItemAnimationId": "Consume"
          }
        }
      ]
    },
    "RemoveEffect": {
      "Interactions": [{ "Type": "Simple" }]
    },
    "Effect": {
      "Interactions": [
        {
          "Type": "ApplyEffect",
          "EffectId": {
            "StatModifiers": { "Health": 25 },
            "StatusEffectIcon": "UI/StatusEffects/Health_Potion.png"
          }
        }
      ]
    }
  }
}
```

Notes: no `Recipe` (obtain via creative inventory), no `BlockType` (spike 1 isolates dual-wield only), single effect (enough to prove consumption works).

- [ ] **Step 2: Deploy**

Run: `./deploy.sh`
Expected: ends with `✓ Deployed merties_mixology-0.3.0.jar to /Users/mertie/Library/Application Support/Hytale/UserData/Mods`

- [ ] **Step 3: USER CHECKPOINT — launch and verify**

Ask the user to launch Hytale, join their test world, then run the log check:

```bash
LOG=$(ls -t "$HOME/Library/Application Support/Hytale/UserData/Logs/"*_client.log | head -1); grep -E "FAIL:|validation FAILED" "$LOG" || echo "VALIDATION CLEAN"
```

Expected: `VALIDATION CLEAN`. If a `FAIL:` line appears, it names the broken reference — fix `Spike_Dongdong.json`, redeploy, relaunch.

Then ask the user to verify in-game (creative inventory → Foods → "Spike Dongdong"):
1. Holding it shows a solo cup in **both** hands
2. The two-handed (Daggers) hold pose looks acceptable
3. Holding right-click drinks it — consume animation plays, +25 health applies

- [ ] **Step 4: Contingency — if the Daggers pose breaks consumption**

If drinking fails or the animation is broken with `"PlayerAnimationsId": "Daggers"`, edit that line in `Spike_Dongdong.json` to `"PlayerAnimationsId": "Item"`, redeploy (`./deploy.sh`), and repeat the USER CHECKPOINT. Record which value worked — Task 5 uses it. If dual-hand rendering itself never appears with either value, STOP: spike 1 failed; consult the spec's decision matrix with the user.

- [ ] **Step 5: Commit**

```bash
git add src/main/resources/Server/Item/Items/Spike_Dongdong.json
git commit -m "spike: dual-wield flag on consumable (spike 1) — result in spec"
```

---

### Task 2: Spike 2 — hand Model + placed BlockType on one item

**Files:**
- Modify: `src/main/resources/Server/Item/Items/Spike_Dongdong.json` (full replace)

- [ ] **Step 1: Replace the spike JSON with the spike-2 shape**

Overwrite `src/main/resources/Server/Item/Items/Spike_Dongdong.json` with exactly:

```json
{
  "TranslationProperties": {
    "Name": "Spike Dongdong",
    "Description": "Throwaway spike test item. Delete before ship."
  },
  "Parent": "Template_Food",
  "Icon": "Icons/ItemsGenerated/cocktail_berry_punch.png",
  "Categories": [
    "Items.Foods"
  ],
  "Quality": "Common",
  "MaxStack": 16,
  "Consumable": true,
  "PlayerAnimationsId": "Item",
  "Model": "Items/cocktail_berry_solo_cup.blockymodel",
  "Texture": "BlockTextures/cocktail_solo_cup.png",
  "BlockType": {
    "CustomModel": "Items/cocktail_martini.blockymodel",
    "CustomModelTexture": [
      {
        "Texture": "BlockTextures/cocktail_martini.png",
        "Weight": 1
      }
    ],
    "BlockParticleSetId": "GlassSmall",
    "ParticleColor": "#f0ead6"
  },
  "InteractionVars": {
    "Consume_Charge": {
      "Interactions": [
        {
          "Parent": "Consume_Charge_Food_T1_Inner",
          "Effects": {
            "ItemAnimationId": "Consume"
          }
        }
      ]
    },
    "RemoveEffect": {
      "Interactions": [{ "Type": "Simple" }]
    },
    "Effect": {
      "Interactions": [
        {
          "Type": "ApplyEffect",
          "EffectId": {
            "StatModifiers": { "Health": 25 },
            "StatusEffectIcon": "UI/StatusEffects/Health_Potion.png"
          }
        }
      ]
    }
  }
}
```

Notes: `Weapon` removed (one unknown at a time), `PlayerAnimationsId` back to `"Item"`, hand model = solo cup, placed model = martini glass (deliberately different so the difference is unmistakable in-game).

- [ ] **Step 2: Deploy**

Run: `./deploy.sh`
Expected: `✓ Deployed ...` as in Task 1.

- [ ] **Step 3: USER CHECKPOINT — launch and verify**

Same log check command as Task 1 Step 3; expected `VALIDATION CLEAN`.

Ask the user to verify in-game:
1. Held: shows the **solo cup** (top-level Model wins in hand)
2. Placed on the ground: shows the **martini glass** (BlockType.CustomModel wins placed)
3. Breaking the placed block drops the item back

Record the result. If held and placed models are NOT different (e.g., hand shows the martini glass — BlockType overrides Model in hand), spike 2 FAILED → STOP and consult the user: the fallback is the two-item split per the spec's decision matrix, and Tasks 3+5 need rework before proceeding.

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/Server/Item/Items/Spike_Dongdong.json
git commit -m "spike: hand Model + placed BlockType coexistence (spike 2) — result in spec"
```

---

### Task 3: Spike 3 — everything combined

**Files:**
- Modify: `src/main/resources/Server/Item/Items/Spike_Dongdong.json`

- [ ] **Step 1: Add the dual-wield pieces to the spike-2 JSON**

Edit `src/main/resources/Server/Item/Items/Spike_Dongdong.json`: change the `PlayerAnimationsId` line to whichever value spike 1 recorded (`"Daggers"` if the pose worked, else `"Item"`), and insert the `Weapon` block immediately after the `Texture` line:

```json
  "Weapon": {
    "RenderDualWielded": true
  },
```

The file is now the spike-2 content plus these two changes — hand model, placed block, and dual-wield all on one item.

- [ ] **Step 2: Deploy**

Run: `./deploy.sh`
Expected: `✓ Deployed ...`

- [ ] **Step 3: USER CHECKPOINT — launch and verify the full chain**

Same log check; expected `VALIDATION CLEAN`. Ask the user to verify the full chain in-game:
1. Held: solo cup in **both hands**
2. Drink: animation + effect apply
3. Place: martini glass appears as the placed block
4. Break: drops the item

All four pass → the experimental one-item shape is proven; proceed to Task 4. Any failure → record which step broke and consult the user against the spec's decision matrix.

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/Server/Item/Items/Spike_Dongdong.json
git commit -m "spike: combined dual-wield + held/placed item (spike 3) — result in spec"
```

---

### Task 4: Record spike results, delete the spike item

**Files:**
- Modify: `docs/superpowers/specs/2026-07-23-rice-dongdong-drink-design.md` (spike ladder section)
- Delete: `src/main/resources/Server/Item/Items/Spike_Dongdong.json`

- [ ] **Step 1: Record outcomes in the spec**

In the spec's "Spike ladder" section, append a `**Results (YYYY-MM-DD):**` list stating pass/fail for each spike, the working `PlayerAnimationsId` value, and any surprises the user reported. Replace the "Record all spike outcomes in this doc when known" bullet with the actual results.

- [ ] **Step 2: Delete the spike item and redeploy**

```bash
rm src/main/resources/Server/Item/Items/Spike_Dongdong.json
./deploy.sh
```

Expected: `✓ Deployed ...` (the spike item is gone from the next launch's creative inventory).

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "spike: record results in spec, remove throwaway spike item"
```

---

### Task 5: Build Rice_Dongdong with placeholder art

**Files:**
- Create: `src/main/resources/Server/Item/Items/Rice_Dongdong.json`

- [ ] **Step 1: Create the real item JSON (placeholder models)**

Write exactly this to `src/main/resources/Server/Item/Items/Rice_Dongdong.json`. Use the `PlayerAnimationsId` value proven in the spikes (shown here as `"Daggers"` — substitute `"Item"` if that's what spike 1 recorded):

```json
{
  "TranslationProperties": {
    "Name": "Rice Dongdong",
    "Description": "A cloudy farmhouse rice brew with grains bobbing dong-dong on the surface. Farmers know it as Dongdongju."
  },
  "Parent": "Template_Food",
  "Icon": "Icons/ItemsGenerated/cocktail_berry_punch.png",
  "Categories": [
    "Items.Foods"
  ],
  "Quality": "Uncommon",
  "MaxStack": 16,
  "Consumable": true,
  "PlayerAnimationsId": "Daggers",
  "Model": "Items/cocktail_berry_solo_cup.blockymodel",
  "Texture": "BlockTextures/cocktail_solo_cup.png",
  "Weapon": {
    "RenderDualWielded": true
  },
  "InteractionVars": {
    "Consume_Charge": {
      "Interactions": [
        {
          "Parent": "Consume_Charge_Food_T1_Inner",
          "Effects": {
            "ItemAnimationId": "Consume"
          }
        }
      ]
    },
    "RemoveEffect": {
      "Interactions": [{ "Type": "Simple" }]
    },
    "Effect": {
      "Interactions": [
        {
          "Type": "ApplyEffect",
          "EffectId": {
            "StatModifiers": { "Health": 25 },
            "StatusEffectIcon": "UI/StatusEffects/Health_Potion.png"
          }
        },
        {
          "Type": "ApplyEffect",
          "EffectId": {
            "StatModifiers": { "Health": -1 },
            "Duration": 30,
            "DamageCalculatorCooldown": 2,
            "Debuff": true,
            "StatusEffectIcon": "UI/StatusEffects/Poison.png",
            "OverlapBehavior": "Extend"
          }
        },
        {
          "Type": "ApplyEffect",
          "EffectId": {
            "StatModifiers": { "Stamina": 5 },
            "Duration": 30,
            "DamageCalculatorCooldown": 2,
            "StatusEffectIcon": "UI/StatusEffects/Stamina.png",
            "OverlapBehavior": "Extend"
          }
        }
      ]
    }
  },
  "BlockType": {
    "CustomModel": "Items/cocktail_martini.blockymodel",
    "CustomModelTexture": [
      {
        "Texture": "BlockTextures/cocktail_martini.png",
        "Weight": 1
      }
    ],
    "BlockParticleSetId": "GlassSmall",
    "ParticleColor": "#f0ead6"
  },
  "Recipe": {
    "Input": [
      {
        "ItemId": "Plant_Crop_Rice_Item",
        "Quantity": 1
      },
      {
        "ItemId": "Ingredient_Life_Essence",
        "Quantity": 1
      }
    ],
    "BenchRequirement": [
      {
        "Type": "Crafting",
        "Id": "Mixology_Bar",
        "Categories": [
          "Mixology_Drinks"
        ]
      }
    ]
  }
}
```

This is the sibling effect profile (identical to `Apple_Martini.json`) plus the spike-proven `Model`/`Texture`/`Weapon`/`BlockType` shape and the rice recipe. Placeholders: solo cup (hand), martini glass (placed), berry punch icon.

- [ ] **Step 2: Deploy**

Run: `./deploy.sh`
Expected: `✓ Deployed ...`

- [ ] **Step 3: USER CHECKPOINT — full gameplay verification**

Same log check; expected `VALIDATION CLEAN`. Ask the user to verify:
1. Mixology Bar → Drinks tab lists **Rice Dongdong**
2. Crafts from 1 Rice + 1 Life Essence
3. Held: cup in both hands; drink applies all three effects (+25 HP, poison tick icon, stamina icon)
4. Places as the (placeholder) martini glass; breaks and drops itself
5. Name and description read correctly

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/Server/Item/Items/Rice_Dongdong.json
git commit -m "feat: add Rice Dongdong drink (dual-wield + placeable, placeholder art)"
```

---

### Task 6: Swap in final art (blocked on user-authored models)

**Files:**
- Create (user provides): `src/main/resources/Common/Items/cocktail_rice_dongdong_cup.blockymodel`
- Create (user provides): `src/main/resources/Common/Items/cocktail_rice_dongdong_set.blockymodel`
- Create (user provides): `src/main/resources/Common/BlockTextures/cocktail_rice_dongdong.png` (plus a set texture if the user makes one)
- Create (user provides): `src/main/resources/Common/Icons/ItemsGenerated/cocktail_rice_dongdong.png`
- Modify: `src/main/resources/Server/Item/Items/Rice_Dongdong.json`

- [ ] **Step 1: WAIT for the user's Blockbench exports**

This task is blocked until the user authors the cup model, set model, texture(s), and icon (art direction in the spec: milky body, floating rice grains; optional starting point `design/bbmodel/cocktail_rice_burn.blockymodel.bbmodel` on `legacy-alpha`). Ask where they've put the exports and copy them to the exact paths above.

- [ ] **Step 2: Point the JSON at the final assets**

In `src/main/resources/Server/Item/Items/Rice_Dongdong.json` replace:
- `"Icon": "Icons/ItemsGenerated/cocktail_berry_punch.png"` → `"Icon": "Icons/ItemsGenerated/cocktail_rice_dongdong.png"`
- `"Model": "Items/cocktail_berry_solo_cup.blockymodel"` → `"Model": "Items/cocktail_rice_dongdong_cup.blockymodel"`
- `"Texture": "BlockTextures/cocktail_solo_cup.png"` → `"Texture": "BlockTextures/cocktail_rice_dongdong.png"`
- `"CustomModel": "Items/cocktail_martini.blockymodel"` → `"CustomModel": "Items/cocktail_rice_dongdong_set.blockymodel"`
- BlockType `"Texture": "BlockTextures/cocktail_martini.png"` → the set's texture path (same `cocktail_rice_dongdong.png` if shared)

Before deploying, verify every referenced file exists:

```bash
cd src/main/resources && for f in Common/Icons/ItemsGenerated/cocktail_rice_dongdong.png Common/Items/cocktail_rice_dongdong_cup.blockymodel Common/Items/cocktail_rice_dongdong_set.blockymodel Common/BlockTextures/cocktail_rice_dongdong.png; do [ -f "$f" ] && echo "OK $f" || echo "MISSING $f"; done; cd ../../..
```

Expected: four `OK` lines. Any `MISSING` → do not deploy; get the file first.

- [ ] **Step 3: Deploy and USER CHECKPOINT**

Run: `./deploy.sh`, then the standard log check (`VALIDATION CLEAN`), then ask the user to confirm the final cup renders in both hands, the placed set looks right, and the icon shows in inventory and the Drinks tab.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: final Rice Dongdong art — cup, serving set, texture, icon"
```

---

### Task 7: Ship 0.4.0

**Files:**
- Modify: `gradle.properties` (line `version = 0.3.0`)
- Modify: `CHANGELOG.md`

- [ ] **Step 1: Bump the version**

In `gradle.properties`, change `version = 0.3.0` → `version = 0.4.0`.

- [ ] **Step 2: Add the changelog entry**

Insert at the top of the entries in `CHANGELOG.md` (above `## [0.3.0]`):

```markdown
## [0.4.0] - <today's date>

### Added
- Rice Dongdong — dual-wielded cloudy rice brew (a nod to Korean dongdongju): cup in each
  hand, places as a bottle+cup serving set, crafted from 1 Rice + 1 Life Essence
```

- [ ] **Step 3: Deploy and final USER CHECKPOINT**

Run: `./deploy.sh`
Expected: `✓ Deployed merties_mixology-0.4.0.jar ...` (note the new version). Standard log check, and ask the user for a final once-over in-game.

- [ ] **Step 4: Commit the release**

```bash
git add gradle.properties CHANGELOG.md
git commit -m "release: 0.4.0 — Rice Dongdong"
```

- [ ] **Step 5: Fast-forward main and tag (ASK USER before pushing)**

```bash
git switch main && git merge --ff-only dev && git switch dev
git tag -a v0.4.0 -m "v0.4.0 — Rice Dongdong"
```

Then ASK THE USER before running: `git push origin dev main v0.4.0`. Never push without their explicit OK.
