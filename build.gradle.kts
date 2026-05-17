plugins {
    id("java")
    id("net.darkhax.curseforgegradle") version "1.1.25"
}

group = "com.mertie.mixology"
version = "0.2.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("libs/HytaleServer.jar"))
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// --- CurseForge publish task ---
// Usage: CURSEFORGE_API_KEY=your-token ./gradlew build publishCurseForge

tasks.register("publishCurseForge", net.darkhax.curseforgegradle.TaskPublishCurseForge::class) {
    dependsOn(tasks.jar)

    doFirst {
        if (System.getenv("CURSEFORGE_API_KEY").isNullOrBlank()) {
            throw GradleException("Set CURSEFORGE_API_KEY environment variable before publishing")
        }
    }

    apiToken = System.getenv("CURSEFORGE_API_KEY") ?: ""

    val jarFile = tasks.jar.get().archiveFile

    upload(1497008, jarFile) {
        releaseType = "release"
        changelog = file("CHANGELOG.md").readText()
        changelogType = "markdown"
        addGameVersion("Hytale")
    }
}
