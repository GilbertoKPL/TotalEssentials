package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.lang.reflect.Field

object ItemUtil {

    private val reflectionCache = ReflectionCache()

    private val armorSlots = setOf("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS")

    /**
     * Entrega um kit ao jogador com verificações de permissão e cooldown
     */
    fun pickupKit(player: Player, kitName: String) {
        if (!hasKitPermission(player, kitName)) {
            player.sendMessage(LangConfig.generalNotPerm)
            return
        }

        if (!canClaimKit(player, kitName)) {
            sendCooldownMessage(player, kitName)
            return
        }

        deliverKit(player, kitName)
    }

    /**
     * Entrega itens de um kit ao jogador
     * @return true se os itens foram entregues com sucesso, false caso contrário
     */
    fun giveKit(
        player: Player,
        items: MutableList<ItemStack>,
        autoEquipArmor: Boolean,
        dropIfFull: Boolean = false
    ): Boolean {
        val inventory = player.inventory
        val clonedItems = items.map { it.clone() }

        val (armorItems, regularItems) = if (autoEquipArmor) {
            separateArmorItems(player, clonedItems)
        } else {
            emptyMap<ArmorSlot, ItemStack>() to clonedItems
        }

        val deliverySuccess = deliverItems(player, regularItems, dropIfFull)

        if (deliverySuccess && autoEquipArmor) {
            equipArmor(inventory, armorItems)
        }

        return deliverySuccess
    }

    /**
     * Cria um ItemStack com nome e lore customizados
     */
    fun createItem(
        material: Material,
        displayName: String,
        lore: List<String> = emptyList(),
        glowEffect: Boolean = false
    ): ItemStack {
        val item = ItemStack(material)

        if (glowEffect) {
            applyGlowEffect(item)
        }

        val meta = item.itemMeta ?: return item

        reflectionCache.setItemDisplayName(meta, displayName)

        if (lore.isNotEmpty()) {
            meta.lore = lore
        }

        if (glowEffect) {
            hideEnchantments(meta)
        }

        item.itemMeta = meta
        return item
    }

    /**
     * Define o display name de um ItemMeta (compatível com versões antigas)
     */
    fun setDisplayName(meta: ItemMeta?, displayName: String) {
        if (meta == null) return
        reflectionCache.setItemDisplayName(meta, displayName)
    }

    private fun hasKitPermission(player: Player, kitName: String): Boolean {
        return player.hasPermission("totalessentials.commands.kit.$kitName")
    }

    private fun canClaimKit(player: Player, kitName: String): Boolean {
        if (player.hasPermission("totalessentials.bypass.kitcatch")) return true

        val kitsCache = PlayerData.kitsCache[player] ?: return true
        val lastClaim = kitsCache[kitName] ?: 0L
        val kitCooldown = KitsData.kitTime[kitName] ?: 0L
        val nextAvailable = lastClaim + kitCooldown

        return nextAvailable < System.currentTimeMillis()
    }

    private fun sendCooldownMessage(player: Player, kitName: String) {
        val kitsCache = PlayerData.kitsCache[player] ?: return
        val lastClaim = kitsCache[kitName] ?: 0L
        val kitCooldown = KitsData.kitTime[kitName] ?: 0L
        val nextAvailable = lastClaim + kitCooldown
        val remainingTime = nextAvailable - System.currentTimeMillis()

        val formattedTime = TotalEssentials.getCore().getTime().convertMillisToString(
            remainingTime,
            MainConfig.kitsUseShortTime
        )

        player.sendMessage(
            LangConfig.kitsGetMessage.replace("%time%", formattedTime)
        )
    }

    private fun deliverKit(player: Player, kitName: String) {
        val kitItems = KitsData.kitItems[kitName] ?: return
        val clonedItems = kitItems.map { it.clone() }.toMutableList()

        val success = giveKit(
            player = player,
            items = clonedItems,
            autoEquipArmor = MainConfig.kitsEquipArmorInCatch,
            dropIfFull = MainConfig.kitsDropItemsInCatch
        )

        if (success) {
            updateKitClaim(player, kitName)
            sendSuccessMessage(player, kitName)
        }
    }

    private fun updateKitClaim(player: Player, kitName: String) {
        PlayerData.kitsCache[player] = hashMapOf(kitName to System.currentTimeMillis())
    }

    private fun sendSuccessMessage(player: Player, kitName: String) {
        val displayName = KitsData.kitFakeName[kitName]?.takeIf { it.isNotEmpty() } ?: kitName
        player.sendMessage(
            LangConfig.kitsGetSuccess.replace("%kit%", displayName)
        )
    }

    private fun separateArmorItems(
        player: Player,
        items: List<ItemStack>
    ): Pair<Map<ArmorSlot, ItemStack>, List<ItemStack>> {
        val availableSlots = getAvailableArmorSlots(player.inventory)
        val armorItems = mutableMapOf<ArmorSlot, ItemStack>()
        val regularItems = mutableListOf<ItemStack>()

        for (item in items) {
            val armorSlot = identifyArmorSlot(item)

            if (armorSlot != null && armorSlot in availableSlots) {
                armorItems[armorSlot] = item
                availableSlots.remove(armorSlot)
            } else {
                regularItems.add(item)
            }
        }

        return armorItems to regularItems
    }

    private fun getAvailableArmorSlots(inventory: org.bukkit.inventory.PlayerInventory): MutableSet<ArmorSlot> {
        val available = mutableSetOf<ArmorSlot>()

        if (inventory.helmet == null) available.add(ArmorSlot.HELMET)
        if (inventory.chestplate == null) available.add(ArmorSlot.CHESTPLATE)
        if (inventory.leggings == null) available.add(ArmorSlot.LEGGINGS)
        if (inventory.boots == null) available.add(ArmorSlot.BOOTS)

        return available
    }

    private fun identifyArmorSlot(item: ItemStack): ArmorSlot? {
        val typeName = item.type.name

        return when {
            typeName.contains("HELMET") -> ArmorSlot.HELMET
            typeName.contains("CHESTPLATE") -> ArmorSlot.CHESTPLATE
            typeName.contains("LEGGINGS") -> ArmorSlot.LEGGINGS
            typeName.contains("BOOTS") -> ArmorSlot.BOOTS
            else -> null
        }
    }

    private fun deliverItems(
        player: Player,
        items: List<ItemStack>,
        dropIfFull: Boolean
    ): Boolean {
        val inventory = player.inventory
        val freeSlots = (0..35).count { inventory.getItem(it) == null }

        if (dropIfFull) {
            deliverItemsWithDrop(player, items, freeSlots)
            return true
        }

        return if (freeSlots >= items.size) {
            items.forEach { inventory.addItem(it) }
            true
        } else {
            sendInsufficientSpaceMessage(player, items.size - freeSlots)
            false
        }
    }

    private fun deliverItemsWithDrop(player: Player, items: List<ItemStack>, initialFreeSlots: Int) {
        var freeSlots = initialFreeSlots

        for (item in items) {
            if (freeSlots > 0) {
                player.inventory.addItem(item)
                freeSlots--
            } else {
                player.world.dropItem(player.location, item)
            }
        }
    }

    private fun sendInsufficientSpaceMessage(player: Player, slotsNeeded: Int) {
        player.sendMessage(
            LangConfig.kitsGetNoSpace.replace("%slots%", slotsNeeded.toString())
        )
    }

    private fun equipArmor(
        inventory: org.bukkit.inventory.PlayerInventory,
        armorItems: Map<ArmorSlot, ItemStack>
    ) {
        armorItems.forEach { (slot, item) ->
            when (slot) {
                ArmorSlot.HELMET -> inventory.helmet = item
                ArmorSlot.CHESTPLATE -> inventory.chestplate = item
                ArmorSlot.LEGGINGS -> inventory.leggings = item
                ArmorSlot.BOOTS -> inventory.boots = item
            }
        }
    }

    private fun applyGlowEffect(item: ItemStack) {
        try {
            val enchantment = EnchantUtil["luck"]
            if (enchantment != null) {
                item.addUnsafeEnchantment(enchantment, 1)
            }
        } catch (e: NoSuchFieldError) {
            e.printStackTrace()
        }
    }

    private fun hideEnchantments(meta: ItemMeta) {
        try {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        } catch (e: NoClassDefFoundError) {
            // ItemFlag não disponível nesta versão
        }
    }

    /**
     * Enum para slots de armadura
     */
    private enum class ArmorSlot {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS
    }

    /**
     * Cache de reflection para métodos de ItemMeta
     */
    private class ReflectionCache {
        private var useLegacyDisplayName = false

        private val displayNameField by lazy {
            findField(ItemMeta::class.java, "displayName")
        }

        fun setItemDisplayName(meta: ItemMeta, displayName: String) {
            if (!useLegacyDisplayName) {
                try {
                    meta.setDisplayName(displayName)
                    return
                } catch (e: NoSuchMethodError) {
                    useLegacyDisplayName = true
                }
            }

            displayNameField?.set(meta, displayName)
        }

        private fun findField(clazz: Class<*>, fieldName: String): Field? {
            return try {
                clazz.getDeclaredField(fieldName).apply { isAccessible = true }
            } catch (e: NoSuchFieldException) {
                null
            }
        }
    }
}