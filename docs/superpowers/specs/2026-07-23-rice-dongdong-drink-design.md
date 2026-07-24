# Rice Dongdong — Dual-Wielded Rice Drink

**Date:** 2026-07-23
**Status:** Approved design, pending spike results
**Target version:** 0.4.0

## Concept

**Rice Dongdong** is the fifth drink and the first special one: a cloudy Korean-style rice
brew (makgeolli/dongdongju family) held dagger-style with a cup in each hand. Placed down,
it renders as a serving set (bottle + cup). Crafted at the Mixology Bar from farmable rice.

- **Name:** Rice Dongdong (item id `Rice_Dongdong`)
- **Description:** "A cloudy farmhouse rice brew with grains bobbing dong-dong on the
  surface. Farmers know it as Dongdongju."

## Engine findings (verified against release Assets.zip, 6,893 item JSONs)

These facts constrain the design and are reusable knowledge for future drinks:

1. Dual-wield rendering is `"Weapon": { "RenderDualWielded": true }` — used only by the
   dagger family (`Template_Weapon_Daggers`). It duplicates **one** model into both hands
   (`PrimaryItem` / `SecondaryItem` entity parts).
2. **No per-hand model support exists.** No key in any vanilla JSON provides different
   left/right models. Bottle-in-left, cup-in-right is not possible; the compromise is the
   same cup model in both hands, with the bottle appearing in the placed form.
3. **No vanilla item has both a top-level hand `Model` and a `BlockType`.** Placeable
   items render their block `CustomModel` in hand. A single item with distinct held vs
   placed models is unproven — hence the spike ladder below.
4. Vanilla has rice: `Plant_Crop_Rice_Item` (`Server/Item/Items/Plant/Crop/Rice/`).
5. A full `Dual_Handed/Daggers` animation family exists, selected via
   `PlayerAnimationsId: "Daggers"`.

## Item data design (experimental one-item shape)

One JSON, `Server/Item/Items/Rice_Dongdong.json`, cloned from `Apple_Martini.json` plus:

- `"Model"`: hand-held cup blockymodel (top level, dagger-style) — duplicated into both hands
- `"Weapon": { "RenderDualWielded": true }` — nothing else in the Weapon block (no
  durability or combat fields)
- `"BlockType"`: `CustomModel` = bottle+cup set model; placed form. Breakable, drops
  itself, `GlassSmall` particles, milky `ParticleColor` (~`#f0ead6`)
- Unchanged from siblings: `Parent: Template_Food`, `Consumable: true`, `Categories:
  ["Items.Foods"]`, `Quality: Uncommon`, `MaxStack: 16`, the three-effect
  `InteractionVars` block, `Icons/ItemsGenerated/` icon

**Open question (spike 1 answers):** `PlayerAnimationsId` — try `"Daggers"` for the
two-handed pose and verify the consume animation still plays; fall back to `"Item"` if
they conflict.

## Spike ladder

Throwaway test item with placeholder assets (solo cup and martini models already in the
pack). Each spike: edit JSON → `./deploy.sh` → launch → check client log
(`~/Library/Application Support/Hytale/UserData/Logs/`) for `FAIL:` lines → verify
in-game. One unknown per spike:

| # | Unknown | Placeholders | Pass criteria |
|---|---------|--------------|---------------|
| 1 | `Consumable` + `Weapon.RenderDualWielded` | Solo-cup model as `Model` | Validates; cup renders in both hands; drinkable; effects apply |
| 2 | Hand `Model` + `BlockType.CustomModel` on one item | Cup in hand, martini glass as placed model | Validates; hand and placed models differ |
| 3 | Both combined | Same | Full chain: dual-held → drink → place → break |

**Decision matrix:**

- Spike 2 fails → fall back to **two items**: `Rice_Dongdong` drink (consumable,
  dual-wield, not placeable) + `Rice_Dongdong_Set` deco block (bottle+cup display,
  crate-style). The placed vision survives.
- Spike 1 fails → two-handed rendering is off the table engine-wide; regroup on design
  (single-hand cup like siblings, or hold for SDK updates).
**Results (2026-07-23) — PAUSED after spike 1, partial fail:**

- **Dual-hand rendering WORKS**: `Weapon.RenderDualWielded: true` on a `Template_Food`
  consumable renders the cup model in both hands. Spike 1's core render question passes.
- **Consume is BROKEN in that config**: with `Weapon` + `PlayerAnimationsId: "Daggers"`,
  right-click never drinks (no charge, no effects). The consume chain
  (`Root_Secondary_Consume_Food_T1`) appears to lose to the Weapon/daggers interaction
  setup. NOT yet isolated whether `Weapon` alone or the animation family is the blocker —
  a no-Weapon Torch build was deployed but its consume result never got recorded.
- **Item texture lesson**: a top-level item `Texture` must NOT point into `BlockTextures/`
  (atlas-packed → scrambled UVs). Item textures live under `Common/Items/` beside the
  model. A copy of the solo-cup texture now exists at `Common/Items/cocktail_solo_cup.png`.
- **Animation family tour** (with dual-render): `Daggers` = both hands raised, crouchy
  combat idle (best two-hand option); `Sword` = relaxed third-person but one-hand FPS;
  `Spellbook` = no visible pose change on this item (dud); `Torch` = primary arm raised
  only. No vanilla family gives relaxed + both-hands; only `Daggers` poses two hands.
- **Consume requires Adventure game mode** — `Condition_Consume_Food_T1` has
  `RequiredGameMode: "Adventure"`; in Creative, right-click falls through to block
  placement. Test drinking in Adventure, always.
- **Path forward (user)**: investigate custom player animations — a custom two-hand
  relaxed drinking set could solve both the pose and possibly the consume conflict.
  Resume by re-running the spike ladder from spike 1's consume isolation (test the
  no-Weapon build's consume, then Weapon + non-Daggers families), then spikes 2–3.
- Current state on disk: `Spike_Dongdong.json` still in the pack (Daggers + dual-wield
  config) and deployed locally for further experimentation.

## Final assets (user-authored in Blockbench, after spikes pass)

| Asset | Path |
|-------|------|
| Cup model | `Common/Items/cocktail_rice_dongdong_cup.blockymodel` |
| Set model | `Common/Items/cocktail_rice_dongdong_set.blockymodel` |
| Texture(s) | `Common/BlockTextures/cocktail_rice_dongdong.png` (+ set texture if separate) |
| Icon | `Common/Icons/ItemsGenerated/cocktail_rice_dongdong.png` |

Art direction: milky/cloudy body, floating rice grains on the surface (the dong-dong
detail). Optional starting point: `design/bbmodel/cocktail_rice_burn.blockymodel.bbmodel`
on the `legacy-alpha` branch.

## Recipe & effects

- **Recipe:** 1 `Plant_Crop_Rice_Item` + 1 `Ingredient_Life_Essence`, BenchRequirement
  `Mixology_Bar` / `Mixology_Drinks` — identical shape to siblings
- **Effects:** shared profile — +25 health instant; 30s −1 health poison tick
  (cooldown 2); 30s +5 stamina buff

## Testing

Per deploy: no `FAIL:` lines in the client log; mod loads. In-game: craftable at the
Mixology Bar Drinks tab; renders in both hands; consume plays animation and applies all
three effects; placeable; placed model is the set; breaking drops the item.

## Ship checklist

On `dev` throughout. When verified in-game: bump `gradle.properties` to 0.4.0, add
CHANGELOG entry, `./deploy.sh`, fast-forward `main`, tag `v0.4.0`.

## Out of scope

- Per-hand distinct models (engine limitation, documented above)
- New status effects or drunk mechanics (shared sibling effect profile only)
- Java/command work (deferred until SDK stabilizes, per project convention)
