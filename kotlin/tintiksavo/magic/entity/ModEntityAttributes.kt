package tintiksavo.magic.entity

import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.Monster

object ModEntityAttributes {
    fun clown(): AttributeSupplier.Builder = baseMob().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 25.0).add(Attributes.FOLLOW_RANGE, 1.0).add(Attributes.KNOCKBACK_RESISTANCE, 0.0).add(Attributes.ARMOR, 32.0)
    fun ninjaClown(): AttributeSupplier.Builder = baseMob().add(Attributes.MAX_HEALTH, 45.0).add(Attributes.MOVEMENT_SPEED, 0.32).add(Attributes.ATTACK_DAMAGE, 50.0).add(Attributes.FOLLOW_RANGE, 1.5).add(Attributes.KNOCKBACK_RESISTANCE, 0.3).add(Attributes.ARMOR, 32.0)
    fun magicento(): AttributeSupplier.Builder = baseMob().add(Attributes.MAX_HEALTH, 50.0).add(Attributes.MOVEMENT_SPEED, 0.22).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 1.0).add(Attributes.KNOCKBACK_RESISTANCE, 0.2).add(Attributes.ARMOR, 40.0)
    fun magiquicento(): AttributeSupplier.Builder = baseMob().add(Attributes.MAX_HEALTH, 75.0).add(Attributes.MOVEMENT_SPEED, 0.2).add(Attributes.ATTACK_DAMAGE, 12.0).add(Attributes.FOLLOW_RANGE, 0.8).add(Attributes.KNOCKBACK_RESISTANCE, 0.3).add(Attributes.ARMOR, 40.0)
    fun bufonBoss(): AttributeSupplier.Builder = baseMob().add(Attributes.MAX_HEALTH, 300.0).add(Attributes.MOVEMENT_SPEED, 0.2).add(Attributes.ATTACK_DAMAGE, 15.0).add(Attributes.FOLLOW_RANGE, 1.0).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 48.0)
    private fun baseMob(): AttributeSupplier.Builder = Monster.createMonsterAttributes()
}
