group = "io.github.gilbertodamim"
val exposedVersion= "0.35.3"
val bukkitversion= "1.17.1-R0.1-SNAPSHOT"
val kotlinversion= "1.5.31"
val hikariversion= "3.4.5"
val slf4j= "1.7.32"

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
    compileOnly("org.apache.logging.log4j:log4j-core:2.14.1")
}
tasks.jar { enabled = false }
artifacts.archives(tasks.shadowJar)
tasks.shadowJar {
    archiveFileName.set(rootProject.name + ".jar")
    destinationDirectory.set(File("$buildDir/../Minecraft/plugins"))
}
tasks.withType<JavaCompile> {
    sourceCompatibility = "8"
    targetCompatibility = "8"
    options.encoding = "UTF-8"
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

