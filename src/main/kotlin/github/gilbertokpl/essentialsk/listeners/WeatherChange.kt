package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.weather.WeatherChangeEvent

class WeatherChange : Listener {
    @EventHandler
    fun event(e: WeatherChangeEvent) {
        if (MainConfig.addonsDisableRain) {
            try {
                disableRain(e)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun disableRain(e: WeatherChangeEvent) {
        e.isCancelled = true
    }
}
