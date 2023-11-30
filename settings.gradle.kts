pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.aap.my.id/")
        maven("https://maven.architectury.dev/")
        maven("https://jitpack.io/")
        maven("https://files.minecraftforge.net/maven/")
        maven("https://repo.essential.gg/repository/maven-public/")
    }
    plugins {
        id("com.github.johnrengelman.shadow") version("8.1.1")
        id("io.github.null2264.preprocess") version("1.0-SNAPSHOT")
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
