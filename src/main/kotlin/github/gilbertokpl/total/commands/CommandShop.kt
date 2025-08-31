package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.cache.data.ShopData
import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.cache.inventory.Shop
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ItemUtil
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandShop : CommandCreator("shop") {

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

        // open own shop GUI
        if (args.isEmpty()) {
            val inventory = Data.shopInventoryCache[1] ?: run {
                s.sendMessage(LangConfig.shopNotExistShop)
                return false
            }

            if (p.hasPermission("totalessentials.commands.shop.set")) {
                inventory.setItem(30, ItemUtil.item(Material.CHEST, LangConfig.shopLoreSet, false))
                inventory.setItem(32, ItemUtil.item(Material.CHEST, LangConfig.shopLoreSwitch, false))
            } else {
                inventory.setItem(30, Shop.GLASS_MATERIAL)
                inventory.setItem(32, Shop.GLASS_MATERIAL)
            }

            p.openInventory(inventory)
            return false
        }

        // toggle own shop open/close
        if (args[0].equals("trocar", true) && p.hasPermission("totalessentials.commands.shop.set")) {
            if (!ShopData.checkIfShopExists(p.name.lowercase())) {
                s.sendMessage(LangConfig.shopNotCreated)
                return false
            }

            val currentState = ShopData.shopOpen[p]!!
            ShopData.shopOpen[p] = !currentState

            val statusText = if (!currentState) LangConfig.shopOpen else LangConfig.shopClosed
            p.sendMessage(LangConfig.shopSwitchMessage.replace("%open%", statusText))

            Shop.setup()
            return false
        }

        // set or update own shop
        if (args[0].equals("setar", true) && p.hasPermission("totalessentials.commands.shop.set")) {
            s.sendMessage(LangConfig.shopCreateShopSuccess)

            if (ShopData.checkIfShopExists(p.name.lowercase())) {
                ShopData.shopLocation[p] = p.location
            } else {
                ShopData.createNewShop(p.location, p)
            }

            Shop.setup()
            return false
        }

        // check if target shop exists
        if (!ShopData.checkIfShopExists(args[0])) {
            s.sendMessage(LangConfig.shopNotExist)
            return false
        }

        // check if target shop is open
        if (ShopData.shopOpen[args[0].lowercase()] == false) {
            s.sendMessage(LangConfig.shopClosedMessage)
            return false
        }

        // increment visit count if visiting other player
        if (args[0].lowercase() != p.name.lowercase()) {
            ShopData.shopVisits[args[0]] = ShopData.shopVisits[args[0]]!!.plus(1)
        }

        // teleport player to the shop
        PlayerUtil.shopTeleport(p, args[0])
        return false
    }
}
