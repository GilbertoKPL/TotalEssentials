group = "me.gilbertodamim"
val exposedVersion= "0.35.2"
val bukkitversion= "1.17.1-R0.1-SNAPSHOT"
val kotlinversion= "1.5.31"
val hikariversion= "3.4.5"
val libpaste = "EssentialsGD/lib/"

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.0"
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
    compileOnly("com.zaxxer:HikariCP:$hikariversion")
}
@kotlin.Suppress("Deprecation")
tasks {
    shadowJar {
        manifest {
            attributes["Class-Path"] = "${libpaste}kotlin-stdlib-$kotlinversion.jar ${libpaste}exposed-core-$exposedVersion.jar ${libpaste}exposed-dao-$exposedVersion.jar ${libpaste}exposed-jdbc-$exposedVersion.jar ${libpaste}h2-1.4.200.jar ${libpaste}mysql-connector-java-8.0.26.jar ${libpaste}HikariCP-$hikariversion.jar ${libpaste}slf4j-nop-1.7.32.jar ${libpaste}slf4j-api-1.7.32.jar"
        }
        classifier = null
        destinationDirectory.set(File("$buildDir/../Minecraft/plugins"))
    }
}
