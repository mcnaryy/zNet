plugins {
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "8.3.0"
    id("java")
}

group = "net.hellz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.hypera.dev/snapshots/")
    maven("https://reposilite.worldseed.online/public")

}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.spongepowered:configurate-hocon:3.7.2") // configuration using hocon

    // Minestom Library
    compileOnly("net.minestom:minestom-snapshots:c96413678c")
    implementation("net.minestom:minestom-snapshots:c96413678c")

    // Reflections library
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10") // Kotlin reflection

    // Imperat Library (Commands)
    implementation("dev.velix:imperat-core:1.5.1")
    implementation("dev.velix:imperat-bukkit:1.5.1")
    implementation("dev.velix:imperat-minestom:1.5.1")


    // SLF4J for logging
    implementation("org.slf4j:slf4j-simple:2.0.14")

    // LuckPerms Implementation
    implementation("dev.lu15:luckperms-minestom:5.4-SNAPSHOT")

    // WorldSeedEntity
    implementation("net.worldseed.multipart:WorldSeedEntityEngine:11.2.5")
    // Atlas Projectiles
    implementation("ca.atlasengine:atlas-projectiles:2.1.1")




}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

