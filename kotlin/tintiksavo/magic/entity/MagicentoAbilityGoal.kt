package tintiksavo.magic.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import tintiksavo.magic.effect.ModEffects

class MagicentoAbilityGoal(mob: MagicEnemyEntity) : MagicAbilityGoal(mob, 100) {
    private var target: Player? = null

    override fun shouldStart(): Boolean {
        target = findNearestTarget(mob.variant.followRange)
        return target != null
    }

    override fun start() {
        val victim = target ?: return
        beginAbility()
        val velocity = victim.deltaMovement
        victim.deltaMovement = velocity.x.let { net.minecraft.world.phys.Vec3(it, 0.75, velocity.z) }
        victim.hurtMarked = true
        victim.addEffect(MobEffectInstance(MobEffects.LEVITATION, 60, 0))
        victim.addEffect(MobEffectInstance(ModEffects.SHAKING, 60, 0))
        finishAbility()
        target = null
    }
}
