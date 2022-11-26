package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.KeyData
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.VipData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.VipUtil
import net.milkbowl.vault.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandVip : github.gilbertokpl.core.external.command.CommandCreator("vip") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("vips"),
            active = MainConfig.vipActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.vip",
            minimumSize = 2,
            maximumSize = 3,
            usage = listOf(
                "/vip criar <vipName> <vipGroup>",
                "/vip gerarkey <vipName> <dias>",
                "/vip usarkey <key>",
                "/vip darvip <key>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args[0] == "criar" && args.size == 3) {
            if (TotalEssentials.permission.groups.contains(args[2])) {
                VipData.createNewVip(args[1], args[2])
                s.sendMessage(LangConfig.VipsCreateNew.replace("%vip%", args[1]))
                return false
            }
            s.sendMessage(LangConfig.VipsGroupNotExist)
            return false
        }

        if (args[0] == "gerarkey" && args.size == 3) {
            val time = args[2].toLongOrNull() ?: return true

            if (VipData.checkIfVipExist(args[1])) {
                val key = KeyData.genNewVipKey(args[1], time)
                s.sendMessage(LangConfig.VipsCreateNewKey.replace("%key%", key))
            }
            return false
        }

        if (s is Player && args[0] == "usarkey" && args.size == 2) {
            if (!KeyData.checkIfKeyExist(args[1])) {
                s.sendMessage(LangConfig.VipsKeyNotExist)
                return false
            }

            val vipName = KeyData.vipName[args[1]] ?: return true
            val vipTime = KeyData.vipTime[args[1]] ?: return true

            val millisVipTime = vipTime * 86400000 + System.currentTimeMillis()

            PlayerData.vipCache[s] = hashMapOf(vipName to millisVipTime)

            KeyData.delete(args[1])

            VipUtil.updateCargo(s.name.lowercase(), VipData.vipGroup[vipName])

            s.sendMessage(LangConfig.VipsActivate.replace("%vip%", vipName).replace("%days%", vipTime.toString()))

            return false

        }

        if (args[0] == "darvip" && args.size == 2) {
            return false
        }

        return true
    }
}