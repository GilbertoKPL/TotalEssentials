package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.weather.WeatherChangeEvent

class WeatherChangeEvent : Listener {
    @EventHandler
    fun event(e: WeatherChangeEvent) {
        if (MainConfig.getInstance().addonsDisableRain) {
            try {
                disableRain(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun disableRain(e: WeatherChangeEvent) {
        e.isCancelled = true
    }
}