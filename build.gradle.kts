import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val base = "github.gilbertokpl.library"

version = "1.0"


repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://m2.dv8tion.net/releases")
    maven("https://mvnrepository.com/artifact/net.dv8tion/JDA")
    maven("https://jitpack.io")
    maven("https://maven.elmakers.com/repository/")
}

dependencies {

    //vault
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit", "bukkit")
    }

    //spigot
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT") {
        exclude("commons-lang", "commons-lang")
        exclude("commons-io", "commons-io")
        exclude("org.yaml", "snakeyaml")
        exclude("com.google.code.gson", "gson")
    }


    //exposed
    compileOnly("org.jetbrains.exposed:exposed-core:0.41.1") {
        exclude("org.slf4j", "slf4j-api")
    }
    compileOnly("org.jetbrains.exposed:exposed-dao:0.41.1") {
        exclude("org.slf4j", "slf4j-api")
    }
    compileOnly("org.jetbrains.exposed:exposed-jdbc:0.41.1") {
        exclude("org.slf4j", "slf4j-api")
    }

    //H2 database
    compileOnly("com.h2database:h2:2.1.214")

    //Mysql with MariaDB driver database
    compileOnly("org.mariadb.jdbc:mariadb-java-client:3.0.6")

    //implementation to mysql - MariaDB
    compileOnly("com.zaxxer:HikariCP:4.0.3") {
        exclude("org.slf4j", "slf4j-api")
    }

    //remove all connections of slf4
    compileOnly("org.slf4j:slf4j-nop:2.0.5")

    //simple yaml to help in yaml
    compileOnly("me.carleslc.Simple-YAML:Simple-Yaml:1.7.3")

    //host info
    compileOnly("com.github.oshi:oshi-core:6.3.2") {
        exclude("org.slf4j", "slf4j-api")
    }

    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.21")

    compileOnly("net.dv8tion:JDA:5.0.0-beta.1") {
        exclude("club.minnced","opus-java")
        exclude("org.slf4j", "slf4j-api")
    }

    compileOnly("commons-io:commons-io:2.11.0")

    compileOnly("org.json:json:20220924")

}

tasks.shadowJar {
    manifest {
        attributes["Class-Path"] = "TotalEssentials/lib/TotalEssentials-1.0.jar"
    }
    archiveFileName.set(rootProject.name + "-" + project.version.toString() + ".jar")
    destinationDirectory.set(File("$projectDir/jar"))



    //relocate all libs
    relocate("org.apache.commons.lang3", "$base.lang3")
    relocate("net.dv8tion", "$base.dv8tion")
    relocate("com.neovisionaries", "$base.neovisionaries")
    relocate("org.apache.commons.io", "$base.io")
    relocate("org.yaml", "$base.yaml")
    relocate("com.google.gson", "$base.gson")
    relocate("org.simpleyaml", "$base.yaml")
    relocate("com.zaxxer.hikari", "$base.hikari")
}

tasks {
    javadoc {
        options.encoding = "UTF-8"
    }
    compileJava {
        options.encoding = "UTF-8"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}