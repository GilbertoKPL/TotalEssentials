import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val base = "github.gilbertokpl.library"

version = "1.0"


repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://m2.dv8tion.net/releases")
    maven("https://maven.elmakers.com/repository/")
    maven("https://jitpack.io")
}

dependencies {

    //vault
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit", "bukkit")
    }

    compileOnly(fileTree(mapOf("dir" to "$buildDir\\..\\localjar", "include" to listOf("*.jar"))))

    //spigot
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT") {
        exclude("commons-lang", "commons-lang")
        exclude("commons-io", "commons-io")
        exclude("org.yaml", "snakeyaml")
        exclude("com.google.code.gson", "gson")
    }


    //exposed
    implementation("org.jetbrains.exposed:exposed-core:0.41.1") {
        exclude("org.slf4j", "slf4j-api")
    }

    implementation("org.jetbrains.exposed:exposed-dao:0.41.1") {
        exclude("org.slf4j", "slf4j-api")
    }

    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1") {
        exclude("org.slf4j", "slf4j-api")
    }


    //H2 database
    implementation("com.github.h2database:h2database:version-2.2.220") {
        exclude("org.slf4j", "slf4j-api")
    }

    //Mysql with MariaDB driver database
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4") {
        exclude("org.slf4j", "slf4j-api")
    }
    //implementation to mysql - MariaDB
    implementation("com.zaxxer:HikariCP:4.0.3") {
            exclude("org.slf4j", "slf4j-api")
    }

    //remove all connections of slf4
    implementation("org.slf4j:slf4j-nop:2.0.7")

    //simple yaml to help in yaml
    implementation("me.carleslc.Simple-YAML:Simple-Yaml:1.7.3") {
        exclude("org.slf4j", "slf4j-api")
    }

    //host info
    implementation("com.github.oshi:oshi-core:6.4.4") {
        exclude("org.slf4j", "slf4j-api")
    }

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0") {
        exclude("org.slf4j", "slf4j-api")
    }

    implementation("net.dv8tion:JDA:5.0.0-beta.12") {
        exclude("club.minnced","opus-java")
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    implementation("commons-io:commons-io:2.11.0") {
        exclude("org.slf4j", "slf4j-api")
    }

    implementation("org.json:json:20230227") {
        exclude("org.slf4j", "slf4j-api")
    }



}

tasks.shadowJar {
    manifest {
        attributes["Class-Path"] = "TotalEssentials/lib/TotalEssentials-1.0.jar"
    }
    archiveFileName.set(rootProject.name + "-" + project.version.toString() + ".jar")
    destinationDirectory.set(File("$projectDir/jar/plugins"))



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