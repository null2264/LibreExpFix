import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("dev.architectury.loom") version "1.4-SNAPSHOT"
    id("com.github.johnrengelman.shadow")
    id("me.modmuss50.mod-publish-plugin") version "0.3.5"
}

operator fun Project.get(property: String): String {
    return property(property) as String
}

val _env = System.getenv()

version = project["mod_version"]
group = project["maven_group"]
val cfGameVersion = project["minecraft_version"]

// TODO: For later
val isForge = project.name.endsWith("forge")
val isNeo = project.name.endsWith("neoforge")
val isFabric = project.name.endsWith("fabric")

repositories {
}

dependencies {
    minecraft("com.mojang:minecraft:${project["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${project["yarn_mappings"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project["loader_version"]}")
}

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    isZip64 = true
    exclude("fabric.mod.json")
    //exclude(if (isFabric) "META-INF/mods.toml" else "fabric.mod.json")
    exclude("architectury.common.json")

    archiveClassifier.set("dev-shade")
}

artifacts {
    archives(shadowJar.get())
}

val remapJar = tasks.named<RemapJarTask>("remapJar") {
    dependsOn(shadowJar.get())
    inputFile.set(shadowJar.get().archiveFile)
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    inputs.property("version", project.version)

    from(sourceSets["main"].resources.srcDirs) {
        include("fabric.mod.json")
        expand(mutableMapOf("version" to project.version))
    }

    from(sourceSets["main"].resources.srcDirs) {
        exclude("fabric.mod.json")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
}

java {
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project["archivesBaseName"]}" }
    }
}

publishMods {
    file = remapJar.get().archiveFile
    displayName = "v${project.version}-BETA"
    changelog = _env["CHANGELOG"] ?: "The changelog can be found at https://github.com/null2264/LibreExpFix/commits/"
    version = project.version as String
    if (isFabric) {
        modLoaders.add("fabric")
        modLoaders.add("quilt")
    } else {
        modLoaders.add("forge")
        modLoaders.add("neoforge")
    }

    type = BETA

    _env["CURSEFORGE"]?.also { token ->
        curseforge {
            accessToken = token
            projectId = project["curseforge_project"]

            /* TODO
            for (final def mcVer in mcReleaseVersions) {
                minecraftVersions.add(mcVer)
            }
             */
        }
    }

    _env["MODRINTH"]?.also { token ->
        modrinth {
            accessToken = token
            projectId = project["modrinth_project"]

            /* TODO
            for (final def mcVer in mcReleaseVersions) {
                minecraftVersions.add(mcVer)
            }
             */
        }
    }
}