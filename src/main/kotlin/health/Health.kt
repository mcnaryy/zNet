package net.hellz.health

data class HealthStatus(
    var health: Float,
    var head: String,
    var legs: String,
    var infection: String
)

object Health {
    val playerHealthTable = mutableMapOf<String, HealthStatus>()

    fun setPlayerHealth(playerId: String, health: Float, head: String, legs: String, infection: String) {
        playerHealthTable[playerId] = HealthStatus(health, head, legs, infection)
    }

    fun getPlayerHealth(playerId: String): HealthStatus? {
        return playerHealthTable[playerId]
    }

    fun removePlayerHealth(playerId: String) {
        playerHealthTable.remove(playerId)
    }

    fun removePlayerHealthAmount(playerId: String, amount: Float) {
        val healthStatus = playerHealthTable[playerId]
        if (healthStatus != null) {
            healthStatus.health -= amount
            if (healthStatus.health < 0) {
                healthStatus.health = 0f
            }
        }
    }
}