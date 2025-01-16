package net.hellz.clerk.luckperms

import me.lucko.luckperms.minestom.context.ContextProvider
import net.minestom.server.entity.Player
import java.util.*


class DummyContextProvider : ContextProvider {
    override fun key(): String {
        return "dummy"
    }

    override fun query(subject: Player): Optional<String> {
        return Optional.of("true")
    }
}