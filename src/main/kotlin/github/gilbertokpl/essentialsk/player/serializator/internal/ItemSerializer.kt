package github.gilbertokpl.essentialsk.player.serializator.internal

import github.gilbertokpl.essentialsk.player.serializator.bukkit.InternalBukkitObjectInputStream
import github.gilbertokpl.essentialsk.player.serializator.bukkit.InternalBukkitObjectOutputStream
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

internal object ItemSerializer {
    fun serialize(items: List<ItemStack>): String {
        if (items.isEmpty()) return ""
        lateinit var toReturn: String
        try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = try {
                BukkitObjectOutputStream(outputStream)
            } catch (e: NoClassDefFoundError) {
                InternalBukkitObjectOutputStream(outputStream)
            }
            dataOutput.writeInt(items.size)
            for (i in items.indices) {
                dataOutput.writeObject(items[i])
            }
            dataOutput.close()
            toReturn = Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }
        return toReturn
    }

    fun deserialize(data: String): List<ItemStack> {
        if (data == "") return emptyList()
        lateinit var toReturn: List<ItemStack>
        try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = try {
                BukkitObjectInputStream(inputStream)
            } catch (e: NoClassDefFoundError) {
                InternalBukkitObjectInputStream(inputStream)
            }
            val items = arrayOfNulls<ItemStack>(dataInput.readInt())
            for (i in items.indices) {
                items[i] = dataInput.readObject() as ItemStack?
            }
            dataInput.close()
            toReturn = items.filterNotNull().toList()
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }
        return toReturn
    }
}