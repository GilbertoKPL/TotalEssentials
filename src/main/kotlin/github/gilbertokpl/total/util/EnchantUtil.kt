package github.gilbertokpl.total.util

import org.bukkit.enchantments.Enchantment

internal object EnchantUtil {

    private val enchants = HashMap<String, Enchantment>(10)

    operator fun get(mat: String) = enchants[mat.lowercase()]

    fun startEnchantments() {
        enchants["luck"] = enchantmentHelper(listOf("LUCK", "LURE", "LOOT_BONUS_MOBS")) ?: return
    }

    private fun enchantmentHelper(name: List<String>): Enchantment? {
        for (i in name) {
            val enchant = Enchantment.getByName(i)
            if (enchant != null) {
                return enchant
            }
        }
        return null
    }
}