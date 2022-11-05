import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val base = "github.gilbertokpl.total"
val exposedVersion = "0.40.1"
val projectVersion = "1.0"


repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
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
    implementation("org.jetbrains.exposed:exposed-core:0.40.1") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("org.jetbrains.exposed:exposed-dao:0.40.1") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("org.jetbrains.exposed:exposed-jdbc:0.40.1") {
        exclude("org.slf4j", "slf4j-api")
    }

    //H2 database
    implementation("com.h2database:h2:2.1.214")

    //Mysql with MariaDB driver database
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")

    //implementation to mysql - MariaDB
    implementation("com.zaxxer:HikariCP:4.0.3") {
        exclude("org.slf4j", "slf4j-api")
    }

    //remove all connections of slf4
    implementation("org.slf4j:slf4j-nop:2.0.3")

    //simple yaml to help in yaml
    implementation("me.carleslc.Simple-YAML:Simple-Yaml:1.8.2")

    //host info
    implementation("com.github.oshi:oshi-core:6.3.0") {
        exclude("org.slf4j", "slf4j-api")
    }

}

tasks.shadowJar {
    manifest {
        attributes["Class-Path"] = "../plugins/LogicFrame/LogicFrame.jar"
    }
    archiveFileName.set(rootProject.name + "-" + project.version.toString() + ".jar")
    destinationDirectory.set(File("$projectDir/jar"))

    dependencies {
        //kotlin
        include(dependency("org.jetbrains:annotations"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib-common"))
        include(dependency("org.jetbrains.kotlin:kotlin-reflect"))
        include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core"))
        include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm"))
        include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-bom"))
        //exposed
        include(dependency("org.jetbrains.exposed:exposed-core"))
        include(dependency("org.jetbrains.exposed:exposed-dao"))
        include(dependency("org.jetbrains.exposed:exposed-jdbc"))

        //H2 database
        include(dependency("com.h2database:h2"))

        //Mysql with MariaDB driver database
        include(dependency("org.mariadb.jdbc:mariadb-java-client"))

        //implementation to mysql - MariaDB
        include(dependency("com.zaxxer:HikariCP"))

        //remove all connections of slf4
        include(dependency("org.slf4j:slf4j-nop"))
        include(dependency("org.slf4j:slf4j-api"))

        //simple yaml to help in yaml
        include(dependency("me.carleslc.Simple-YAML:Simple-Yaml"))
        include(dependency("org.yaml:snakeyaml"))

        //host info
        include(dependency("com.github.oshi:oshi-core"))
        include(dependency("net.java.dev.jna:jna-platform"))
        include(dependency("net.java.dev.jna:jna"))
    }

    //relocate all libs
    relocate("org.apache.commons.lang3", "$base.lang3")
    relocate("org.apache.commons.io", "$base.io")
    relocate("org.yaml", "$base.yaml")
    relocate("com.google.gson", "$base.gson")
    relocate("org.simpleyaml", "$base.yaml")
    relocate("com.zaxxer.hikari", "$base.hikari")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}