import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "github.gilbertokpl.essentialsk"
version = 1.0

val base = "$group.libs"
val exposedVersion = "0.36.2"
val kotlin = "1.6.0"
val buildVersion = "1.18"

plugins {
    kotlin("jvm") version "1.6.0"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("io.github.slimjar") version "1.3.0"
}

detekt {
    config = files("$projectDir/config/detekt.yml")
    autoCorrect = true
    ignoreFailures = true
}


allprojects {
    repositories {
        mavenCentral()

        //não esquecer de ocultar
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/GilbertoKpl/EssentialsK")
            credentials {
                username = "GilbertoKPL"
                password = project.findProperty("TOKEN").toString()
            }
        }

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://mvnrepository.com/artifact/")
        maven("https://jitpack.io")
    }
}

dependencies {
    implementation("github.gilbertokpl.lib:libchecker-jar:1.0")

    compileOnly("org.spigotmc:spigot-api:$buildVersion-R0.1-SNAPSHOT") {
        exclude("com.google.guava", "guava")
        exclude("commons-lang", "commons-lang")
        exclude("commons-io", "commons-io")
        exclude("org.yaml", "snakeyaml")
        exclude("com.google.code.gson", "gson")
    }

    //exclude messages
    compileOnly("org.apache.logging.log4j:log4j-core:2.14.1")

    slim("org.jetbrains.kotlin:kotlin-reflect:$kotlin")

    slim("org.jetbrains.kotlin:kotlin-stdlib:$kotlin")

    slim("org.jetbrains:annotations:23.0.0")

    slim("org.jetbrains.exposed:exposed-core:$exposedVersion") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
        exclude("org.slf4j", "slf4j-api")
    }

    slim("org.jetbrains.exposed:exposed-dao:$exposedVersion") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
        exclude("org.slf4j", "slf4j-api")
    }

    slim("org.jetbrains.exposed:exposed-jdbc:$exposedVersion") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
        exclude("org.slf4j", "slf4j-api")
    }

    slim("org.bstats:bstats-bukkit:2.2.1")

    slim("org.mariadb.jdbc:mariadb-java-client:2.7.4")

    slim("org.slf4j:slf4j-api:1.7.32")

    slim("org.slf4j:slf4j-nop:1.7.32")

    slim("org.json:json:20210307")

    slim("com.h2database:h2:1.4.199")

    slim("com.zaxxer:HikariCP:3.4.5") {
        exclude("org.slf4j", "slf4j-api")
    }

    slim("com.github.ben-manes.caffeine:caffeine:2.9.3")

    slim("com.github.ben-manes.caffeine:guava:2.9.3")

    slim("commons-io:commons-io:2.11.0")

    slim("me.carleslc.Simple-YAML:Simple-Yaml:1.7.2")

    slim("org.apache.commons:commons-lang3:3.12.0")
}

project.gradle.startParameter.excludedTaskNames.also {
    it.add("compileTestKotlin")
    it.add("compileTestJava")
    it.add("compileJava")
    it.add("processTestResources")
    it.add("testClasses")
}

artifacts.archives(tasks.shadowJar)
tasks.slimJar {
    relocate("org.bstats", "$base.bstats")
    relocate("org.apache.commons", "$base.io")
    relocate("org.yaml", "$base.yaml")
    relocate("com.google.guava", "$base.guava")
}
tasks.shadowJar {
    minimize()
    archiveFileName.set(rootProject.name + "-" + project.version.toString() + ".jar")
    destinationDirectory.set(File("$projectDir/Minecraft/plugins"))
    manifest {
        attributes(
            "Plugin-Version" to project.version.toString(),
            "Plugin-Creator" to "Gilberto",
            "Plugin-Name" to "EssentialsK",
            "Plugin-Github" to "https://github.com/GilbertoKPL/EssentialsK"
        )
    }
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "1.8"
}
bukkit {
    main = "$group.EssentialsK"
    author = "Gilberto"
    name = project.name
    version = project.version.toString()
    apiVersion = "1.13"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    commands {
        register("essentialsk") {
            description = "This is a main essentialsk command!"
            aliases = listOf("system", "essentials", "s", "ks")
        }
        register("kit") {
            description = "This is a kit command!"
            aliases = listOf("kits")
        }
        register("editkit") {
            description = "This is a editkit command!"
            aliases = listOf("editarkit")
        }
        register("createkit") {
            description = "This is a createkit command!"
            aliases = listOf("criarkit")
        }
        register("delkit") {
            description = "This is a delkit command!"
            aliases = listOf("deletarkit")
        }
        register("givekit") {
            description = "This is a givekit command!"
            aliases = listOf("darkit")
        }
        register("nick") {
            description = "This is a nick command!"
            aliases = listOf("nome")
        }
    }
}