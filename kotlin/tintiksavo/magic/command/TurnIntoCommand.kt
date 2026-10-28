package tintiksavo.magic.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.server.level.ServerPlayer

object TurnIntoCommand : MagicCommand {
    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("turn").then(
                Commands.literal("into").requires { it.hasPermission(2) }
                    .then(Commands.literal("clear").executes { ctx ->
                        val player = ctx.source.playerOrException
                        BossTransformationManager.clear(player)
                        ctx.source.sendSuccess({ Component.literal("Transformation cleared.") }, true)
                        1
                    })
                    .then(Commands.literal("form").then(
                        Commands.argument("form", StringArgumentType.word())
                            .suggests { _, builder -> SharedSuggestionProvider.suggest(TransformForms.ids(), builder) }
                            .executes { ctx ->
                                executeTransform(ctx.source, ctx.source.playerOrException, StringArgumentType.getString(ctx, "form"))
                            }
                            .then(Commands.literal("target").then(
                                Commands.argument("target", EntityArgument.player()).executes { ctx ->
                                    executeTransform(ctx.source, EntityArgument.getPlayer(ctx, "target"), StringArgumentType.getString(ctx, "form"))
                                }
                            ))
                    ))
            )
        )
    }

    private fun executeTransform(source: CommandSourceStack, player: ServerPlayer, form: String): Int {
        if (!BossTransformationManager.transform(player, form)) {
            source.sendFailure(Component.literal("No form found: $form"))
            return 0
        }
        source.sendSuccess({ Component.literal("${player.name.string} transformed into $form") }, true)
        return 1
    }
}
