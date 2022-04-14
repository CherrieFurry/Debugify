plugins {
    id("com.github.johnrengelman.shadow") version "7.+"
}

base.archivesName.set("debugify-gameplay-${project.name}")

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    //accessWidenerPath.set(project(":gameplay:common").loom.accessWidenerPath)
}

val common by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentFabric"].extendsFrom(this)
}
val shadowCommon by configurations.creating

dependencies {
    val fabricLoaderVersion: String by rootProject
    val clothVersion: String by rootProject

    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    common(project(path = ":gameplay:common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":gameplay:common", configuration = "transformProductionFabric")) { isTransitive = false }

    "com.github.llamalad7:mixinextras:0.0.+".let {
        implementation(it)
        annotationProcessor(it)
    }

    modImplementation("me.shedaniel.cloth:cloth-config-fabric:$clothVersion") {
        exclude(module = "fabric-api")
    }

    modImplementation("com.terraformersmc:modmenu:3.+")
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version
            )
        }
    }

    shadowJar {
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
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
