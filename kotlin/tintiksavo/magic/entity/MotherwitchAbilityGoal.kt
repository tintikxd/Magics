package tintiksavo.magic.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.EvokerFangs
import net.minecraft.world.phys.AABB
import kotlin.math.cos
import kotlin.math.sin

/** Alternates between a triple ring of evoker fangs and a poison/blindness attack. */
class MotherwitchAbilityGoal(mob: MagicEnemyEntity) : MagicAbilityGoal(mob, 120) {
    private var fangAttackNext = true
    private var castTicks = 0

    override fun shouldStart(): Boolean {
        val target = mob.target as? Player ?: return false
        return target.isAlive && !target.isSpectator && mob.distanceToSqr(target) <= 324.0
    }

    override fun start() {
        castTicks = 10
        beginAbility()
    }

    override fun tick() {
        (mob.target as? Player)?.let { mob.lookAt(it, 30.0f, 30.0f) }
        castTicks--
        if (castTicks > 0) return

        if (fangAttackNext) {
            mob.triggerAnim("action_controller", "attack1")
            spawnFangRings()
        } else {
            mob.triggerAnim("action_controller", "attack2")
            applyDebuffAttack()
        }
        fangAttackNext = !fangAttackNext
        finishAbility()
    }

    private fun spawnFangRings() {
        spawnRing(3.5, 14)
        spawnRing(2.25, 8)
        spawnRing(1.1, 4)
    }

    private fun spawnRing(radius: Double, count: Int) {
        val level = mob.level()
        repeat(count) { i ->
            val angle = 2.0 * Math.PI * i / count
            val x = mob.x + cos(angle) * radius
            val z = mob.z + sin(angle) * radius
            val y = mob.y
            val fangs = EvokerFangs(level, x, y, z, Math.toDegrees(angle).toFloat(), 2, null)
            level.addFreshEntity(fangs)

            val hitBox = AABB(x - 0.5, y, z - 0.5, x + 0.5, y + 1.0, z + 0.5)
            for (player in level.getEntitiesOfClass(Player::class.java, hitBox)) {
                if (!player.isAlive || player.isSpectator) continue
                val armorHalf = if (player.armorValue > 0) player.armorValue / 2 else 0
                val multiplier = (1.0f - armorHalf * 0.04f).coerceIn(0.1f, 1.0f)
                player.hurt(mob.damageSources().mobAttack(mob), 4.0f * multiplier)
            }
        }
    }

    private fun applyDebuffAttack() {
        val target = mob.target as? Player ?: return
        target.addEffect(MobEffectInstance(MobEffects.POISON, 200, 1))
        target.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 200, 0))
    }
}
