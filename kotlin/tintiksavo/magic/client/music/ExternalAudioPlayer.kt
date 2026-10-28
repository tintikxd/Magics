package tintiksavo.magic.client.music

import net.minecraft.client.Minecraft
import net.minecraft.client.sounds.SoundEngine
import net.minecraft.client.sounds.SoundEngineExecutor
import net.minecraft.client.sounds.SoundManager
import org.lwjgl.openal.AL10
import org.lwjgl.stb.STBVorbis
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.libc.LibCStdlib
import org.slf4j.LoggerFactory
import tintiksavo.magic.client.mixin.SoundEngineAccessor
import tintiksavo.magic.client.mixin.SoundManagerAccessor
import java.nio.ShortBuffer
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.abs

/**
 * Plays one external OGG/Vorbis track through Minecraft's OpenAL context.
 * Audio decoding is done before queuing; every OpenAL operation is dispatched
 * onto Minecraft's own sound-engine thread, where its context is current.
 */
object ExternalAudioPlayer {
    private val logger = LoggerFactory.getLogger("magic-external-music")
    private val requestGeneration = AtomicLong(0L)

    // These fields are only read/written by the sound-engine thread.
    private var sourceId: Int = 0
    private var bufferId: Int = 0
    @Volatile private var gain: Float = 1.0f

    fun play(file: Path): Boolean {
        stop()
        val request = requestGeneration.incrementAndGet()

        var decoded: ShortBuffer? = null
        var channelCount = 0
        var sampleRate = 0

        try {
            val stack = MemoryStack.stackPush()
            val channelsBuffer = stack.mallocInt(1)
            val sampleRateBuffer = stack.mallocInt(1)
            try {
                decoded = STBVorbis.stb_vorbis_decode_filename(
                    file.toAbsolutePath().toString(),
                    channelsBuffer,
                    sampleRateBuffer
                )
                channelCount = channelsBuffer.get(0)
                sampleRate = sampleRateBuffer.get(0)
            } finally {
                stack.close()
            }

            val pcm = decoded ?: throw IllegalArgumentException("No se pudo decodificar el archivo OGG.")
            if (pcm.remaining() == 0 || sampleRate <= 0) {
                throw IllegalArgumentException("El archivo OGG está vacío o tiene una frecuencia inválida.")
            }
            if (channelCount != 1 && channelCount != 2) {
                throw IllegalArgumentException("Solo se admite audio mono o estéreo (canales detectados: $channelCount).")
            }

            val accepted = executeOnSoundThread {
                if (request != requestGeneration.get()) {
                    freeDecoded(pcm, file)
                    return@executeOnSoundThread
                }
                var createdBuffer = 0
                var createdSource = 0
                try {
                    stopNative()
                    AL10.alGetError() // Clear any stale error before creating our buffer/source.

                    createdBuffer = AL10.alGenBuffers()
                    if (createdBuffer == 0) throw IllegalStateException("OpenAL no pudo crear un búfer de audio.")

                    val format = if (channelCount == 1) AL10.AL_FORMAT_MONO16 else AL10.AL_FORMAT_STEREO16
                    AL10.alBufferData(createdBuffer, format, pcm, sampleRate)

                    createdSource = AL10.alGenSources()
                    if (createdSource == 0) throw IllegalStateException("OpenAL no pudo crear una fuente de audio.")

                    AL10.alSourcei(createdSource, AL10.AL_BUFFER, createdBuffer)
                    AL10.alSourcei(createdSource, AL10.AL_LOOPING, AL10.AL_TRUE)
                    AL10.alSourcef(createdSource, AL10.AL_GAIN, gain)
                    AL10.alSourcei(createdSource, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE)
                    AL10.alSourcef(createdSource, AL10.AL_ROLLOFF_FACTOR, 0.0f)

                    val error = AL10.alGetError()
                    if (error != AL10.AL_NO_ERROR) {
                        throw IllegalStateException("OpenAL devolvió el error 0x${error.toString(16)}.")
                    }

                    AL10.alSourcePlay(createdSource)
                    sourceId = createdSource
                    bufferId = createdBuffer
                } catch (exception: Exception) {
                    if (createdSource != 0) runCatching { AL10.alDeleteSources(createdSource) }
                    if (createdBuffer != 0) runCatching { AL10.alDeleteBuffers(createdBuffer) }
                    logger.error("No se pudo reproducir música externa desde {}", file, exception)
                } finally {
                    freeDecoded(pcm, file)
                }
            }

            if (!accepted) {
                freeDecoded(pcm, file)
                return false
            }
            // Playback starts asynchronously on Minecraft's sound thread.
            return true
        } catch (exception: Exception) {
            decoded?.let { freeDecoded(it, file) }
            logger.error("No se pudo decodificar música externa desde {}", file, exception)
            return false
        }
    }

    fun setGain(newGain: Float) {
        val normalized = newGain.coerceIn(0.0f, 3.0f)
        if (abs(normalized - gain) < 0.005f) return
        gain = normalized
        executeOnSoundThread {
            if (sourceId != 0) {
                runCatching { AL10.alSourcef(sourceId, AL10.AL_GAIN, gain) }
                    .onFailure { logger.debug("No se pudo actualizar el volumen de la música externa", it) }
            }
        }
    }

    fun stop() {
        requestGeneration.incrementAndGet()
        executeOnSoundThread { stopNative() }
    }

    private fun stopNative() {
        val oldSource = sourceId
        val oldBuffer = bufferId
        sourceId = 0
        bufferId = 0

        if (oldSource != 0) {
            runCatching {
                AL10.alSourceStop(oldSource)
                AL10.alDeleteSources(oldSource)
            }.onFailure { logger.debug("No se pudo detener la fuente de música externa", it) }
        }
        if (oldBuffer != 0) {
            runCatching { AL10.alDeleteBuffers(oldBuffer) }
                .onFailure { logger.debug("No se pudo eliminar el búfer de música externa", it) }
        }
    }

    private fun freeDecoded(pcm: ShortBuffer, file: Path) {
        runCatching { LibCStdlib.free(pcm) }
            .onFailure { logger.warn("No se pudo liberar el búfer decodificado de {}", file, it) }
    }

    private fun executeOnSoundThread(task: () -> Unit): Boolean {
        return try {
            val minecraft = Minecraft.getInstance()
            val soundManager = minecraft.soundManager as SoundManagerAccessor
            val soundEngine: SoundEngine = soundManager.getSoundEngine()
            val executor: SoundEngineExecutor = (soundEngine as SoundEngineAccessor).getExecutor()
            executor.execute(Runnable(task))
            true
        } catch (exception: Exception) {
            logger.warn("No se pudo acceder al hilo de audio de Minecraft", exception)
            false
        }
    }
}
