package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
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
                e.printStackTrace()
            }
        }
    }

    private fun disableRain(e: WeatherChangeEvent) {
        e.isCancelled = true
    }
}
