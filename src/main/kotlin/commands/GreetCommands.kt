package net.hellz.commands

import net.hellz.clerk.PlayerProfile
import net.minestom.server.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.annotation.Optional
import revxrsal.commands.minestom.actor.MinestomCommandActor

class GreetCommands {

    @Command("greet")
    @Description("Greets the specified player")
    fun greet(
        actor: MinestomCommandActor,
        @Optional target: Player? = null
    ) {
        // Ensure the actor is an PlayerProfile
        val executingPlayer = actor.asPlayer() as? PlayerProfile ?: run {
            actor.reply("This command can only be used by players.")
            return
        }

        // Check for permission
        if (!executingPlayer.hasPermission("commands.greet")) {
            actor.reply("You do not have permission to use this command.")
            return
        }

        // Determine the target and send the greeting
        val targetPlayer = target ?: executingPlayer
        targetPlayer.sendMessage("Welcome, ${targetPlayer.username}!")
        actor.reply("You greeted ${targetPlayer.username}.")
    }
}