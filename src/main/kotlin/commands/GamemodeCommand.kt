package net.hellz.commands

import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player


class GamemodeCommand : Command("gamemode", "gm") {
    init {
        val gameModeArg = ArgumentType.Enum("gameMode", GameMode::class.java)

        // Set the default executor for the command
        defaultExecutor = CommandExecutor { sender: CommandSender, context: CommandContext? ->
            sender.sendMessage("Usage: /gamemode <creative / survival>")
        }

        val gamemodeArg = ArgumentType.String("gamemode")
        addSyntax({ sender: CommandSender, context: CommandContext ->
            val gamemode = context.get(gamemodeArg)
            if (sender is Player) {
                when (gamemode) {
                    "creative" -> {
                        sender.setGameMode(GameMode.CREATIVE)
                        sender.sendMessage("You have switched to Creative mode.")
                    }
                    "survival" -> {
                        sender.setGameMode(GameMode.SURVIVAL)
                        sender.sendMessage("You have switched to Survival mode.")
                    }
                    "adventure" -> {
                        sender.setGameMode(GameMode.ADVENTURE)
                        sender.sendMessage("You have switched to Adventure mode.")
                    }
                    "spectator" -> {
                        sender.setGameMode(GameMode.SPECTATOR)
                        sender.sendMessage("You have switched to Spectator mode.")
                    }
                    else -> sender.sendMessage("Invalid gamemode")
                }
            } else {
                sender.sendMessage("You must be a player to execute this command.")
            }
        }, gamemodeArg)


    }
}

