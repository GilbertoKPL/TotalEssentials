package me.gilberto.essentials.version

import me.gilberto.essentials.management.Manager.consoleMessage
import me.gilberto.essentials.management.Manager.pluginpastedir
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files

object VersionChecker {
    private val libloc = pluginpastedir() +"/lib"
    fun checkversion() {
        val versionfile: YamlConfiguration
        consoleMessage("§aIniciando verificação da lib")
        try {
            versionfile = YamlConfiguration.loadConfiguration(URL("https://www.dropbox.com/s/34gzmbcs61gbu3d/versionchecker.yml?dl=1").openStream())
        } catch (Ex : Exception) {
            consoleMessage("§aErro ao iniciar verificação de atualização!")
            return
        }
        for (i in versionfile.getKeys(true)) {
            val a = i.split(".")
            if (a[0] == "version-lib" && a.size > 1) {
                val version = versionfile.getString(i)
                val lib = File(libloc, "$version.jar")
                if (!lib.exists()) {
                    val paste = File(libloc).list()
                    if (paste != null) {
                        for (d in paste) {
                            val e = d.split("-")
                            if (e.size == 2) {
                                if (e[0] == a[1]) {
                                    File(libloc, d).delete()
                                }
                            }
                            if (e.size == 3) {
                                if (e[1] == a[1].split("-")[1]) {
                                    File(libloc, d).delete()
                                }
                            }
                        }
                    }
                    else File(libloc).mkdirs()
                    consoleMessage("§aBaixando lib: $version...")
                    val filelib :InputStream
                    try {
                        filelib = URL(versionfile.getString(i.replace("version-lib", "repo"))).openStream()
                    } catch (Ex : Exception) {
                        consoleMessage("§cErro ao baixar modulo: $version !")
                        continue
                    }
                    consoleMessage("§aBaixado lib: $version com sucesso !")
                    Files.copy(filelib, lib.toPath())
                }
            }
        }
    }
}