plugins {
    id("java")
    id("net.fabricmc.fabric-loom") version("1.15-SNAPSHOT")
}

val modName: String by extra
val modFileName: String by extra
val modId: String by extra
val modAuthor: String by extra
val modGroup: String by extra
val modJavaVersion: String by extra
val minecraftVersion: String by extra
val minecraftVersionRange: String by extra
val fabricVersion: String by extra
val fabricLoaderVersion: String by extra
val jeiVersion: String by extra
val clothVersion: String by extra
val modmenuVersion: String by extra
val specificationVersion: String by extra
val githubUrl: String by extra

base {
    archivesName.set("${modFileName}-Fabric-${minecraftVersion}")
}

version = specificationVersion
group = modGroup

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

loom {
    accessWidenerPath.set(file("src/main/resources/jeresources.accesswidener"))
}

repositories {
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.blamejared.com/")
    maven("https://maven.parchmentmc.org/")
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    implementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
    implementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")

    // JEI
    compileOnly("mezz.jei:jei-${minecraftVersion}-fabric-api:${jeiVersion}")
    runtimeOnly("mezz.jei:jei-${minecraftVersion}-fabric:${jeiVersion}")

    // Cloth Config
    implementation("me.shedaniel.cloth:cloth-config-fabric:${clothVersion}") {
        exclude("net.fabricmc.fabric-api")
    }

    // ModMenu
    compileOnly("com.terraformersmc:modmenu:${modmenuVersion}")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(JavaLanguageVersion.of(modJavaVersion).asInt())
}

tasks.withType<ProcessResources> {
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand(
            "modId" to modId,
            "modName" to modName,
            "version" to version,
            "minecraftVersionRange" to minecraftVersionRange,
            "fabricLoaderVersion" to fabricLoaderVersion,
            "modJavaVersion" to modJavaVersion,
            "githubUrl" to githubUrl,
            "modAuthor" to modAuthor,
        )
    }
}

tasks.withType<Jar> {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}
