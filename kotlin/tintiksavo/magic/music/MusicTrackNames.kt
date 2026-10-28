package tintiksavo.magic.music

/**
 * Track identifiers are names, not paths. Keeping them to a small safe alphabet
 * prevents commands or network packets from escaping the Musics directory.
 */
object MusicTrackNames {
    private val trackPattern = Regex("^[A-Za-z0-9][A-Za-z0-9_.-]{0,63}$")

    fun normalizeTrack(raw: String): String? {
        val withoutExtension = if (raw.endsWith(".ogg", ignoreCase = true)) {
            raw.dropLast(4)
        } else {
            raw
        }
        return withoutExtension.takeIf { trackPattern.matches(it) }
    }

    fun normalizeEvent(raw: String): String? = raw.takeIf { trackPattern.matches(it) }
}
