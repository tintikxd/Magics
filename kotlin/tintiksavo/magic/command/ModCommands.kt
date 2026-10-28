package tintiksavo.magic.command

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import tintiksavo.magic.dimension.ModDimensions

/** Registro del conjunto de comandos incluido en el JAR. */
object ModCommands {
    private val commands: List<MagicCommand> = listOf(TurnIntoCommand)

    fun register() {
        CommandRegistrationCallback.EVENT.register { dispatcher: CommandDispatcher<CommandSourceStack>, _: CommandBuildContext, _: Commands.CommandSelection ->
            commands.forEach { it.register(dispatcher) }
            ModDimensions.registerCommand(dispatcher)
        }
    }
}
