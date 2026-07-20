import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.attributes.java.TargetJvmVersion

plugins {
    kotlin("jvm") version "2.4.0"
    id("com.gradleup.shadow") version "9.4.0"
}

version = "1.2.3"


repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://maven.elmakers.com/repository/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/creatorfromhell/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {

    // Velocity companion (the same jar can be installed on the proxy and backends)
    compileOnly("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")

    // BungeeCord companion (same jar, selected through bungee.yml)
    compileOnly("net.md-5:bungeecord-api:1.20-R0.2")

    //vault
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1") {
        exclude("org.bukkit", "bukkit")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.15") {
        exclude("org.bukkit", "bukkit")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("me.clip:placeholderapi:2.11.6")

    compileOnly(fileTree(mapOf(
        "dir" to "$buildDir\\..\\localjar",
        "include" to listOf("*.jar"),
        "exclude" to listOf("LegendChat*.jar")
    )))

    //spigot
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT") {
        exclude("commons-lang", "commons-lang")
        exclude("commons-io", "commons-io")
        exclude("org.yaml", "snakeyaml")
        exclude("com.google.code.gson", "gson")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    //exposed
    compileOnly("org.jetbrains.exposed:exposed-core:1.3.1") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("org.jetbrains.exposed:exposed-dao:1.3.1") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("org.jetbrains.exposed:exposed-jdbc:1.3.1") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }


    //H2 database
    compileOnly("com.h2database:h2:2.2.224") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    //Mysql with MariaDB driver database
    compileOnly("org.mariadb.jdbc:mariadb-java-client:3.5.9") {
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

    // Isolated in the final jar because old Bukkit versions provide an
    // incompatible SnakeYAML in the parent classloader.
    implementation("me.carleslc.Simple-YAML:Simple-Yaml:1.7.3") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }
    implementation("org.yaml:snakeyaml:1.30")

    //host info
    compileOnly("com.github.oshi:oshi-core:6.9.3") {
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
    }

    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.4.0") {
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

    relocate("org.simpleyaml", "github.gilbertokpl.total.internal.libs.simpleyaml")
    relocate("org.yaml.snakeyaml", "github.gilbertokpl.total.internal.libs.snakeyaml")
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

// The backend remains Java 8 compatible for MCPC/Paper 1.16. Velocity 3.5 itself
// runs on Java 21, but the companion entry point is deliberately compiled to
// Java 8 bytecode so both platforms can use the same artifact.
configurations.named("compileClasspath") {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
}
