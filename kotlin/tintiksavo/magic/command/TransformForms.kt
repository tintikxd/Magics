package tintiksavo.magic.command

import net.minecraft.server.level.ServerPlayer
import tintiksavo.magic.entity.BufonBossProxyEntity
import tintiksavo.magic.entity.MagicEnemyEntity
import tintiksavo.magic.entity.ModEntities
import java.util.Locale

/** Formas que pueden adoptar los jugadores. El JAR registra solo bufonboss. */
object TransformForms {
    private val forms: LinkedHashMap<String, (ServerPlayer) -> MagicEnemyEntity> = linkedMapOf(
        "bufonboss" to { player ->
            BufonBossProxyEntity(ModEntities.BUFONBOSS_PROXY, player.serverLevel()).also {
                it.setOwner(player)
            }
        }
    )

    fun create(id: String, player: ServerPlayer): MagicEnemyEntity? =
        forms[id.lowercase(Locale.ROOT)]?.invoke(player)

    fun ids(): Collection<String> = forms.keys
}
