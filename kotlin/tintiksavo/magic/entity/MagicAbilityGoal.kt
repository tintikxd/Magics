package tintiksavo.magic.entity

import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.player.Player

/** Common server-side cooldown and target-selection logic for special attacks. */
abstract class MagicAbilityGoal(
    protected val mob: MagicEnemyEntity,
    private val cooldownTicks: Int
) : Goal() {
    private var cooldown = 0
    protected var active: Boolean = false
        protected set

    override fun canUse(): Boolean {
        if (mob.level().isClientSide) return false
        if (cooldown > 0) {
            cooldown--
            return false
        }
        return shouldStart()
    }

    protected abstract fun shouldStart(): Boolean

    override fun canContinueToUse(): Boolean = active

    override fun stop() {
        active = false
    }

    protected fun beginAbility() {
        active = true
        mob.triggerAnim("living_controller", "attack")
    }

    protected fun finishAbility() {
        active = false
        cooldown = cooldownTicks
    }

    protected fun findTargets(range: Double = 16.0): List<Player> {
        return mob.level().getEntitiesOfClass(
            Player::class.java,
            mob.boundingBox.inflate(range)
        ) { it.isAlive && !it.isSpectator && !it.isCreative }
            .sortedBy { mob.distanceToSqr(it) }
    }

    protected fun findNearestTarget(range: Double = 16.0): Player? = findTargets(range).firstOrNull()
}
