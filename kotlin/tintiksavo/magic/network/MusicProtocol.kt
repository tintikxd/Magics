package tintiksavo.magic.network

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import tintiksavo.magic.Magic

/** Small server-to-client protocol for server-owned music layers. */
object MusicProtocol {
    val SYNC_PACKET_ID: ResourceLocation = Magic.id("music_sync")

    const val BOSS_SET = "boss_set"
    const val BOSS_CLEAR = "boss_clear"
    const val EVENT_SET = "event_set"
    const val EVENT_CLEAR = "event_clear"

    fun sendTo(player: ServerPlayer, action: String, eventId: String = "", track: String = "", gain: Float = 1.0f) {
        if (!ServerPlayNetworking.canSend(player, SYNC_PACKET_ID)) return

        val buf = PacketByteBufs.create()
        buf.writeUtf(action)
        buf.writeUtf(eventId)
        buf.writeUtf(track)
        buf.writeFloat(gain.coerceIn(0.0f, 3.0f))
        ServerPlayNetworking.send(player, SYNC_PACKET_ID, buf)
    }

    fun broadcast(server: MinecraftServer, action: String, eventId: String = "", track: String = "", gain: Float = 1.0f) {
        server.playerList.players.forEach { player ->
            sendTo(player, action, eventId, track, gain)
        }
    }
}