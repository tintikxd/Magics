package tintiksavo.magic.client.music

import tintiksavo.magic.music.MusicTrackNames
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import java.util.stream.Collectors

/** Manages external .ogg tracks in the active Minecraft game directory. */
object MusicFiles {
    private var musicDirectory: Path? = null

    fun initialize(gameDirectory: Path) {
        musicDirectory = gameDirectory.resolve("Musics").toAbsolutePath().normalize()
        ensureDirectory()
    }

    fun ensureDirectory() {
        val directory = musicDirectory ?: return
        try {
            Files.createDirectories(directory)
            val readme = directory.resolve("LEEME.txt")
            if (!Files.exists(readme)) {
                Files.writeString(readme, README, StandardCharsets.UTF_8)
            }
        } catch (exception: IOException) {
            System.err.println("[Magic music] No se pudo preparar la carpeta ${directory}: ${exception.message}")
        }
    }

    fun directory(): Path? = musicDirectory

    fun resolveTrack(rawName: String): Path? {
        val directory = musicDirectory ?: return null
        val name = MusicTrackNames.normalizeTrack(rawName) ?: return null
        val candidate = directory.resolve("$name.ogg").normalize()
        if (candidate.parent != directory || !Files.isRegularFile(candidate)) return null
        return candidate
    }

    fun listTracks(): List<String> {
        val directory = musicDirectory ?: return emptyList()
        ensureDirectory()
        if (!Files.isDirectory(directory)) return emptyList()

        return try {
            Files.list(directory).use { files ->
                files
                    .filter { Files.isRegularFile(it) }
                    .map { it.fileName.toString() }
                    .filter { it.lowercase(Locale.ROOT).endsWith(".ogg") }
                    .map { it.dropLast(4) }
                    .filter { MusicTrackNames.normalizeTrack(it) != null }
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList())
            }
        } catch (exception: IOException) {
            System.err.println("[Magic music] No se pudieron listar las canciones: ${exception.message}")
            emptyList()
        }
    }

    fun ambientTrackFor(dimensionId: String): String {
        val safeId = dimensionId
            .replace(':', '_')
            .replace('/', '_')
        return "ambient_$safeId"
    }

    private const val README = """
Magic - música externa

1) Copia archivos OGG/Vorbis dentro de esta carpeta. Por ahora solo se admiten .ogg.
2) Para reproducir una canción personal, usa: /music play <nombre>
   El nombre va sin la extensión .ogg. Por ejemplo: /music play MySong
3) /music list muestra los archivos que detectó el mod; /music stop detiene la capa personal.
4) /music play y /music list también reconocen canciones empaquetadas en el mod.
5) Música de jefe, evento y ambiental se empaqueta en src/main/resources/assets/magic/music/.
   Ejemplos: void_boss.ogg, circus_event.ogg, ambient_minecraft_overworld.ogg.
6) No pongas archivos de otros formatos renombrados a .ogg: tienen que estar codificados
   realmente como OGG/Vorbis.

Prioridad: jefe > evento > personal > ambiental.
"""
}
