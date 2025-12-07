package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.weather.WeatherChangeEvent


class WeatherChange : Listener {

    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent) {
        if (MainConfig.addonsDisableRain && event.toWeatherState()) {
            event.isCancelled = true
        }
    }
}
