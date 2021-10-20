group = "me.gilbertodamim"
val exposedVersion= "0.35.2"
val bukkitversion= "1.17.1-R0.1-SNAPSHOT"
val kotlinversion= "1.5.31"
val hikariversion= "3.4.5"
val slf4j= "1.7.32"
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
    compileOnly("org.slf4j:slf4j-api:$slf4j")
    compileOnly("org.apache.logging.log4j:log4j-core:2.14.1")
}
@kotlin.Suppress("Deprecation")
tasks {
    shadowJar {
        manifest {
            attributes["Class-Path"] = "${libpaste}kotlin-stdlib-$kotlinversion.jar ${libpaste}exposed-core-$exposedVersion.jar ${libpaste}exposed-dao-$exposedVersion.jar ${libpaste}exposed-jdbc-$exposedVersion.jar ${libpaste}h2-1.4.200.jar ${libpaste}mysql-connector-java-8.0.26.jar ${libpaste}HikariCP-$hikariversion.jar ${libpaste}slf4j-nop-$slf4j.jar ${libpaste}slf4j-simple-$slf4j.jar"
        }
        classifier = null
        destinationDirectory.set(File("$buildDir/../Minecraft/plugins"))
    }
}
