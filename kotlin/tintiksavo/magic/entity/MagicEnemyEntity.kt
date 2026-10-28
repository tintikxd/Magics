package tintiksavo.magic.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.FlyingMoveControl
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager
import software.bernie.geckolib.core.animation.AnimationController
import software.bernie.geckolib.core.animation.AnimationState
import software.bernie.geckolib.core.animation.RawAnimation
import software.bernie.geckolib.core.`object`.PlayState
import software.bernie.geckolib.util.GeckoLibUtil

abstract class MagicEnemyEntity(
    entityType: EntityType<out Monster>,
    level: Level
) : Monster(entityType, level), GeoEntity {

    private val animatableCache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)

    val variant: MagicEnemyVariant
        get() = magicVariant

    abstract val magicVariant: MagicEnemyVariant

    private fun isFlyingVariant(): Boolean =
        magicVariant == MagicEnemyVariant.MAGICENTO || magicVariant == MagicEnemyVariant.MAGIQUICENTO

    init {
        if (isFlyingVariant()) {
            setNoGravity(true)
            moveControl = FlyingMoveControl(this, 20, true)
        }
    }

    override fun createNavigation(level: Level): PathNavigation {
        return if (isFlyingVariant()) FlyingPathNavigation(this, level) else super.createNavigation(level)
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))

        when (magicVariant.attackType) {
            AttackType.MELEE -> goalSelector.addGoal(2, MeleeAttackGoal(this, 1.15, false))
            AttackType.ABILITY -> when (magicVariant) {
                MagicEnemyVariant.MAGICENTO -> goalSelector.addGoal(2, MagicentoAbilityGoal(this))
                MagicEnemyVariant.MAGIQUICENTO -> goalSelector.addGoal(2, MagiquicentoAbilityGoal(this))
                MagicEnemyVariant.NINJACLOWN -> goalSelector.addGoal(2, NinjaClownAbilityGoal(this))
                MagicEnemyVariant.MOTHERWITCH -> goalSelector.addGoal(2, MotherwitchAbilityGoal(this))
                MagicEnemyVariant.ETERNALWITCH -> goalSelector.addGoal(2, EternalwitchSupportGoal(this))
                MagicEnemyVariant.ENGINEEREVOKER -> goalSelector.addGoal(2, EngineerevokerAbilityGoal(this))
                else -> Unit
            }
        }

        if (isFlyingVariant()) {
            goalSelector.addGoal(5, WaterAvoidingRandomFlyingGoal(this, 1.0))
        } else {
            goalSelector.addGoal(6, WaterAvoidingRandomStrollGoal(this, 0.9))
        }
        goalSelector.addGoal(7, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))

        targetSelector.addGoal(1, HurtByTargetGoal(this).setAlertOthers())
        targetSelector.addGoal(2, NearestAttackableTargetGoal(this, Player::class.java, true))
    }

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar) {
        controllers.add(AnimationController(this, "living_controller", 5) { state -> livingPredicate(state) })
        controllers.add(
            AnimationController(this, "action_controller", 3) { state -> actionPredicate(state) }
                .triggerableAnim("attack1", ATTACK_ONE)
                .triggerableAnim("attack2", ATTACK_TWO)
                .triggerableAnim("melee_attack", ATTACK_BASE)
        )
    }

    private fun livingPredicate(state: AnimationState<MagicEnemyEntity>): PlayState {
        state.controller.setAnimation(if (state.isMoving) WALK else IDLE)
        return PlayState.CONTINUE
    }

    // This controller is controlled by explicit one-shot triggers, not by aggression state.
    private fun actionPredicate(state: AnimationState<MagicEnemyEntity>): PlayState = PlayState.STOP

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache = animatableCache

    override fun getMaxHeadXRot(): Int = if (magicVariant.boss) 20 else super.getMaxHeadXRot()

    companion object {
        private val IDLE = RawAnimation.begin().thenLoop("idle")
        private val WALK = RawAnimation.begin().thenLoop("walk")
        private val ATTACK_BASE = RawAnimation.begin().thenPlay("attack")
        private val ATTACK_ONE = RawAnimation.begin().thenPlay("attack1")
        private val ATTACK_TWO = RawAnimation.begin().thenPlay("attack2")

        fun createAttributes(variant: MagicEnemyVariant): AttributeSupplier.Builder =
            Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, variant.maxHealth)
                .add(Attributes.ATTACK_DAMAGE, variant.attackDamage)
                .add(Attributes.MOVEMENT_SPEED, variant.movementSpeed)
                .add(Attributes.FOLLOW_RANGE, variant.followRange)
                .add(Attributes.FLYING_SPEED, variant.followRange)
                .add(Attributes.KNOCKBACK_RESISTANCE, if (variant.boss) 0.65 else 0.05)
    }
}
