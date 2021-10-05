group = "me.gilbertodamim"
val exposedVersion= "0.35.1"
val bukkitversion= "1.17.1-R0.1-SNAPSHOT"
val kotlinversion= "1.5.31"
val libpaste = "EssentialsGD/lib/"

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
    compileOnly("com.zaxxer:HikariCP:3.4.2")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    shadowJar {
        manifest {
            attributes["Class-Path"] = "$libpaste/kotlin-stdlib-$kotlinversion.jar $libpaste/exposed-core-$exposedVersion.jar $libpaste/exposed-dao-$exposedVersion.jar $libpaste/exposed-jdbc-$exposedVersion.jar $libpaste/h2-1.4.200.jar $libpaste/mysql-connector-java-8.0.26.jar $libpaste/HikariCP-3.4.2.jar $libpaste/slf4j-nop-1.7.32.jar $libpaste/slf4j-api-1.7.32.jar"
        }
        classifier = null
        destinationDirectory.set(File("$buildDir/../Minecraft/plugins"))
    }
}
