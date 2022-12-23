package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.cache.internal.DataManager
import github.gilbertokpl.total.cache.internal.ShopInventory
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.ShopData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandShop : github.gilbertokpl.core.external.command.CommandCreator("shop") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("loja", "lojas"),
            active = MainConfig.shopActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.shop",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "/loja <player>",
                "totalessentials.commands.shop.set_/loja setar",
                "totalessentials.commands.shop.set_/loja trocar",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        val p = s as Player

        if (args.isEmpty()) {

            DataManager.shopGuiCache[1].also {
                it ?: run {
                    s.sendMessage(LangConfig.shopNotExistShop)
                    return false
                }

                p.openInventory(it)
            }
            return false
        }

        if (args[0].equals("trocar", true) && p.hasPermission("totalessentials.commands.shop.set")) {
            if (!ShopData.checkIfExist(p.name.lowercase())) {
                s.sendMessage(LangConfig.shopNotCreated)
                return false
            }
            val bol = ShopData.shopOpen[p]!!

            ShopData.shopOpen[p] = bol.not()

            val checkIfIsOpen = if (bol.not()) {
                LangConfig.shopOpen
            } else {
                LangConfig.shopClosed
            }

            p.sendMessage(LangConfig.shopSwitchMessage.replace("%open%", checkIfIsOpen))
            ShopInventory.setup()
            return false
        }

        if (args[0].equals("setar", true) && p.hasPermission("totalessentials.commands.shop.set")) {
            s.sendMessage(LangConfig.shopCreateShopSuccess)
            if (ShopData.checkIfExist(p.name.lowercase())) {
                ShopData.shopLocation[p] = p.location
                return false
            }
            ShopData.createNewShop(p, p.location)
            ShopInventory.setup()
            return false
        }

        //check if not exist
        if (!ShopData.checkIfExist(args[0])) {
            s.sendMessage(LangConfig.shopNotExist)
            return false
        }

        if (ShopData.shopOpen[args[0].lowercase()] == false) {
            s.sendMessage(LangConfig.shopClosedMessage)
            return false
        }

        if (args[0].lowercase() != p.name.lowercase()) {
            ShopData.shopVisits[args[0]] = ShopData.shopVisits[args[0]]!!.plus(1)
        }

        PlayerUtil.shopTeleport(p, args[0])

        return false
    }
}