import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import com.modrinth.minotaur.TaskModrinthUpload
import com.modrinth.minotaur.request.VersionType

plugins {
    id("fabric-loom") version "1.0-SNAPSHOT"
    id("maven-publish")
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("com.modrinth.minotaur") version "1.2.1"
}

operator fun Project.get(property: String): String {
    return property(property) as String
}

val environment = System.getenv()

version = "${project["mod_version"]}-${project["version_type"]}"
group = project["maven_group"]
val cfGameVersion = project["minecraft_version"]
val releaseFile = "${buildDir}/libs/${base.archivesName.get()}-${version}.jar"
val releaseType = project["version_type"]

fun getChangeLog(): String {
    return "The changelog can be found at https://github.com/null2264/LibreExpFix/commits/"
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${project["yarn_mappings"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project["loader_version"]}")
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

curseforge {
    environment["CURSEFORGE_API_KEY"]?.let { apiKey = it }
    project(closureOf<CurseProject> {
        id = project["curseforge_id"]
        changelog = getChangeLog()
        releaseType = this@Build_gradle.releaseType.toLowerCase()
        addGameVersion(cfGameVersion)
        addGameVersion("Fabric")
        mainArtifact(file(releaseFile), closureOf<CurseArtifact> {
            displayName = version
            relations(closureOf<CurseRelation> {
                requiredDependency("fabric-api")
            })
        })
        afterEvaluate {
            uploadTask.dependsOn("remapJar")
        }
    })
    options(closureOf<Options> {
        forgeGradleIntegration = false
    })
}

task<TaskModrinthUpload>("modrinth") {
    dependsOn(tasks.remapJar)
    group = "upload"

    onlyIf {
        environment.containsKey("MODRINTH_TOKEN")
    }
    token = environment["MODRINTH_TOKEN"]

    projectId = project["modrinth_id"]
    changelog = getChangeLog()

    versionNumber = version as String
    versionName = version as String
    versionType = VersionType.valueOf(releaseType)

    uploadFile = file(releaseFile)

    addGameVersion(project["minecraft_version"])
    addLoader("fabric")
}
