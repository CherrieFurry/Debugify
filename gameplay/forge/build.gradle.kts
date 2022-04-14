plugins {
    id("com.github.johnrengelman.shadow") version "7.+"
}

base.archivesName.set("debugify-${project.name}")

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    //accessWidenerPath.set(project(":gameplay:common").loom.accessWidenerPath)

    forge.apply {
        mixinConfig("debugify-gameplay-common.mixins.json")
        mixinConfig("debugify-gameplay.mixins.json")
        convertAccessWideners.set(true)
        //extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }
}

val common by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentForge"].extendsFrom(this)
}
val shadowCommon by configurations.creating

dependencies {
    val minecraftVersion: String by rootProject
    val forgeVersion: String by rootProject
    val clothVersion: String by rootProject

    forge("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

    common(project(path = ":gameplay:common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":gameplay:common", configuration = "transformProductionForge")) { isTransitive = false }


    "com.github.zafarkhaja:java-semver:0.9.+".let {
        implementation(it)
        forgeRuntimeLibrary(it)
        shadowCommon(it)
    }

    "com.github.llamalad7:mixinextras:0.0.+".let {
        forgeRuntimeLibrary(it)
        implementation(it)
        annotationProcessor(it)
    }

    modImplementation("me.shedaniel.cloth:cloth-config-forge:$clothVersion")
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("META-INF/mods.toml") {
            expand(
                "version" to project.version
            )
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
        archiveClassifier.set(null as String?)
    }

    jar {
        archiveClassifier.set("dev")
    }
}

components["java"].withGroovyBuilder {
    "withVariantsFromConfiguration"(configurations["shadowRuntimeElements"]) {
        "skip"()
    }
}

quiltflower {
    addToRuntimeClasspath.set(true)
}
