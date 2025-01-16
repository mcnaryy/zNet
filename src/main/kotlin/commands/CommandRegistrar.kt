package net.hellz.commands

import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.Command
import kotlin.reflect.full.createInstance

object CommandRegistrar {
    fun registerCommands(){
        val packageName = "net.hellz.commands"
        val reflections = org.reflections.Reflections(packageName)

        val commandClasses = reflections.getSubTypesOf(Command::class.java)

        for (commandClass in commandClasses) {
            val commandInstance = commandClass.kotlin.createInstance() as Command
            MinecraftServer.getCommandManager().register(commandInstance)
        }
    }
}