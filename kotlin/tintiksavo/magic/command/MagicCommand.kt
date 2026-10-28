package tintiksavo.magic.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack

/** Contrato de comandos usado por ModCommands. */
interface MagicCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>)
}
