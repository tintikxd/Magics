
package tintiksavo.magic.command

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import tintiksavo.magic.entity.MagicEnemyEntity
import java.util.UUID

/**
 * Mantiene sincronizada la entidad proxy con el jugador transformado.
 */
object BossTransformationManager {

    private val activeForms: MutableMap<UUID, MagicEnemyEntity> = LinkedHashMap()

    fun register() {
        ServerTickEvents.END_SERVER_TICK.register(::tick)

        ServerLifecycleEvents.SERVER_STOPPED.register {
            activeForms.values.forEach { it.discard() }
            activeForms.clear()
        }
    }

    fun transform(player: ServerPlayer, formId: String): Boolean {
        activeForms.remove(player.uuid)?.discard()
        player.removeEffect(MobEffects.INVISIBILITY)

        val entity = TransformForms.create(formId, player) ?: return false

        syncPositionAndRotation(entity, player)

        player.serverLevel().addFreshEntity(entity)
        activeForms[player.uuid] = entity

        player.addEffect(
            MobEffectInstance(
                MobEffects.INVISIBILITY,
                Int.MAX_VALUE,
                0,
                false,
                false,
                false
            )
        )

        return true
    }

    fun clear(player: ServerPlayer) {
        activeForms.remove(player.uuid)?.discard()
        player.removeEffect(MobEffects.INVISIBILITY)
    }

    fun isTransformed(player: ServerPlayer): Boolean {
        return activeForms.containsKey(player.uuid)
    }

    private fun tick(server: MinecraftServer) {
        for ((uuid, entity) in activeForms.toMap()) {
            val player = server.playerList.getPlayer(uuid)

            if (player == null) {
                entity.discard()
                activeForms.remove(uuid)
                continue
            }

            if (!entity.isAlive ||
                player.level().dimension() != entity.level().dimension()
            ) {
                clear(player)
                continue
            }

            syncPositionAndRotation(entity, player)

            if (entity.target === player) {
                entity.target = null
            }
        }
    }

    private fun syncPositionAndRotation(
        entity: MagicEnemyEntity,
        player: ServerPlayer
    ) {
        entity.moveTo(
            player.x,
            player.y,
            player.z,
            player.yRot,
            player.xRot
        )

        entity.setYHeadRot(player.yHeadRot)
        entity.setYBodyRot(player.yRot)
    }
}
