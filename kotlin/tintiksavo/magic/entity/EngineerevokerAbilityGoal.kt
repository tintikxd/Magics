package tintiksavo.magic.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player

/** Cycles through three debuff packages, applying each to up to five nearby players. */
class EngineerevokerAbilityGoal(mob: MagicEnemyEntity) : MagicAbilityGoal(mob, 120) {
    private var pack = 0

    override fun shouldStart(): Boolean = findTargets().isNotEmpty()

    override fun start() {
        val targets = findTargets()
        if (targets.isEmpty()) return
        beginAbility()
        when (pack) {
            0 -> applyPackOne(targets)
            1 -> applyPackTwo(targets)
            else -> applyPackThree(targets)
        }
        pack = (pack + 1) % 3
        finishAbility()
    }

    private fun findTargets(): List<Player> = mob.level().getEntitiesOfClass(
        Player::class.java, mob.boundingBox.inflate(16.0)
    ) { it.isAlive && !it.isSpectator && !it.isCreative }
        .sortedBy { mob.distanceToSqr(it) }
        .take(5)

    private fun applyPackOne(targets: List<Player>) {
        targets.forEach { target ->
            target.addEffect(MobEffectInstance(MobEffects.POISON, 200, 1))
            target.addEffect(MobEffectInstance(MobEffects.LEVITATION, 60, 0))
            target.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0))
        }
        mob.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, 200, 0))
    }

    private fun applyPackTwo(targets: List<Player>) {
        targets.forEach { target ->
            target.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 200, 0))
            target.addEffect(MobEffectInstance(MobEffects.HARM, 1, 0))
            target.addEffect(MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 0))
        }
    }

    private fun applyPackThree(targets: List<Player>) {
        targets.forEach { target ->
            target.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 200, 0))
            target.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 200, 0))
            target.addEffect(MobEffectInstance(MobEffects.HUNGER, 200, 2))
        }
    }
}
