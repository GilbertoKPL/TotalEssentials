group = "io.github.gilbertodamim.ksystem"
version = 1.1


val exposedVersion= "0.35.3"
val bukkitversion= "1.17.1-R0.1-SNAPSHOT"
val kotlinversion= "1.5.31"
val hikariversion= "3.4.5" // java 8
val slf4j= "1.7.32"
val caffeine= "2.9.2" // java 8
val libpaste = "KSystem/lib/"

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
}

bukkit {
    main = "io.github.gilbertodamim.ksystem.KSystemMain"
    author = "Gilberto"
    version = "${project.version}"
    apiVersion = "1.13"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    commands {
        register("kit") {
            description = "This is a kit command!"
            aliases = listOf("kits")
            permission = "ksystem.kits"
        }
        register("editkit") {
            description = "This is a editkit command!"
            aliases = listOf("editarkit")
            permission = "ksystem.kits.admin"
        }
        register("createkit") {
            description = "This is a createkit command!"
            aliases = listOf("criarkit")
            permission = "ksystem.kits.admin"
        }
        register("delkit") {
            description = "This is a delkit command!"
            aliases = listOf("deletarkit")
            permission = "ksystem.kits.admin"
        }
    }
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
    compileOnly("com.github.ben-manes.caffeine:caffeine:$caffeine")
    //not lib
    compileOnly("org.apache.logging.log4j:log4j-core:2.14.1")
}
tasks.jar { enabled = false }
artifacts.archives(tasks.shadowJar)
tasks.shadowJar {
    archiveFileName.set(rootProject.name + "-" + project.version.toString() + ".jar")
    destinationDirectory.set(File("$buildDir/../Minecraft/plugins"))
    manifest {
        attributes["Class-Path"] = "${libpaste}kotlin-stdlib-$kotlinversion.jar ${libpaste}exposed-core-$exposedVersion.jar ${libpaste}exposed-dao-$exposedVersion.jar ${libpaste}exposed-jdbc-$exposedVersion.jar ${libpaste}h2-1.4.200.jar ${libpaste}mysql-connector-java-8.0.26.jar ${libpaste}HikariCP-$hikariversion.jar ${libpaste}slf4j-nop-$slf4j.jar ${libpaste}slf4j-simple-$slf4j.jar ${libpaste}caffeine-$caffeine.jar ${libpaste}caffeine-guava-$caffeine.jar"
    }
}
tasks.withType<JavaCompile> {
    sourceCompatibility = "8"
    targetCompatibility = "8"
    options.encoding = "UTF-8"
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

