package tintiksavo.magic.client.music

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundSource
import net.minecraft.network.chat.Component
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import org.slf4j.LoggerFactory
import tintiksavo.magic.network.MusicProtocol
import tintiksavo.magic.music.MusicTrackNames

/**
 * Client-side priority controller. Server-owned layers are kept separate from
 * the local personal layer, allowing music to resume correctly when a higher
 * priority layer is removed.
 */
object MusicClientController {
    private enum class Layer { AMBIENT, PERSONAL, EVENT, BOSS }
    private data class Selection(val layer: Layer, val track: String)

    private val logger = LoggerFactory.getLogger("magic-music-controller")

    private var ambientDimension: String? = null
    private var ambientTrack: String? = null
    private var personalTrack: String? = null
    private var eventId: String? = null
    private var eventTrack: String? = null
    private var bossTrack: String? = null
    private var bossVolume: Float = 1.0f
    private var savedBossVolume: Float = 1.0f
    private var volumeConfigPath: Path? = null
    private var appliedSelection: Selection? = null
    private var volume: Float = 1.0f

    fun register() {
        MusicFiles.initialize(Minecraft.getInstance().gameDirectory.toPath())
        loadSavedBossVolume()
        registerPackets()
        MusicClientCommands.register()

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            updateDimension(client)
            updateVolume(client)
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, client ->
            client.execute { clearServerOwnedTracks() }
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            ExternalAudioPlayer.stop()
        }
    }

    private fun registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(MusicProtocol.SYNC_PACKET_ID) { client, _, buf, _ ->
            // Read all data on the network thread, then mutate music state on the client thread.
            val action = buf.readUtf(32)
            val incomingEventId = buf.readUtf(64)
            val incomingTrack = buf.readUtf(64)
            val incomingGain = buf.readFloat().coerceIn(0.0f, 3.0f)
            client.execute {
                when (action) {
                    MusicProtocol.BOSS_SET -> setBossTrack(incomingTrack, incomingGain)
                    MusicProtocol.BOSS_CLEAR -> setBossTrack(null)
                    MusicProtocol.EVENT_SET -> setEventTrack(incomingEventId, incomingTrack)
                    MusicProtocol.EVENT_CLEAR -> clearEventTrack(incomingEventId)
                    else -> logger.warn("Se ignoró una acción de música desconocida: {}", action)
                }
            }
        }
    }

    private fun updateDimension(client: Minecraft) {
        val player = client.player
        if (player == null) {
            if (ambientDimension != null || ambientTrack != null) {
                ambientDimension = null
                ambientTrack = null
                reconcile()
            }
            return
        }

        val dimensionId: ResourceLocation = player.level().dimension().location()
        val dimensionName = dimensionId.toString()
        if (ambientDimension == dimensionName) return

        ambientDimension = dimensionName
        val candidate = MusicFiles.ambientTrackFor(dimensionName)
        ambientTrack = if (BundledMusicFiles.exists(candidate)) candidate else null
        reconcile()
    }

    private fun updateVolume(client: Minecraft) {
        val master = client.options.getSoundSourceVolume(SoundSource.MASTER)
        val music = client.options.getSoundSourceVolume(SoundSource.MUSIC)
        val trackMultiplier = if (desiredSelection()?.layer == Layer.BOSS) bossVolume else 1.0f
        volume = (master * music * trackMultiplier).coerceIn(0.0f, 3.0f)
        ExternalAudioPlayer.setGain(volume)
    }

    private fun loadSavedBossVolume() {
        val config = Minecraft.getInstance().gameDirectory.toPath()
            .resolve("config").resolve("magic_music.properties")
        volumeConfigPath = config
        savedBossVolume = try {
            if (!Files.exists(config)) 1.0f else {
                val properties = Properties()
                Files.newInputStream(config).use { properties.load(it) }
                parseVolume(properties.getProperty("bossVolume")) ?: 1.0f
            }
        } catch (exception: IOException) {
            logger.warn("No se pudo leer config/magic_music.properties", exception)
            1.0f
        }
        bossVolume = savedBossVolume
    }

    private fun saveSavedBossVolume() {
        val config = volumeConfigPath ?: return
        try {
            Files.createDirectories(config.parent)
            val properties = Properties()
            if (Files.exists(config)) Files.newInputStream(config).use { properties.load(it) }
            properties.setProperty("bossVolume", savedBossVolume.toString())
            Files.newOutputStream(config).use { properties.store(it, "Magic music settings") }
        } catch (exception: IOException) {
            logger.error("No se pudo guardar config/magic_music.properties", exception)
        }
    }

    private fun parseVolume(raw: String?): Float? {
        val value = raw?.toFloatOrNull() ?: return null
        return listOf(0.5f, 1.0f, 2.0f, 3.0f).firstOrNull { kotlin.math.abs(it - value) < 0.001f }
    }

    fun listAllTracks(): List<String> = (MusicFiles.listTracks() + BundledMusicFiles.listTracks())
        .distinctBy { it.lowercase() }
        .sorted()

    fun setBossTrack(track: String?, gain: Float = savedBossVolume) {
        val normalized = track?.let(MusicTrackNames::normalizeTrack)
        if (normalized != null && normalized == bossTrack) {
            // Reissuing /modmusic play for the current song restarts it from the beginning.
            appliedSelection = null
            ExternalAudioPlayer.stop()
        }
        bossTrack = normalized
        if (normalized != null) {
            bossVolume = gain.coerceIn(0.0f, 3.0f)
            savedBossVolume = bossVolume
            saveSavedBossVolume()
            updateVolume(Minecraft.getInstance())
        }
        reconcile()
    }

    fun setEventTrack(incomingEventId: String, track: String) {
        val safeEvent = MusicTrackNames.normalizeEvent(incomingEventId) ?: return
        val safeTrack = MusicTrackNames.normalizeTrack(track) ?: return
        eventId = safeEvent
        eventTrack = safeTrack
        reconcile()
    }

    fun clearEventTrack(incomingEventId: String) {
        // An old stop packet must not clear a newer event this player has joined.
        if (eventId != incomingEventId) return
        eventId = null
        eventTrack = null
        reconcile()
    }

    fun clearServerOwnedTracks() {
        bossTrack = null
        eventId = null
        eventTrack = null
        reconcile()
    }

    fun playPersonal(rawTrack: String): String {
        val track = MusicTrackNames.normalizeTrack(rawTrack)
            ?: return "Nombre inválido. Usa letras, números, _, - o .; no incluyas carpetas."
        if (MusicFiles.resolveTrack(track) == null && !BundledMusicFiles.exists(track)) {
            return "No se encontró '$track.ogg' ni en Musics ni en assets/magic/music. Usa /music list."
        }
        personalTrack = track
        reconcile()
        return "Música seleccionada: $track.ogg"
    }

    fun stopPersonal(): String {
        personalTrack = null
        reconcile()
        return "Música personal detenida. Se escuchará la siguiente capa disponible."
    }

    fun reload(): String {
        MusicFiles.ensureDirectory()
        appliedSelection = null
        ExternalAudioPlayer.stop()
        // Re-scan the current dimension too, so a newly added ambient track is picked up.
        ambientDimension = null
        updateDimension(Minecraft.getInstance())
        reconcile()
        return "Carpeta Musics actualizada."
    }

    fun shouldSuppressVanillaMusic(): Boolean {
        // Boss/event music suppress vanilla music even on a client missing the requested file;
        // silently falling through to another soundtrack would violate their priority.
        return bossTrack != null || eventTrack != null || desiredSelection() != null
    }

    private fun desiredSelection(): Selection? {
        bossTrack?.let { return Selection(Layer.BOSS, it) }
        eventTrack?.let { return Selection(Layer.EVENT, it) }
        personalTrack?.let { return Selection(Layer.PERSONAL, it) }
        ambientTrack?.let { return Selection(Layer.AMBIENT, it) }
        return null
    }

    private fun reconcile() {
        val target = desiredSelection()
        if (target == appliedSelection) return

        ExternalAudioPlayer.stop()
        appliedSelection = target

        if (target == null) return
        val file = if (target.layer == Layer.PERSONAL) {
            MusicFiles.resolveTrack(target.track) ?: BundledMusicFiles.resolveTrack(target.track)
        } else {
            BundledMusicFiles.resolveTrack(target.track)
        }
        if (file == null) {
            if (target.layer == Layer.BOSS || target.layer == Layer.EVENT) {
                Minecraft.getInstance().player?.displayClientMessage(
                    Component.literal("[Magic Music] Falta assets/magic/music/${target.track}.ogg en este mod."),
                    false
                )
            }
            return
        }

        if (!ExternalAudioPlayer.play(file)) {
            logger.warn("No se pudo iniciar la pista {}", target.track)
            Minecraft.getInstance().player?.displayClientMessage(
                Component.literal("[Magic Music] No se pudo reproducir ${target.track}.ogg; revisa que sea OGG/Vorbis."),
                false
            )
        }
    }
}
