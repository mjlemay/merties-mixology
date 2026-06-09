# File System Refactor — Phase 1

**Date:** 2026-06-08  
**Scope:** File/folder organization only. Build system alignment is Phase 2.

## Goal

Align the project structure with the `HytaleModding/plugin-template`, and consolidate design-time assets, planning files, and docs into a `design/` folder at the project root.

## New `design/` Folder

All non-source, non-functional files move here. Subfolders are typed.

```
design/
  bbmodel/
    cocktail_berry_solo_cup.bbmodel
    cocktail_corn_jar.bbmodel
    cocktail_pumkin.bbmodel
    cocktail_pumpkin_sip.blockymodel.bbmodel
    cocktail_rice_burn.blockymodel.bbmodel
    solo_cup.bbmodel
  docs/
    ASSETS_README.md              (from src/main/resources/)
    hytale-block-particle-sets.md (from docs/)
    IMPLEMENTATION_PLAN.md        (from root)
  psd/
    cocktail_martini.psd          (from src/main/resources/Common/BlockTextures/)
    mixology_crate_side.psd       (from src/main/resources/Common/BlockTextures/)
  reference/                      (from root reference/)
  heroimage.jpg                   (from root)
  heroimage.png                   (from root)
  EXAMPLE_Apple_Martini.json      (from root)
  OTHER_DRINKS.json               (from root)
  RECIPE_SYSTEM_SKELETON.java     (from root)
```

## Root After Refactor

Only functional build/runtime files remain at root:

```
.claude/
.github/workflows/gradle.yml   ← added from template
.gitignore
.vscode/
build.gradle.kts
CHANGELOG.md                   ← stays (referenced by build.gradle.kts CurseForge task)
deploy.sh
docs/superpowers/              ← stays (superpowers workflow expects this location)
gradle/
gradle.properties
gradlew
gradlew.bat                    ← added from template
libs/
package.json
README.md
settings.gradle.kts
src/
```

## Source Tree Changes

`src/` is otherwise untouched. Only these files are removed from it:

- `src/main/resources/Common/BlockTextures/cocktail_martini.psd` → `design/psd/`
- `src/main/resources/Common/BlockTextures/mixology_crate_side.psd` → `design/psd/`
- `src/main/resources/Common/Items/*.bbmodel` → `design/bbmodel/`
- `src/main/resources/ASSETS_README.md` → `design/docs/`

Runtime files (`.png`, `.blockymodel`, `.json`) stay in `src/main/resources/`.

## Files Added from Template

- `.github/workflows/gradle.yml` — CI pipeline
- `gradlew.bat` — Windows build wrapper

## Out of Scope (Phase 2)

- `build.gradle.kts` migration to `com.azuredoom.hytale-tools` plugin
- `gradle.properties` alignment to template format
- `libs/HytaleServer.jar` removal (replaced by hytale-tools dependency management)
- `manifest.json` auto-generation via hytale-tools
