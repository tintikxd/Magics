package tintiksavo.magic.entity

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerBossEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.BossEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.level.Level

/** Boss-bar behaviour recovered from the compiled JAR. */
class BufonBossEntity(entityType: EntityType<out Monster>, level: Level) : MagicEnemyEntity(entityType, level) {
    private val bossBar = ServerBossEvent(
        Component.translatable("entity.magic.bufonboss"),
        BossEvent.BossBarColor.PURPLE,
        BossEvent.BossBarOverlay.PROGRESS
    )

    override val magicVariant: MagicEnemyVariant get() = MagicEnemyVariant.BUFONBOSS

    override fun startSeenByPlayer(player: ServerPlayer) {
        super.startSeenByPlayer(player)
        bossBar.addPlayer(player)
    }

    override fun stopSeenByPlayer(player: ServerPlayer) {
        super.stopSeenByPlayer(player)
        bossBar.removePlayer(player)
    }

    override fun customServerAiStep() {
        super.customServerAiStep()
        bossBar.progress = (health / maxHealth).coerceIn(0.0f, 1.0f)
    }
}
