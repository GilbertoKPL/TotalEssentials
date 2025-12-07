package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PermissionUtil {

    private const val MAX_PERMISSION_VALUE = 1000
    private const val COLOR_ALL_PERMISSION = "totalessentials.color.*"

    /**
     * Obtém o maior número de permissão que um jogador possui
     *
     * Exemplo: Se o jogador tem "sethome.5" e "sethome.10", retorna 10
     *
     * @param player Jogador para verificar permissões
     * @param permissionPrefix Prefixo da permissão (ex: "totalessentials.commands.sethome.")
     * @param defaultValue Valor padrão se nenhuma permissão numérica for encontrada
     * @return O maior número encontrado nas permissões ou o valor padrão
     */
    fun getNumberPermission(
        player: Player?,
        permissionPrefix: String,
        defaultValue: Int
    ): Int {
        if (player == null || !player.isOnline) return defaultValue

        return try {
            val maxPermissionValue = if (TotalEssentials.isLowVersion()) {
                findMaxPermissionLegacy(player, permissionPrefix)
            } else {
                findMaxPermissionModern(player, permissionPrefix)
            }

            if (maxPermissionValue > 0) maxPermissionValue else defaultValue
        } catch (ex: Exception) {
            logPermissionError(player, ex)
            defaultValue
        }
    }

    /**
     * Aplica coloração ao texto baseado nas permissões do jogador
     *
     * @param player Jogador (null para aplicar todas as cores)
     * @param message Mensagem com códigos de cor (&, #)
     * @return Mensagem com cores aplicadas conforme permissões
     */
    fun colorPermission(player: Player?, message: String): String {
        if (!hasColorCodes(message)) return message

        val colorApi = TotalEssentials.getCore().getColor()

        return when {
            player == null -> colorApi.rgbHex(null, message)
            player.hasPermission(COLOR_ALL_PERMISSION) -> colorApi.rgbHex(player, message)
            else -> colorApi.color(player, message)
        }
    }

    /**
     * Busca a maior permissão numérica em versões antigas
     * Itera de 0 até MAX_PERMISSION_VALUE procurando permissões
     */
    private fun findMaxPermissionLegacy(player: Player, permissionPrefix: String): Int {
        return (0..MAX_PERMISSION_VALUE)
            .filter { number -> player.hasPermission("$permissionPrefix$number") }
            .maxOrNull() ?: 0
    }

    /**
     * Busca a maior permissão numérica em versões modernas
     * Usa effectivePermissions para performance melhorada
     */
    private fun findMaxPermissionModern(player: Player, permissionPrefix: String): Int {
        return player.effectivePermissions
            .asSequence()
            .mapNotNull { permissionAttachment ->
                extractPermissionNumber(permissionAttachment.permission, permissionPrefix)
            }
            .maxOrNull() ?: 0
    }

    /**
     * Extrai o número de uma permissão se ela começar com o prefixo
     *
     * Exemplo: "totalessentials.sethome.10" com prefixo "totalessentials.sethome." → 10
     */
    private fun extractPermissionNumber(permission: String, prefix: String): Int? {
        if (!permission.startsWith(prefix)) return null

        return permission
            .substringAfterLast(".")
            .toIntOrNull()
    }

    /**
     * Verifica se a mensagem contém códigos de cor (&) ou hex (#)
     */
    private fun hasColorCodes(message: String): Boolean {
        return message.contains('&') || message.contains('#')
    }

    /**
     * Registra erro ao verificar permissões
     */
    private fun logPermissionError(player: Player, exception: Exception) {
        val logger = Bukkit.getLogger()
        logger.severe("[TotalEssentials] Error checking permissions for player ${player.name}")
        logger.severe("[TotalEssentials] Error details: ${exception.message}")
        exception.printStackTrace()
    }
}