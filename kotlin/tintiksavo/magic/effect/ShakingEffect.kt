package tintiksavo.magic.effect

import net.minecraft.server.level.ServerPlayer
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity

class ShakingEffect : MobEffect(MobEffectCategory.HARMFUL, 14056447) {
    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean = true

    override fun applyEffectTick(entity: LivingEntity, amplifier: Int) {
        val magnitude = 0.04 + amplifier * 0.02
        val x = (entity.random.nextDouble() - 0.5) * magnitude
        val z = (entity.random.nextDouble() - 0.5) * magnitude
        if (entity.level().isClientSide) {
            entity.deltaMovement = entity.deltaMovement.add(x, 0.0, z)
        } else {
            entity.knockback(x, 0.0, z)
            entity.hurtMarked = true
            if (entity is ServerPlayer) {
                entity.connection.send(ClientboundSetEntityMotionPacket(entity))
            }
        }
    }
}
