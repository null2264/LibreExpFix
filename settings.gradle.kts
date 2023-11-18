pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://jitpack.io/")
        maven("https://files.minecraftforge.net/maven/")
        maven("https://repo.essential.gg/repository/maven-public/")
        gradlePluginPortal()
    }
    plugins {
        id("com.github.johnrengelman.shadow") version("8.1.1")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.github.null2264.preprocess")
                useModule("com.github.null2264:Preprocessor:ba61239")
        }
    }
}

rootProject.name = "LibreExpFix"
rootProject.buildFileName = "root.gradle.kts"

listOf(
        "1.18-fabric",
        "1.18-forge",
        "1.20.2-fabric",
        "1.20.2-forge",
        "1.20.2-neoforge",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle"
    }
}