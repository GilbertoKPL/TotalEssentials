package github.gilbertokpl.essentialsk.economy

import github.gilbertokpl.essentialsk.player.PlayerData
import github.gilbertokpl.essentialsk.player.modify.MoneyCache.addMoney
import github.gilbertokpl.essentialsk.player.modify.MoneyCache.getMoney
import github.gilbertokpl.essentialsk.player.modify.MoneyCache.takeMoney
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer

class EconomyHolder : Economy {
    override fun isEnabled(): Boolean {
        return true
    }

    override fun getName(): String {
        return "EssentialsK-Economy"
    }

    override fun hasBankSupport(): Boolean {
        return false
    }

    override fun fractionalDigits(): Int {
        return 0
    }

    override fun format(amount: Double): String {
        TODO("Not yet implemented")
    }

    override fun currencyNamePlural(): String {
        return "coins"
    }

    override fun currencyNameSingular(): String {
        return "coin"
    }

    override fun hasAccount(playerName: String): Boolean {
        return PlayerData[playerName] != null
    }

    override fun hasAccount(player: OfflinePlayer): Boolean {
        return PlayerData[player] != null
    }

    override fun hasAccount(playerName: String, worldName: String): Boolean {
        return hasAccount(playerName)
    }

    override fun hasAccount(player: OfflinePlayer, worldName: String): Boolean {
        return hasAccount(player)
    }

    override fun getBalance(playerName: String): Double {
        return PlayerData[playerName]?.getMoney() ?: 0.0
    }

    override fun getBalance(player: OfflinePlayer): Double {
        return PlayerData[player]?.getMoney() ?: 0.0
    }

    override fun getBalance(playerName: String, world: String?): Double {
        return getBalance(playerName)
    }

    override fun getBalance(player: OfflinePlayer, world: String?): Double {
        return getBalance(player)
    }

    override fun has(playerName: String, amount: Double): Boolean {
        return amount < (PlayerData[playerName]?.moneyCache ?: return false)
    }

    override fun has(player: OfflinePlayer, amount: Double): Boolean {
        return amount < (PlayerData[player]?.moneyCache ?: return false)
    }

    override fun has(playerName: String, worldName: String?, amount: Double): Boolean {
        return has(playerName, amount)
    }

    override fun has(player: OfflinePlayer, worldName: String?, amount: Double): Boolean {
        return has(player, amount)
    }

    override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse {
        val playerData = PlayerData[playerName]
        return withdrawPlayer(playerData, amount)
    }

    override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        val playerData = PlayerData[player]
        return withdrawPlayer(playerData, amount)
    }

    private fun withdrawPlayer(playerData: PlayerData?, amount: Double) : EconomyResponse {
        if (playerData != null && amount < playerData.moneyCache ) {
            playerData.takeMoney(amount)
            return EconomyResponse(amount, playerData.moneyCache, EconomyResponse.ResponseType.SUCCESS, "")
        }
        return EconomyResponse(amount, playerData?.moneyCache ?: 0.0, EconomyResponse.ResponseType.FAILURE, "player does not have a money")
    }

    override fun withdrawPlayer(playerName: String, worldName: String?, amount: Double): EconomyResponse {
        return withdrawPlayer(playerName, amount)
    }

    override fun withdrawPlayer(player: OfflinePlayer, worldName: String?, amount: Double): EconomyResponse {
       return withdrawPlayer(player, amount)
    }

    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse {
        val playerData = PlayerData[playerName]
        return depositPlayer(playerData, amount)
    }

    override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        val playerData = PlayerData[player]
        return depositPlayer(playerData, amount)
    }

    private fun depositPlayer(playerData: PlayerData?, amount: Double) : EconomyResponse {
        if (playerData != null) {
            playerData.addMoney(amount)
            return EconomyResponse(amount, playerData.moneyCache, EconomyResponse.ResponseType.SUCCESS, "")
        }
        return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "player does not have a money")
    }

    override fun depositPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun depositPlayer(player: OfflinePlayer?, worldName: String?, amount: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun createBank(name: String?, player: String?): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun createBank(name: String?, player: OfflinePlayer?): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun deleteBank(name: String?): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankBalance(name: String?): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankHas(name: String?, amount: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankWithdraw(name: String?, amount: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankDeposit(name: String?, amount: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun isBankOwner(name: String?, playerName: String?): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun isBankOwner(name: String?, player: OfflinePlayer?): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun isBankMember(name: String?, playerName: String?): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun isBankMember(name: String?, player: OfflinePlayer?): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun getBanks(): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun createPlayerAccount(playerName: String?): Boolean {
        return true
    }

    override fun createPlayerAccount(player: OfflinePlayer?): Boolean {
        return true
    }

    override fun createPlayerAccount(playerName: String?, worldName: String?): Boolean {
        return true
    }

    override fun createPlayerAccount(player: OfflinePlayer?, worldName: String?): Boolean {
        return true
    }
}