package tintiksavo.magic.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects

/** Heals and strengthens up to five other magic enemies in a 16-block radius. */
class EternalwitchSupportGoal(mob: MagicEnemyEntity) : MagicAbilityGoal(mob, 100) {
    private var delayTicks = 0
    private var listAllies: List<MagicEnemyEntity> = emptyList()

    override fun shouldStart(): Boolean = findAllies().isNotEmpty()

    override fun start() {
        listAllies = findAllies()
        if (listAllies.isEmpty()) return
        delayTicks = 20
        mob.navigation.stop()
        beginAbility()
    }

    override fun canContinueToUse(): Boolean = active && delayTicks >= 0

    override fun tick() {
        if (listAllies.isNotEmpty() && listAllies[0].isAlive) {
            mob.lookAt(listAllies[0], 30.0f, 30.0f)
        }
        delayTicks--
        if (delayTicks == 0) executeSupport()
    }

    private fun executeSupport() {
        for (ally in findAllies()) {
            // These are exactly the two effects present in the compiled class: instant health
            // (one tick, amplifier 1) and Strength (300 ticks).
            ally.addEffect(MobEffectInstance(MobEffects.HEAL, 1, 1))
            ally.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 0))
            spawnGreenParticles(ally)
        }
        finishAbility()
    }

    override fun stop() {
        super.stop()
        delayTicks = 0
        listAllies = emptyList()
    }

    private fun findAllies(): List<MagicEnemyEntity> = mob.level().getEntitiesOfClass(
        MagicEnemyEntity::class.java,
        mob.boundingBox.inflate(16.0)
    ) { it.isAlive && it !== mob }
        .sortedBy { mob.distanceToSqr(it) }
        .take(5)

    private fun spawnGreenParticles(ally: MagicEnemyEntity) {
        val level = mob.level() as? ServerLevel ?: return
        level.sendParticles(
            ParticleTypes.HAPPY_VILLAGER,
            ally.x, ally.y + ally.bbHeight * 0.5, ally.z,
            18, 0.4, 0.6, 0.4, 0.02
        )
    }
}
