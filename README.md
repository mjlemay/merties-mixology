# Merties Mixology Plugin

A Hytale plugin for creating custom cocktails and mixology workstations.

## Building

```bash
./gradlew build
```

The built JAR will be in `build/libs/MertiesMixologyPlugin-1.0.0.jar`

## Installation

Copy the JAR file to your Hytale server's Mods directory:
```bash
cp build/libs/MertiesMixologyPlugin-1.0.0.jar "$HOME/Library/Application Support/Hytale/UserData/Mods/"
```

## Commands

- `/mixology` - Display plugin information

## Development

This plugin uses:
- Java 21
- Gradle for building
- Hytale Server API

## Resources

- [Hytale Modding Documentation](https://hytalemodding.dev)
- [Hytale Template Plugin](https://github.com/realBritakee/hytale-template-plugin)
