group = "me.gilbertodamim"
version = "1.0"
val exposedVersion= "0.34.1"
val bukkitversion= "1.8.8-R0.1-SNAPSHOT"
val kotlinversion= "1.5.30"

plugins {
    kotlin("jvm") version "1.5.30"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinversion")
    compileOnly("org.jetbrains.exposed:exposed-core:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    compileOnly("org.spigotmc:spigot-api:$bukkitversion")
    compileOnly("org.bukkit:bukkit:$bukkitversion")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    shadowJar {
        manifest {
            attributes["Class-Path"] = "GD_Essentials/lib/kotlin-stdlib-$kotlinversion.jar GD_Essentials/lib/exposed-core-$exposedVersion.jar GD_Essentials/lib/exposed-dao-$exposedVersion.jar GD_Essentials/lib/exposed-jdbc-$exposedVersion.jar GD_Essentials/lib/h2-1.4.200.jar"
        }
        classifier = null
        destinationDirectory.set(File("$buildDir/../Minecraft/plugins"))
    }
}
