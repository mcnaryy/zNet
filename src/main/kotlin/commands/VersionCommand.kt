package net.hellz.commands

import net.minestom.server.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.minestom.actor.MinestomCommandActor


class VersionCommand {
    @Command("version", "ver")
    fun version(sender: Player) {
        sender.sendMessage("You are currently running zNet v0.1!")
    }
}