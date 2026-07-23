# Changelog

All notable changes to Mertie's Mixology will be documented in this file.

## [0.3.0] - 2026-07-22

### Changed
- Migrated to the HytaleModding plugin-template boilerplate (hytale-tools Gradle plugin, generated manifest, Java 25)
- Renamed mod id and jar: `MertiesMixologyPlugin` → `merties_mixology`
- All content re-verified in-game after migration: Mixology Bar, Mixology Crate, and all four cocktails

### Removed
- Unfinished legacy Java recipe/effect layer (preserved on the `legacy-alpha` branch); the mod is fully JSON-driven

## [0.2.5] - 2026-07-16

### Changed
- Updated to Hytale 0.5.6 (server build 2026.06.17-5ea7c2639); verified the mod compiles against the 0.5 server API

## [0.2.0] - 2026-04-02

### Fixed
- Mixology Bar now appears in the Workbench crafting menu in adventure mode
- Moved bench definition to `Server/Item/Items/Bench/` to match vanilla bench registration
- Added recipe (6 Wood Trunk + 3 Rock) so the bench can be crafted at the Workbench
- Updated gather type from Wood to Benches
- Added crafting time, item level, and sound set to match vanilla bench schema

## [0.1.0] - 2026-01-28

### Added
- Mixology Bar bench with custom model and texture
- Mixology Drinks crafting category
- Apple Martini (health regen)
- Berry Punch (health regen)
- Corn Cooler (health regen)
- Autumn Sip (health regen)
- Mixology Crate decorative block
