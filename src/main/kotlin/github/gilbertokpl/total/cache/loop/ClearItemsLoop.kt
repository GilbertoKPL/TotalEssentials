package github.gilbertokpl.total.cache.loop

import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.TaskUtil
import github.gilbertokpl.total.util.WorldUtil
import java.util.concurrent.TimeUnit

/**
 * Classe que executa um loop de limpeza de itens.
 * Remove todos os itens do mundo em intervalos regulares.
 */
object ClearItemsLoop
{
    private val CLEAR_ITEMS_INTERVAL_MINUTES = MainConfig.clearitemsTime.toLong()
    /**
     * Inicia o loop de limpeza de itens.
     */
    fun start() {
        TaskUtil.getInternalExecutor().scheduleWithFixedDelay(::clearItems, CLEAR_ITEMS_INTERVAL_MINUTES, CLEAR_ITEMS_INTERVAL_MINUTES, TimeUnit.MINUTES)
    }

    /**
     * Remove todos os itens do mundo.
     */
    private fun clearItems() {
        WorldUtil.clearItems()
    }
}