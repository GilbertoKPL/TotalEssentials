import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "github.gilbertokpl.essentialsk"
version = "1.5.1"

val base = "$group.libs"
val exposedVersion = "0.37.2"
val kotlin = "1.6.10"
val buildVersion = "1.16.5"

plugins {
    kotlin("jvm") version "1.6.10"
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

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/GilbertoKpl/EssentialsK")
            credentials {
                username = "GilbertoKPL"
                password = project.findProperty("TOKEN").toString()
            }
        }

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        maven("https://mvnrepository.com/artifact/")
        maven("https://jitpack.io")
    }
}

dependencies {
    implementation("github.gilbertokpl.lib:libchecker-jar:1.0")

    compileOnly("org.spigotmc:spigot-api:$buildVersion-R0.1-SNAPSHOT") {
        exclude("commons-lang", "commons-lang")
        exclude("commons-io", "commons-io")
        exclude("org.yaml", "snakeyaml")
        exclude("com.google.code.gson", "gson")
    }

    //vault api
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit", "bukkit")
    }

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

    slim("org.slf4j:slf4j-nop:1.7.32")

    slim("com.google.code.gson:gson:2.8.9")

    slim("com.googlecode.json-simple:json-simple:1.1.1")

    slim("com.h2database:h2:2.0.204")

    slim("com.zaxxer:HikariCP:4.0.3") {
        exclude("org.slf4j", "slf4j-api")
    }

    slim("commons-io:commons-io:2.11.0")

    slim("me.carleslc.Simple-YAML:Simple-Yaml:1.7.2")

    slim("org.apache.commons:commons-lang3:3.12.0")

    slim("com.github.oshi:oshi-core:6.0.0") {
        exclude("org.slf4j", "slf4j-api")
    }

    slim("net.dv8tion:JDA:5.0.0-alpha.3") {
        exclude ("club.minnced", "opus-java")
        exclude("org.slf4j", "slf4j-api")
        exclude("org.jetbrains", "annotations")
    }

}

project.gradle.startParameter.excludedTaskNames.also {
    it.add("compileTestKotlin")
    it.add("compileTestJava")
    it.add("processTestResources")
    it.add("testClasses")
}

artifacts.archives(tasks.shadowJar)
tasks.slimJar {
    relocate("org.bstats", "$base.bstats")
    relocate("org.apache.commons.lang3", "$base.lang3")
    relocate("org.apache.commons.io", "$base.io")
    relocate("org.yaml", "$base.yaml")
    relocate("com.google.gson", "$base.gson")
    relocate("org.slf4j", "$base.slf4j")
}
tasks.shadowJar {
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
    this.softDepend = listOf("Vault", "Multiverse-Core")
    commands {
        register("essentialsk") {
            description = "This is a main essentialsk command!"
            aliases = listOf("system", "essentials", "s", "ks", "e")
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
        register("home") {
            description = "This is a home command!"
            aliases = listOf("h", "homes")
        }
        register("delhome") {
            description = "This is a sethome command!"
            aliases = listOf("deletarhome")
        }
        register("sethome") {
            description = "This is a sethome command!"
            aliases = listOf("setarhome")
        }
        register("setwarp") {
            description = "This is a setwarp command!"
            aliases = listOf("setarwarp")
        }
        register("delwarp") {
            description = "This is a delwarp command!"
            aliases = listOf("deletarwarp")
        }
        register("warp") {
            description = "This is a warp command!"
            aliases = listOf("w", "warps")
        }
        register("tp") {
            description = "This is a tp command!"
            aliases = listOf("teleport")
        }
        register("tpa") {
            description = "This is a tpa command!"
        }
        register("tpaccept") {
            description = "This is a tpaccept command!"
        }
        register("tpdeny") {
            description = "This is a tpdeny command!"
        }
        register("echest") {
            description = "This is a echest command!"
            aliases = listOf("ec")
        }
        register("gamemode") {
            description = "This is a gamemode command!"
            aliases = listOf("gm")
        }
        register("vanish") {
            description = "This is a vanish command!"
            aliases = listOf("v")
        }
        register("feed") {
            description = "This is a feed command!"
            aliases = listOf("comer")
        }
        register("heal") {
            description = "This is a heal command!"
            aliases = listOf("h")
        }
        register("light") {
            description = "This is a light command!"
            aliases = listOf("luz")
        }
        register("back") {
            description = "This is a back command!"
        }
        register("spawn") {
            description = "This is a spawn command!"
        }
        register("setspawn") {
            description = "This is a setspawn command!"
        }
        register("fly") {
            description = "This is a fly command!"
        }
        register("online") {
            description = "This is a online command!"
        }
        register("announce") {
            description = "This is a announce command!"
            aliases = listOf("anunciar")
        }
        register("craft") {
            description = "This is a announce command!"
        }
        register("trash") {
            description = "This is a announce command!"
            aliases = listOf("lixo")
        }
        register("tphere") {
            description = "This is a announce command!"
            aliases = listOf("tphere")
        }
    }
    permissions {
        register("essentialsk.*") {
            description = "all permission"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }


        register("essentialsk.commands.sethome.*")  {
            description = "max sethomes"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.kit.*")  {
            description = "permission kit"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.warp.*")  {
            description = "warp permission"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.nick.other")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.ec.other")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.gamemode.other")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.heal.other")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.feed.other")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.vanish.other")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.light.other")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.home.other")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.delhome.other")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.sethome.other")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.commands.spawn.other")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.teleport")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.homelimit")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.kitcatch")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.vanish")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.bed")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.vehicles")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.nametag")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.netherceiling")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.shiftcontainer")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.opencontainer")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.waitcommand")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.backblockedworlds")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.homeblockedworlds")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        register("essentialsk.bypass.nickblockednicks")  {
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }

    }
}