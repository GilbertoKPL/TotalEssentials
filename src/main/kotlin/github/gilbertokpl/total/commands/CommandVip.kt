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
            minimumSize = 1,
            maximumSize = 4,
            usage = listOf(
                "totalessentials.commands.vip.admin_/vip criar <vipName> <vipGroup>",
                "totalessentials.commands.vip.admin_/vip gerarkey <vipName> <days>",
                "/vip usarkey <key>",
                "totalessentials.commands.vip.admin_/vip dar <player> <vipName> <days>",
                "P_/vip tempo",
                "P_/vip mudar",
                "totalessentials.commands.vip.admin_/vip tempo <player>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args[0] == "criar" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin"))  {
            if (VipData.checkIfVipExist(args[1])) {
                s.sendMessage(LangConfig.VipsExist)
                return false
            }
            if (TotalEssentials.permission.groups.contains(args[2])) {
                VipData.createNewVip(args[1], args[2])
                s.sendMessage(LangConfig.VipsCreateNew.replace("%vip%", args[1]))
                return false
            }
            s.sendMessage(LangConfig.VipsGroupNotExist)
            return false
        }

        if (args[0] == "gerarkey" && args.size == 3 && s.hasPermission("totalessentials.commands.vip.admin")) {
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

        if (args[0] == "tempo" && args.size == 2 && s.hasPermission("totalessentials.commands.vip.admin")) {
            for (i in PlayerData.vipCache[args[1]] ?: return false) {
                s.sendMessage(LangConfig.VipsTimeMessage.replace("%vipName%", i.key).replace("%vipTime%", TotalEssentials.basePlugin.getTime().convertMillisToString(i.value - System.currentTimeMillis(), false)))
            }
            return false
        }

        if (args.size == 1 && args[0] == "tempo" && s is Player) {
            for (i in PlayerData.vipCache[s] ?: return false) {
                s.sendMessage(LangConfig.VipsTimeMessage.replace("%vipName%", i.key).replace("%vipTime%", TotalEssentials.basePlugin.getTime().convertMillisToString(i.value - System.currentTimeMillis(), false)))
            }
            return false
        }

        if (args[0] == "mudar" && args.size == 1 && s is Player) {
            val vipName = VipUtil.updateCargo(s.name.lowercase()) ?: return false
            s.sendMessage(LangConfig.VipsSwitch.replace("%vipName%", vipName))
            return false
        }

        if (args[0] == "dar" && args.size == 4 && s.hasPermission("totalessentials.commands.vip.admin")) {

            if (args[3].toLongOrNull() == null) {
                return true
            }

            if (!PlayerData.checkIfPlayerExist(args[1])) {
                s.sendMessage(LangConfig.generalPlayerNotExist)
                return false
            }

            if (!VipData.checkIfVipExist(args[2])) {
                s.sendMessage(LangConfig.VipsNotExist)
                return false
            }

            val millisVipTime = (args[3].toLong() * 86400000) + System.currentTimeMillis()

            PlayerData.vipCache[args[1]] = hashMapOf(args[2] to millisVipTime)

            VipUtil.updateCargo(s.name.lowercase(), VipData.vipGroup[args[2]])

            s.sendMessage(LangConfig.VipsActivate.replace("%vip%", args[2]).replace("%days%", args[3]))
            return false
        }

        return true
    }
}