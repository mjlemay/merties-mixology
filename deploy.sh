#!/bin/bash
set -e

echo "Building Merties Mixology..."
./gradlew build

VERSION=$(grep '^version' gradle.properties | sed 's/version *= *//' | tr -d ' ')
JAR="build/libs/merties_mixology-${VERSION}.jar"
MODS="$HOME/Library/Application Support/Hytale/UserData/Mods"

echo "Deploying ${JAR}..."
# Remove any older versions (including legacy-named jars) so only one loads
rm -f "$MODS"/merties_mixology-*.jar "$MODS"/MertiesMixologyPlugin-*.jar
cp "$JAR" "$MODS/"

echo "✓ Deployed merties_mixology-${VERSION}.jar to $MODS"
