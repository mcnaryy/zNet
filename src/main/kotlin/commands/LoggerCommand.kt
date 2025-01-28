package net.hellz.commands


import net.hellz.modules.EventLogger
import net.hellz.clerk.PlayerProfile
import net.minestom.server.entity.Player
import revxrsal.commands.annotation.Command

class LoggerCommand {

    @Command("logger", "eventlogger", "logs")
    fun logger(sender: Player, targetPlayer: Player?) {
        val player = sender as? PlayerProfile ?: return

        if (!player.hasPermission("commands.logger")) {
            sender.sendMessage("You do not have permission to use this command!")
            return
        }

        val target = targetPlayer ?: sender
        val logs = EventLogger.getLogs(target)

        if (logs.isNotEmpty()) {
            sender.sendMessage("Logs for ${target.username}:")
            logs.forEach { log ->
                sender.sendMessage(log)
            }
        } else {
            sender.sendMessage("No logs found for ${target.username}.")
        }
    }
}