package net.hellz.commands

import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player



/**
 *  class VersionCommand : Command("version", "ver"){
 *     init {
 *         // Permission condition for command
 *         setCondition { sender, context ->
 *             sender !is Player || sender.hasPermission(Permission("commands.version"))
 *         }
 *
 *         setDefaultExecutor { sender, context ->
 *             sender.sendMessage("You are currently playing HellZ v0.1")
 *         }
 *     }
 * }
 */