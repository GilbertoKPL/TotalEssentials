package github.gilbertokpl.essentialsk.economy

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

    override fun hasAccount(playerName: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasAccount(player: OfflinePlayer?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasAccount(playerName: String?, worldName: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasAccount(player: OfflinePlayer?, worldName: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBalance(playerName: String?): Double {
        TODO("Not yet implemented")
    }

    override fun getBalance(player: OfflinePlayer?): Double {
        TODO("Not yet implemented")
    }

    override fun getBalance(playerName: String?, world: String?): Double {
        TODO("Not yet implemented")
    }

    override fun getBalance(player: OfflinePlayer?, world: String?): Double {
        TODO("Not yet implemented")
    }

    override fun has(playerName: String?, amount: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun has(player: OfflinePlayer?, amount: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun has(playerName: String?, worldName: String?, amount: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun has(player: OfflinePlayer?, worldName: String?, amount: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(playerName: String?, amount: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(player: OfflinePlayer?, worldName: String?, amount: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun depositPlayer(playerName: String?, amount: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun depositPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun createPlayerAccount(player: OfflinePlayer?): Boolean {
        TODO("Not yet implemented")
    }

    override fun createPlayerAccount(playerName: String?, worldName: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun createPlayerAccount(player: OfflinePlayer?, worldName: String?): Boolean {
        TODO("Not yet implemented")
    }
}