base.archivesName.set("debugify-gameplay-${project.name}")

dependencies {
    val fabricLoaderVersion: String by rootProject
    val clothVersion: String by rootProject

    api(project(path = ":base:common", configuration = "namedElements"))

    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    implementation("com.github.llamalad7:mixinextras:0.0.+")
    annotationProcessor("com.github.llamalad7:mixinextras:0.0.+")

    modImplementation("me.shedaniel.cloth:cloth-config:$clothVersion")
}

architectury {
    common()
}

loom {
    //accessWidenerPath.set(file("src/main/resources/debugify-gameplay.accesswidener"))
}
