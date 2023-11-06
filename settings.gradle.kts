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
}