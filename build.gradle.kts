import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.20"
    id("com.gradleup.shadow") version "9.4.0"
}

version = "1.2.3"


repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://m2.dv8tion.net/releases")
    maven("https://maven.elmakers.com/repository/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/creatorfromhell/")
}

dependencies {

    //vault
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1") {
        exclude("org.bukkit", "bukkit")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.15") {
        exclude("org.bukkit", "bukkit")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly(fileTree(mapOf("dir" to "$buildDir\\..\\localjar", "include" to listOf("*.jar"))))

    //spigot
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT") {
        exclude("commons-lang", "commons-lang")
        exclude("commons-io", "commons-io")
        exclude("org.yaml", "snakeyaml")
        exclude("com.google.code.gson", "gson")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    //exposed
    compileOnly("org.jetbrains.exposed:exposed-core:1.1.1") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("org.jetbrains.exposed:exposed-dao:1.1.1") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("org.jetbrains.exposed:exposed-jdbc:1.1.1") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }


    //H2 database
    compileOnly("com.h2database:h2:2.2.224") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    //Mysql with MariaDB driver database
    compileOnly("org.mariadb.jdbc:mariadb-java-client:3.5.7") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }
    //implementation to mysql - MariaDB
    compileOnly("com.zaxxer:HikariCP:4.0.3") {
            exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    //remove all connections of slf4
    compileOnly("org.slf4j:slf4j-nop:2.0.17")

    //simple yaml to help in yaml
    compileOnly("me.carleslc.Simple-YAML:Simple-Yaml:1.7.3") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    //host info
    compileOnly("com.github.oshi:oshi-core:6.9.3") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.3.20") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("net.dv8tion:JDA:6.3.2") {
        exclude("club.minnced","opus-java")
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("org.json:json:20250517") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("club.minnced:discord-webhooks:0.8.4")

}

tasks.shadowJar {
    archiveFileName.set(rootProject.name + "-" + project.version.toString() + ".jar")
    destinationDirectory.set(File("$projectDir/jar/plugins"))

    manifest {
        attributes(
            "Plugin-Version" to project.version.toString(),
            "Plugin-Creator" to "Gilberto",
            "Plugin-Name" to "TotalEssentials",
            "Plugin-Github" to "https://github.com/GilbertoKPL/TotalEssentials"
        )
    }
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
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
}