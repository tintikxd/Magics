package tintiksavo.magic.entity

enum class MagicEnemyVariant(
    val id: String,
    val displayName: String,
    val maxHealth: Double,
    val attackDamage: Double,
    val movementSpeed: Double,
    val followRange: Double,
    val width: Float,
    val height: Float,
    val primaryEggColor: Int,
    val secondaryEggColor: Int,
    val attackType: AttackType,
    val boss: Boolean
) {
    CLOWN("clown", "Clown", 24.0, 4.0, 0.28, 24.0, 0.65f, 1.95f, 16773617, 14231592, AttackType.MELEE, false),
    NINJACLOWN("ninjaclown", "Ninja Clown", 30.0, 6.0, 0.34, 28.0, 0.65f, 1.95f, 2105636, 14693942, AttackType.ABILITY, false),
    MAGICENTO("magicento", "Magicento", 36.0, 7.0, 0.26, 32.0, 0.75f, 2.05f, 5974662, 3458047, AttackType.ABILITY, false),
    MAGIQUICENTO("magiquicento", "Magiquicento", 42.0, 8.0, 0.31, 34.0, 0.75f, 2.05f, 2461029, 16765286, AttackType.ABILITY, false),
    BUFONBOSS("bufonboss", "Bufon Boss", 180.0, 13.0, 0.24, 48.0, 1.25f, 3.4f, 10162462, 16369487, AttackType.MELEE, true),
    MOTHERWITCH("motherwitch", "Motherwitch", 150.0, 0.0, 0.24, 32.0, 0.85f, 2.15f, 6166344, 14900127, AttackType.ABILITY, true),
    ETERNALWITCH("eternalwitch", "Eternalwitch", 40.0, 0.0, 0.25, 28.0, 0.7f, 1.95f, 3107663, 9306034, AttackType.ABILITY, false),
    ENGINEEREVOKER("engineerevoker", "Engineerevoker", 45.0, 0.0, 0.25, 32.0, 0.75f, 2.05f, 4934514, 10132191, AttackType.ABILITY, false)
}
