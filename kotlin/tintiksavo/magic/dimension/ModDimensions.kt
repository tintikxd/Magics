package tintiksavo.magic.dimension

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import tintiksavo.magic.Magic

object ModDimensions {

    /** Coincide con data/magic/dimension/circus.json */
    val CIRCUS: ResourceKey<Level> = ResourceKey.create(Registries.DIMENSION, Magic.id("circus"))

    fun enterCircus(player: ServerPlayer) {
        val level = player.server.getLevel(CIRCUS) ?: return
        // Capas del flat: bedrock y=0, piedra y=1..3, circus_block y=4 -> se pisa en y=5
        player.teleportTo(level, 0.5, 5.0, 0.5, player.yRot, player.xRot)
    }

    fun leaveCircus(player: ServerPlayer) {
        val target = player.server.getLevel(player.respawnDimension) ?: player.server.overworld()
        val pos = player.respawnPosition ?: target.sharedSpawnPos
        player.teleportTo(target, pos.x + 0.5, pos.y + 1.0, pos.z + 0.5, player.yRot, player.xRot)
    }

    /** Llama a esto desde tu ModCommands: ModDimensions.registerCommand(dispatcher) */
    fun registerCommand(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("circus")
                .requires { it.hasPermission(2) }
                .executes { ctx ->
                    val player = ctx.source.playerOrException
                    if (player.level().dimension() == CIRCUS) leaveCircus(player) else enterCircus(player)
                    1
                }
        )
    }
}
