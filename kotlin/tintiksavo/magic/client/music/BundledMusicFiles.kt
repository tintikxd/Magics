package tintiksavo.magic.client.music

import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import tintiksavo.magic.music.MusicTrackNames
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

object BundledMusicFiles {

    private const val MOD_ID = "magic"
    private const val RESOURCE_DIRECTORY = "music"

    private var cacheDirectory: Path? = null

    /**
     * Busca una pista incluida en los recursos del mod.
     *
     * Ejemplo:
     * track = "void_boss"
     * recurso = assets/magic/music/void_boss.ogg
     *
     * Devuelve una ruta de caché que puede utilizar ExternalAudioPlayer.
     */
    fun resolveTrack(rawTrack: String): Path? {
        val track = MusicTrackNames.normalizeTrack(rawTrack) ?: return null

        val resourceId = ResourceLocation(
            MOD_ID,
            "$RESOURCE_DIRECTORY/$track.ogg"
        )

        val resource = Minecraft.getInstance()
            .resourceManager
            .getResource(resourceId)
            .orElse(null)
            ?: return null

        return try {
            val directory = getCacheDirectory()
            Files.createDirectories(directory)

            val destination = directory.resolve("$track.ogg")
                .normalize()

            if (destination.parent != directory.normalize()) {
                return null
            }

            resource.open().use { input ->
                Files.copy(
                    input,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
                )
            }

            destination
        } catch (exception: IOException) {
            System.err.println(
                "[Magic Music] No se pudo cargar la pista '$track': " +
                        exception.message
            )
            null
        }
    }

    /**
     * Comprueba si la pista está incluida en los recursos del mod.
     */
    /** Lista nombres de las canciones OGG incluidas en assets/magic/music, sin extensión. */
    fun listTracks(): List<String> {
        return Minecraft.getInstance().resourceManager
            .listResources(RESOURCE_DIRECTORY) { id ->
                id.namespace == MOD_ID && id.path.endsWith(".ogg", ignoreCase = true)
            }
            .keys
            .map { it.path.removePrefix("$RESOURCE_DIRECTORY/").removeSuffix(".ogg") }
            .filter { "/" !in it && MusicTrackNames.normalizeTrack(it) != null }
            .distinct()
            .sorted()
    }

    fun exists(rawTrack: String): Boolean {
        val track = MusicTrackNames.normalizeTrack(rawTrack) ?: return false

        val resourceId = ResourceLocation(
            MOD_ID,
            "$RESOURCE_DIRECTORY/$track.ogg"
        )

        return Minecraft.getInstance()
            .resourceManager
            .getResource(resourceId)
            .isPresent
    }

    private fun getCacheDirectory(): Path {
        cacheDirectory?.let { return it }

        val directory = Minecraft.getInstance()
            .gameDirectory
            .toPath()
            .resolve("cache")
            .resolve("tintiksavo-magic-music")
            .toAbsolutePath()
            .normalize()

        cacheDirectory = directory
        return directory
    }
}