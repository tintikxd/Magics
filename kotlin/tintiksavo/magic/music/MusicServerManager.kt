package tintiksavo.magic.music

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.commands.Commands
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import tintiksavo.magic.network.MusicProtocol
import java.util.UUID

/**
 * Stores server-owned music state. The state is deliberately independent from
 * entities and dimensions, so boss music survives death and dimension changes.
 */
object MusicServerManager {
    private data class EventAssignment(val eventId: String, val track: String)

    @Volatile
    private var bossTrack: String? = null
    private var bossVolume: Float = 1.0f
    private val eventParticipants = mutableMapOf<UUID, EventAssignment>()

    fun register() {
        registerCommands()

        ServerPlayConnectionEvents.JOIN.register { handler, _, server ->
            val player = handler.player
            server.execute {
                bossTrack?.let { MusicProtocol.sendTo(player, MusicProtocol.BOSS_SET, track = it, gain = bossVolume) }
                eventParticipants[player.uuid]?.let { assignment ->
                    MusicProtocol.sendTo(
                        player,
                        MusicProtocol.EVENT_SET,
                        eventId = assignment.eventId,
                        track = assignment.track
                    )
                }
            }
        }

        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            // Disconnecting means that this player is no longer participating.
            server.execute { eventParticipants.remove(handler.player.uuid) }
        }

        ServerLivingEntityEvents.AFTER_DEATH.register { entity, _ ->
            if (entity is ServerPlayer) {
                val assignment = eventParticipants.remove(entity.uuid)
                if (assignment != null) {
                    MusicProtocol.sendTo(
                        entity,
                        MusicProtocol.EVENT_CLEAR,
                        eventId = assignment.eventId
                    )
                }
            }
        }

        ServerLifecycleEvents.SERVER_STARTED.register {
            bossTrack = null
            bossVolume = 1.0f
            eventParticipants.clear()
        }
        ServerLifecycleEvents.SERVER_STOPPED.register {
            bossTrack = null
            bossVolume = 1.0f
            eventParticipants.clear()
        }
    }

    private fun registerCommands() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("modmusic")
                    .requires { it.hasPermission(2) }
                    .then(
                        Commands.literal("play")
                            .then(
                                Commands.argument("track", StringArgumentType.word())
                                    .executes { context ->
                                        activateBossMusic(
                                            context.source,
                                            StringArgumentType.getString(context, "track"),
                                            1.0f
                                        )
                                    }
                                    .then(
                                        Commands.argument("volume", FloatArgumentType.floatArg(0.5f, 3.0f))
                                            .executes { context ->
                                                val volume = FloatArgumentType.getFloat(context, "volume")
                                                if (listOf(0.5f, 1.0f, 2.0f, 3.0f).none { kotlin.math.abs(it - volume) < 0.001f }) {
                                                    context.source.sendFailure(
                                                        Component.literal("Volumen inválido. Usa 0.5, 1, 2 o 3.")
                                                    )
                                                    return@executes 0
                                                }
                                                activateBossMusic(
                                                    context.source,
                                                    StringArgumentType.getString(context, "track"),
                                                    volume
                                                )
                                            }
                                    )
                            )
                    )
                    .then(
                        Commands.literal("stop").executes { context ->
                            bossTrack = null
                            MusicProtocol.broadcast(context.source.server, MusicProtocol.BOSS_CLEAR)
                            context.source.sendSuccess(
                                { Component.literal("Música de jefe detenida.") },
                                true
                            )
                            1
                        }
                    )
            )

            dispatcher.register(
                Commands.literal("eventmusic")
                    .then(
                        Commands.literal("start")
                            .requires { it.hasPermission(2) }
                            .then(
                                Commands.argument("event", StringArgumentType.word())
                                    .then(
                                        Commands.argument("track", StringArgumentType.word())
                                            .then(
                                                Commands.argument("players", EntityArgument.players())
                                                    .executes { context ->
                                                        val eventId = MusicTrackNames.normalizeEvent(
                                                            StringArgumentType.getString(context, "event")
                                                        )
                                                        val track = MusicTrackNames.normalizeTrack(
                                                            StringArgumentType.getString(context, "track")
                                                        )
                                                        if (eventId == null || track == null) {
                                                            context.source.sendFailure(
                                                                Component.literal("Nombre de evento o música inválido.")
                                                            )
                                                            return@executes 0
                                                        }

                                                        val targets = EntityArgument.getPlayers(context, "players")
                                                        targets.forEach { player ->
                                                            eventParticipants[player.uuid] = EventAssignment(eventId, track)
                                                            MusicProtocol.sendTo(
                                                                player,
                                                                MusicProtocol.EVENT_SET,
                                                                eventId = eventId,
                                                                track = track
                                                            )
                                                        }
                                                        context.source.sendSuccess(
                                                            {
                                                                Component.literal(
                                                                    "Música de evento '$eventId' activada para ${targets.size} jugador(es): $track.ogg"
                                                                )
                                                            },
                                                            true
                                                        )
                                                        targets.size
                                                    }
                                            )
                                    )
                            )
                    )
                    .then(
                        Commands.literal("stop")
                            .requires { it.hasPermission(2) }
                            .then(
                                Commands.argument("event", StringArgumentType.word())
                                    .executes { context ->
                                        val eventId = MusicTrackNames.normalizeEvent(
                                            StringArgumentType.getString(context, "event")
                                        )
                                        if (eventId == null) {
                                            context.source.sendFailure(Component.literal("Nombre de evento inválido."))
                                            return@executes 0
                                        }
                                        val stopped = stopEvent(context.source.server, eventId)
                                        context.source.sendSuccess(
                                            { Component.literal("Evento '$eventId' detenido para $stopped jugador(es).") },
                                            true
                                        )
                                        stopped
                                    }
                            )
                    )
                    .then(
                        Commands.literal("leave").executes { context ->
                            val player = context.source.entity as? ServerPlayer
                            if (player == null) {
                                context.source.sendFailure(Component.literal("Este comando solo lo puede usar un jugador."))
                                return@executes 0
                            }
                            val assignment = eventParticipants.remove(player.uuid)
                            if (assignment == null) {
                                context.source.sendFailure(Component.literal("No estás participando en un evento con música."))
                                return@executes 0
                            }
                            MusicProtocol.sendTo(
                                player,
                                MusicProtocol.EVENT_CLEAR,
                                eventId = assignment.eventId
                            )
                            context.source.sendSuccess(
                                { Component.literal("Saliste del evento '${assignment.eventId}'.") },
                                false
                            )
                            1
                        }
                    )
            )
        }
    }

    private fun activateBossMusic(source: CommandSourceStack, rawTrack: String, volume: Float): Int {
        val track = MusicTrackNames.normalizeTrack(rawTrack)
        if (track == null) {
            source.sendFailure(
                Component.literal("Nombre inválido. Usa letras, números, _, - o . (sin rutas).")
            )
            return 0
        }
        bossTrack = track
        bossVolume = volume
        MusicProtocol.broadcast(
            source.server,
            MusicProtocol.BOSS_SET,
            track = track,
            gain = volume
        )
        source.sendSuccess(
            { Component.literal("Música de jefe activada: $track.ogg (volumen $volume)") },
            true
        )
        return 1
    }

    private fun stopEvent(server: MinecraftServer, eventId: String): Int {
        val matching = eventParticipants.entries
            .filter { it.value.eventId.equals(eventId, ignoreCase = true) }
            .map { it.key to it.value }

        matching.forEach { (uuid, assignment) ->
            eventParticipants.remove(uuid)
            server.playerList.getPlayer(uuid)?.let { player ->
                MusicProtocol.sendTo(
                    player,
                    MusicProtocol.EVENT_CLEAR,
                    eventId = assignment.eventId
                )
            }
        }
        return matching.size
    }
}
