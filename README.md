# MERTIE'S MIXOLOGY

A Hytale mod for creating custom cocktails and mixology workstations.

## Content

- **Mixology Bar** — a craftable bench (6 Wood Trunk + 3 Rock at a Workbench) with its own
  "Drinks" crafting category
- **Mixology Crate** — a decorative storage crate for your finest beverages
- **Four signature cocktails**, each crafted at the Mixology Bar from one produce item plus a
  Life Essence, served in unique glassware:
  - **Apple Martini** — crisp apple cocktail in a martini glass
  - **Autumn Sip** — warm pumpkin beverage in a rustic pint glass
  - **Berry Punch** — vibrant berry punch in a solo cup
  - **Corn Cooler** — smooth corn beverage in a coupe glass

Each drink restores health on the spot, with a lingering stamina buff and a mild kick of
poison — drink responsibly.

## Building

```bash
./gradlew build
```

The jar is built to `build/libs/merties_mixology-<version>.jar`. The version is set in
`gradle.properties`.

## Deploying locally

```bash
./deploy.sh
```

This builds the mod, removes any older jars from the Hytale Mods folder
(`~/Library/Application Support/Hytale/UserData/Mods/`), and copies the fresh jar in.

## Development

This project is built on the [Hytale Gradle Plugin](https://github.com/AzureDoom/Hytale-Gradle-Plugin)
(`com.azuredoom.hytale-tools`), which handles manifest generation, validation, local server runs,
and IDE source setup. It requires Java 25; JetBrains Runtime is recommended for the best
hot-reload/debugging experience.

```bash
# Sync/setup the local Hytale development environment
./gradlew setupHytaleDev

# Run the local Hytale server
./gradlew runServer

# Run the server with debugging and hot swap enabled
./gradlew runServer -Ddebug=true -Dhotswap=true

# Check your JVM and hot swap setup
./gradlew hytaleJvmDoctor

# Refresh dependencies if something fails to resolve
./gradlew build --refresh-dependencies
```

Mod metadata lives in `gradle.properties` and is compiled into the generated `manifest.json`
at build time (`manifest_group`, `mod_id`, `version`, `mod_description`, `mod_author`,
`mod_url`, `main_class`, dependencies, and the targeted server version). After changing these
values, run `./gradlew updatePluginManifest`.

## Project structure

```text
src/main/java/        Plugin source code
src/main/resources/   Mod assets (Common/ and Server/) and generated manifest.json
gradle.properties     Mod metadata and version
build.gradle.kts      Gradle build and Hytale Gradle Plugin configuration
settings.gradle.kts   Plugin repositories and project name
deploy.sh             Build + deploy to the local Hytale Mods folder
```

## Project history

This mod was originally developed as `MertiesMixologyPlugin` and migrated to the
HytaleModding plugin-template in v0.3.0. The final legacy state (v0.2.5) is preserved on the
`legacy-alpha` branch. See `CHANGELOG.md` for the full history.

## Troubleshooting

- **Gradle sync fails in IntelliJ** — Check that Java 25 is installed and configured under
  **File → Project Structure → SDKs**.
- **The Hytale Gradle Plugin does not resolve** — Make sure `settings.gradle.kts` includes the
  AzureDoom Maven repository at `https://maven.azuredoom.com/mods`.
- **Build fails with missing dependencies** — Run `./gradlew build --refresh-dependencies` and make
  sure you have internet access.
- **The mod fails asset validation on load** — Every path referenced by a JSON in
  `src/main/resources` must exist in the pack; one dangling reference fails the whole mod.
  Check the client log in `~/Library/Application Support/Hytale/UserData/Logs/` for `FAIL:` lines.
- **Permission denied on `./gradlew`** — Run `chmod +x gradlew` on macOS/Linux.

## Resources

- [Hytale Gradle Plugin](https://github.com/AzureDoom/Hytale-Gradle-Plugin)
- [Hytale Modding Guides](https://hytalemodding.dev)
- [Hytale Modding Discord](https://discord.gg/hytalemodding)
