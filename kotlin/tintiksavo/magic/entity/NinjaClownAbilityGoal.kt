package tintiksavo.magic.entity

import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f

/** Charges a visible 10x5x10 cube, then teleports behind the target and attacks within it. */
class NinjaClownAbilityGoal(mob: MagicEnemyEntity) : MagicAbilityGoal(mob, 40) {
    private var target: Player? = null
    private var charging = false
    private var chargeTicks = 0
    private var attackCenter: Vec3? = null

    override fun shouldStart(): Boolean = findNearestTarget(mob.variant.followRange) != null

    override fun start() {
        target = findNearestTarget(mob.variant.followRange)
        charging = false
        chargeTicks = 0
        attackCenter = null
        active = true
    }

    override fun canContinueToUse(): Boolean = active && target?.isAlive == true

    override fun stop() {
        super.stop()
        charging = false
        target = null
        attackCenter = null
    }

    override fun tick() {
        val victim = target ?: run {
            stop()
            return
        }
        if (!victim.isAlive) {
            stop()
            return
        }

        if (!charging) {
            mob.lookAt(victim, 30.0f, 30.0f)
            if (mob.distanceToSqr(victim) > 20.25) {
                mob.navigation.moveTo(victim, 1.15)
                return
            }
            attackCenter = mob.position()
            charging = true
            chargeTicks = 0
            mob.navigation.stop()
            beginAbility()
        }

        mob.lookAt(victim, 30.0f, 30.0f)
        if (isInsideAttackCube(victim)) {
            victim.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0, false, false))
        }
        if (chargeTicks % 2 == 0) drawCubeEdges()
        chargeTicks++
        if (chargeTicks >= 35) executeAttack(victim)
    }

    private fun executeAttack(victim: Player) {
        // Play the one-shot attack animation when the charge finishes.
        mob.triggerAnim("action_controller", "melee_attack")

        val center = attackCenter
        charging = false
        teleportBehind(victim)
        if (center != null && isInsideAttackCube(victim, center)) {
            victim.hurt(mob.damageSources().mobAttack(mob), 40.0f)
        }
        attackCenter = null
        finishAbility()
    }

    private fun isInsideAttackCube(victim: Player): Boolean {
        val center = attackCenter ?: return false
        return isInsideAttackCube(victim, center)
    }

    private fun isInsideAttackCube(victim: Player, center: Vec3): Boolean {
        val cube = AABB(
            center.x - 5.0, center.y, center.z - 5.0,
            center.x + 5.0, center.y + 5.0, center.z + 5.0
        )
        return cube.intersects(victim.boundingBox)
    }

    private fun teleportBehind(victim: Player) {
        val look = victim.lookAngle
        val base = Vec3(victim.x - look.x * 2.0, victim.y, victim.z - look.z * 2.0)
        val candidates = listOf(base, base.add(0.0, 1.0, 0.0), base.add(0.0, -1.0, 0.0))
        for (candidate in candidates) {
            val movedBox = mob.boundingBox.move(candidate.subtract(mob.position()))
            if (mob.level().noCollision(mob, movedBox)) {
                mob.teleportTo(candidate.x, candidate.y, candidate.z)
                mob.deltaMovement = Vec3.ZERO
                return
            }
        }
        mob.teleportTo(base.x, base.y, base.z)
        mob.deltaMovement = Vec3.ZERO
    }

    private fun drawCubeEdges() {
        val level = mob.level() as? ServerLevel ?: return
        val center = attackCenter ?: return
        val x1 = center.x - 5.0
        val x2 = center.x + 5.0
        val y1 = center.y
        val y2 = center.y + 5.0
        val z1 = center.z - 5.0
        val z2 = center.z + 5.0
        val edges = listOf(
            doubleArrayOf(x1, y1, z1, x2, y1, z1), doubleArrayOf(x2, y1, z1, x2, y1, z2),
            doubleArrayOf(x2, y1, z2, x1, y1, z2), doubleArrayOf(x1, y1, z2, x1, y1, z1),
            doubleArrayOf(x1, y2, z1, x2, y2, z1), doubleArrayOf(x2, y2, z1, x2, y2, z2),
            doubleArrayOf(x2, y2, z2, x1, y2, z2), doubleArrayOf(x1, y2, z2, x1, y2, z1),
            doubleArrayOf(x1, y1, z1, x1, y2, z1), doubleArrayOf(x2, y1, z1, x2, y2, z1),
            doubleArrayOf(x2, y1, z2, x2, y2, z2), doubleArrayOf(x1, y1, z2, x1, y2, z2)
        )
        val pink = DustParticleOptions(Vector3f(1.0f, 0.15f, 0.7f), 1.0f)
        edges.forEach { drawLine(level, pink, it) }
    }

    private fun drawLine(level: ServerLevel, particle: DustParticleOptions, points: DoubleArray) {
        for (step in 0..10) {
            val t = step / 10.0
            val x = points[0] + (points[3] - points[0]) * t
            val y = points[1] + (points[4] - points[1]) * t
            val z = points[2] + (points[5] - points[2]) * t
            level.sendParticles(particle, x, y, z, 1, 0.0, 0.0, 0.0, 0.0)
        }
    }
}
