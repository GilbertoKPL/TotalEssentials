group = "io.github.gilbertodamim.ksystem"
version = 1.0

//1.17.1 -> 1.17.1-R0.1-SNAPSHOT

val exposedVersion= "0.35.3"
val bukkitVersion= "1.17.1-R0.1-SNAPSHOT"
val kotlinVersion= "1.5.31"
val xSeries = "8.4.0"
val hikariVersion= "3.4.5" // java 8
val slf4j= "1.7.32"
val caffeine= "2.9.2" // java 8
val libPaste = "KSystem/lib/"

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
        register("ksystem") {
            description = "This is a kit command!"
            aliases = listOf("system", "essentials", "s", "ks")
            permission = "ksystem.kits"
        }
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
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    compileOnly("org.jetbrains.exposed:exposed-core:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    compileOnly("org.spigotmc:spigot-api:$bukkitVersion")
    compileOnly("com.zaxxer:HikariCP:$hikariVersion")
    compileOnly("com.github.ben-manes.caffeine:caffeine:$caffeine")
    //in plugin
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.slf4j:slf4j-nop:1.7.32")
    //not lib
    compileOnly("org.apache.logging.log4j:log4j-core:2.14.1")
}
artifacts.archives(tasks.shadowJar)
tasks.shadowJar {
    archiveFileName.set(rootProject.name + "-" + project.version.toString() + ".jar")
    destinationDirectory.set(File("$buildDir/../Minecraft/plugins"))
    manifest {
        attributes["Class-Path"] = "${libPaste}kotlin-stdlib-$kotlinVersion.jar ${libPaste}exposed-core-$exposedVersion.jar ${libPaste}exposed-dao-$exposedVersion.jar ${libPaste}exposed-jdbc-$exposedVersion.jar ${libPaste}h2-1.4.200.jar ${libPaste}mysql-connector-java-8.0.26.jar ${libPaste}HikariCP-$hikariVersion.jar ${libPaste}caffeine-$caffeine.jar ${libPaste}caffeine-guava-$caffeine.jar"
        attributes["Version-Name"] = project.version
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

