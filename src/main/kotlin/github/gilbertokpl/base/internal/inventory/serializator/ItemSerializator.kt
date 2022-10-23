package github.gilbertokpl.base.internal.inventory.serializator

import github.gilbertokpl.base.internal.serializator.InternalBukkitObjectInputStream
import github.gilbertokpl.base.internal.serializator.InternalBukkitObjectOutputStream
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ItemSerializator {

    fun serialize(items: MutableList<ItemStack>): String {
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
        } catch (ignored: Throwable) {
        }
        return toReturn
    }

    fun deserialize(data: String): ArrayList<ItemStack> {
        if (data == "") return ArrayList()
        val toReturn: ArrayList<ItemStack>  = ArrayList()
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
            for (i in items.filterNotNull()) {
                toReturn.add(i)
            }
        } catch (ignored: Throwable) {
        }
        return toReturn
    }
}