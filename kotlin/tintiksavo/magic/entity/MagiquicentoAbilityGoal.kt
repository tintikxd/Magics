package tintiksavo.magic.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

class MagiquicentoAbilityGoal(mob: MagicEnemyEntity) : MagicAbilityGoal(mob, 100) {
    private var pullTicks = 0
    private var targets: List<Player> = emptyList()

    override fun shouldStart(): Boolean = findTargets().isNotEmpty()

    override fun start() {
        targets = findTargets()
        if (targets.isEmpty()) return
        pullTicks = 40
        beginAbility()
        targets.forEach { it.addEffect(MobEffectInstance(MobEffects.POISON, 200, 1)) }
    }

    override fun tick() {
        for (target in targets) {
            if (!target.isAlive) continue
            val direction = Vec3(mob.x - target.x, 0.0, mob.z - target.z)
            if (direction.lengthSqr() > 1.0E-4) {
                val push = direction.normalize().scale(0.035)
                target.push(push.x, 0.0, push.z)
                target.hurtMarked = true
            }
        }
        pullTicks--
        if (pullTicks <= 0) finishAbility()
    }

    override fun stop() {
        super.stop()
        targets = emptyList()
        pullTicks = 0
    }
}
