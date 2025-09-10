package github.gilbertokpl.core.command

import github.gilbertokpl.core.TotalCore
import github.gilbertokpl.core.command.interfaces.ICommand
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.LangConfig.generalCommandsUsage
import github.gilbertokpl.total.config.files.LangConfig.generalCommandsUsageList
import github.gilbertokpl.total.config.files.LangConfig.generalCooldownMoreTime
import github.gilbertokpl.total.config.files.LangConfig.generalNotPerm
import github.gilbertokpl.total.config.files.LangConfig.generalOnlyConsoleCommand
import github.gilbertokpl.total.config.files.LangConfig.generalOnlyPlayerCommand
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

abstract class CommandManager(name: String) : BukkitCommand(name), ICommand {

    private val hashCountDown: MutableMap<Player, Long> = HashMap()

    var totalCore: TotalCore? = null
    var active: Boolean = true
    var target: CommandTargetType = CommandTargetType.ALL
    var commandUsage: List<String> = emptyList()
    var countdown: Long? = 0L
    var minimumSize: Int? = 0
    var maximumSize: Int? = 0

    // =========================================================
    // Main execution entry
    // =========================================================
    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val configMessages = totalCore?.getConfig()?.messages ?: return true

        // Validate target type (player / console)
        if (!isValidTarget(sender, configMessages)) return true

        // Validate permission
        if (!hasPermission(sender, configMessages)) return true

        // Validate arguments size
        if (!isValidArgsSize(sender, args, configMessages)) return true

        // Validate cooldown
        if (!isValidCooldown(sender, configMessages)) return true

        // Execute custom command logic
        val hasError = funCommand(sender, label, args)

        // Apply cooldown if execution succeeded
        if (!hasError && countdown != null && sender is Player) {
            hashCountDown[sender] = System.currentTimeMillis() + (countdown!! * 1000)
        }

        // Send usage if error occurred
        if (hasError) {
            sendUsage(sender, configMessages)
        }

        return true
    }

    // =========================================================
    // Validation helpers
    // =========================================================

    private fun isValidTarget(sender: CommandSender, messages: Any): Boolean {
        when (sender) {
            !is Player if target == CommandTargetType.PLAYER -> {
                sender.sendMessage(generalOnlyPlayerCommand!!)
                return false
            }

            is Player if target == CommandTargetType.CONSOLE -> {
                sender.sendMessage(generalOnlyConsoleCommand!!)
                return false
            }
        }
        return true
    }

    private fun hasPermission(sender: CommandSender, messages: Any): Boolean {
        if (sender is Player && !permission.isNullOrEmpty() && !sender.hasPermission(permission!!)) {
            sender.sendMessage(generalNotPerm!!)
            return false
        }
        return true
    }

    private fun isValidArgsSize(sender: CommandSender, args: Array<out String>, messages: Any): Boolean {
        val tooMany = maximumSize != null && args.size > maximumSize!!
        val tooFew = minimumSize != null && args.size < minimumSize!!
        if (tooMany || tooFew) {
            sendUsage(sender, messages)
            return false
        }
        return true
    }

    private fun isValidCooldown(sender: CommandSender, messages: Any): Boolean {
        if (countdown == null || sender !is Player) return true

        val time = hashCountDown.getOrDefault(sender, 0)
        if (time != 0L && System.currentTimeMillis() < time) {
            val timeString = totalCore?.getTime()
                ?.convertMillisToString(time - System.currentTimeMillis(), true)
            sender.sendMessage(generalCooldownMoreTime!!.replace("%time%", timeString!!))
            return false
        }
        return true
    }

    // =========================================================
    // Usage message
    // =========================================================
    private fun sendUsage(sender: CommandSender, messages: Any) {
        val sb = StringBuilder()
        sb.append(generalCommandsUsage!!).append("\n")

        for (usage in commandUsage) {
            val parts = usage.split("_")

            // Simple usage without permission check
            if (parts.size == 1) {
                sb.append(generalCommandsUsageList!!.replace("%command%", usage)).append("\n")
                continue
            }

            // Skip based on sender type
            if ((parts[0] == "C" && sender is Player) || (parts[0] == "P" && sender !is Player)) {
                continue
            }

            // Append usage if permission is valid
            if (sender !is Player || sender.hasPermission(parts[0])) {
                sb.append(generalCommandsUsageList!!.replace("%command%", parts[1])).append("\n")
            }
        }

        sender.sendMessage(sb.toString())
    }

}
