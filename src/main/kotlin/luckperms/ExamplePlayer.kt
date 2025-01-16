package net.hellz.luckperms

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPerms
import net.luckperms.api.cacheddata.CachedMetaData
import net.luckperms.api.model.data.DataMutateResult
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import net.luckperms.api.util.Tristate
import net.minestom.server.entity.Player
import net.minestom.server.network.player.GameProfile
import net.minestom.server.network.player.PlayerConnection
import java.util.concurrent.CompletableFuture


/**
 * An example implementation of permission handling in a Player using LuckPerms.
 * This class is a simple example and is not intended for production use.
 * Every situation is different, and you should consider your own requirements when implementing permission handling.
 */
class ExamplePlayer(private val luckPerms: LuckPerms, profile: GameProfile, connection: PlayerConnection) :
    Player(connection, profile) {
    private val playerAdapter = luckPerms.getPlayerAdapter(
        Player::class.java
    )

    private val luckPermsUser: User
        get() = playerAdapter.getUser(this)

    private val luckPermsMetaData: CachedMetaData
        get() = luckPermsUser.cachedData.metaData

    /**
     * Adds a permission to the player. You may choose not to implement
     * this method on a production server, and leave permission management
     * to the LuckPerms web interface or in-game commands.
     *
     * @param permission the permission to add
     * @return the result of the operation
     */
    fun addPermission(permission: String): CompletableFuture<DataMutateResult> {
        val user = this.luckPermsUser
        val result = user.data().add(Node.builder(permission).build())
        return luckPerms.userManager.saveUser(user).thenApply { ignored: Void? -> result }
    }

    /**
     * Sets a permission for the player. This method uses a [Node] rather
     * than a permission name, this allows for permissions that rely on context.
     * You may choose not to implement this method on a production server, and
     * leave permission management to the LuckPerms web interface or in-game
     * commands.
     *
     * @param permission the permission to set
     * @param value the value of the permission
     * @return the result of the operation
     */
    fun setPermission(permission: Node, value: Boolean): CompletableFuture<DataMutateResult> {
        val user = this.luckPermsUser
        val result = if (value)
            user.data().add(permission)
        else
            user.data().remove(permission)
        return luckPerms.userManager.saveUser(user).thenApply { ignored: Void? -> result }
    }

    /**
     * Removes a permission from the player. You may choose not to implement
     * this method on a production server, and leave permission management
     * to the LuckPerms web interface or in-game commands.
     *
     * @param permissionName the name of the permission to remove
     */
    fun removePermission(permissionName: String): CompletableFuture<DataMutateResult> {
        val user = this.luckPermsUser
        val result = user.data().remove(Node.builder(permissionName).build())
        return luckPerms.userManager.saveUser(user).thenApply { ignored: Void? -> result }
    }

    /**
     * Checks if the player has a permission.
     *
     * @param permissionName the name of the permission to check
     * @return true if the player has the permission
     */
    fun hasPermission(permissionName: String): Boolean {
        return getPermission(permissionName).asBoolean()
    }

    /**
     * Gets the value of a permission. This passes a [Tristate] value
     * straight from LuckPerms, which may be a better option than using
     * boolean values in some cases.
     *
     * @param permissionName the name of the permission to check
     * @return the value of the permission
     */
    fun getPermission(permissionName: String): Tristate {
        val user = this.luckPermsUser
        return user.cachedData.permissionData.checkPermission(permissionName)
    }

    val prefix: Component
        /**
         * Gets the prefix of the player. This method uses the MiniMessage library
         * to parse the prefix, which is a more advanced option than using legacy
         * chat formatting.
         *
         * @return the prefix of the player
         */
        get() {
            val prefix = luckPermsMetaData.prefix ?: return Component.empty()
            return MINI_MESSAGE.deserialize(prefix)
        }

    val suffix: Component
        /**
         * Gets the suffix of the player. This method uses the MiniMessage library
         * to parse the suffix, which is a more advanced option than using legacy
         * chat formatting.
         *
         * @return the suffix of the player
         */
        get() {
            val suffix = luckPermsMetaData.suffix ?: return Component.empty()
            return MINI_MESSAGE.deserialize(suffix)
        }

    companion object {
        private val MINI_MESSAGE = MiniMessage.miniMessage()
    }
}