#!/bin/bash

# Build the plugin
echo "Building Mixology Plugin..."
./gradlew build

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful!"

    # Copy to Hytale mods folder
    echo "Copying to Hytale mods folder..."
    cp build/libs/MertiesMixologyPlugin-1.0.0.jar ~/Library/Application\ Support/Hytale/UserData/mods/

    if [ $? -eq 0 ]; then
        echo "✓ Plugin deployed successfully to ~/Library/Application Support/Hytale/UserData/mods/"
    else
        echo "✗ Failed to copy plugin to mods folder"
        exit 1
    fi
else
    echo "✗ Build failed"
    exit 1
fi
