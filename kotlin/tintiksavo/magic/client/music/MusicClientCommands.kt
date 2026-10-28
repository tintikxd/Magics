package tintiksavo.magic.client.music

import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.network.chat.Component

object MusicClientCommands {
    fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommandManager.literal("music")
                    .then(
                        ClientCommandManager.literal("play")
                            .then(
                                ClientCommandManager.argument("track", StringArgumentType.word())
                                    .executes { context ->
                                        context.source.sendFeedback(
                                            Component.literal(
                                                MusicClientController.playPersonal(
                                                    StringArgumentType.getString(context, "track")
                                                )
                                            )
                                        )
                                        1
                                    }
                            )
                    )
                    .then(
                        ClientCommandManager.literal("stop").executes { context ->
                            context.source.sendFeedback(Component.literal(MusicClientController.stopPersonal()))
                            1
                        }
                    )
                    .then(
                        ClientCommandManager.literal("list").executes { context ->
                            val tracks = MusicClientController.listAllTracks()
                            val message = if (tracks.isEmpty()) {
                                "No hay archivos .ogg en Musics ni en assets/magic/music."
                            } else {
                                "Canciones disponibles en Musics y en el mod (${tracks.size}): ${tracks.joinToString(", ")}"
                            }
                            context.source.sendFeedback(Component.literal(message))
                            1
                        }
                    )
                    .then(
                        ClientCommandManager.literal("reload").executes { context ->
                            context.source.sendFeedback(Component.literal(MusicClientController.reload()))
                            1
                        }
                    )
            )
        }
    }
}
