import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

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
    compileOnly("net.minestom:minestom-snapshots:32735340d7")
    implementation("net.minestom:minestom-snapshots:32735340d7")

    // Reflections library
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10") // Kotlin reflection

    // SLF4J for logging
    implementation("org.slf4j:slf4j-simple:2.0.14")

    // LuckPerms Implementation
    implementation("dev.lu15:luckperms-minestom:5.4-SNAPSHOT")

    implementation("com.github.EmortalMC:Rayfast:1.0.0")

    // WorldSeedEntity
    implementation("net.worldseed.multipart:WorldSeedEntityEngine:11.2.5")

    // Lamp Command Framework (https://foxhut.gitbook.io/lamp-docs)
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.4")
    implementation("io.github.revxrsal:lamp.minestom:4.0.0-rc.4")


}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    compilerOptions {
        javaParameters = true
    }
    jvmToolchain(21)
}


tasks.withType<JavaCompile> {
    // Preserve parameter names in the bytecode
    options.compilerArgs.add("-parameters")
}

// optional: if you're using Kotlin
tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        javaParameters = true
    }
}