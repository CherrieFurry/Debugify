pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://server.bbkr.space/artifactory/libs-release")
    }
}

rootProject.name = "Debugify"

include(":base:common", ":base:fabric", ":base:forge")
include(":gameplay:common", ":gameplay:fabric", ":gameplay:forge")
