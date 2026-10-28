package tintiksavo.magic.network

import org.joml.Vector3f
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.monster.Monster
import tintiksavo.magic.Magic
import tintiksavo.magic.item.ModItems
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object KatanaAbilityHandler {
    val ABILITY_PACKET_ID: ResourceLocation = Magic.id("katana_ability")
    private const val ATTACK_RANGE = 7.0
    private const val ABILITY_DAMAGE = 10.0f

    private val spiralColors = listOf(
        Vector3f(0.12f, 0.42f, 1.0f), // azul
        Vector3f(0.62f, 0.16f, 1.0f), // violeta
        Vector3f(1.0f, 0.24f, 0.68f)  // rosa
    )

    fun registerServerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(ABILITY_PACKET_ID) { server: MinecraftServer, player: ServerPlayer, _, _, _ ->
            server.execute { performAbility(player) }
        }
    }

    private fun performAbility(player: ServerPlayer) {
        if (!player.mainHandItem.`is`(ModItems.KATANA)) return
        val level = player.level() as? ServerLevel ?: return

        // La habilidad consume la mitad de la durabilidad máxima por uso.
        // hurtAndBreak rompe la katana automáticamente si no le queda suficiente.
        val katana = player.mainHandItem
        val durabilityCost = (katana.maxDamage / 2).coerceAtLeast(1)
        katana.hurtAndBreak(durabilityCost, player) { p ->
            p.broadcastBreakEvent(EquipmentSlot.MAINHAND)
        }

        level.playSound(null, player.x, player.y, player.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.0f)
        val box = player.boundingBox.inflate(ATTACK_RANGE)
        for (monster in level.getEntitiesOfClass(Monster::class.java, box)) {
            if (!monster.isAlive || monster === player) continue
            monster.hurt(player.damageSources().playerAttack(player), ABILITY_DAMAGE)
            level.sendParticles(ParticleTypes.SWEEP_ATTACK, monster.x, monster.y + monster.bbHeight * 0.5, monster.z, 1, 0.0, 0.0, 0.0, 0.0)
            spawnColorSpiral(level, monster.x, monster.y, monster.z, monster.bbHeight)
        }

        level.sendParticles(ParticleTypes.SWEEP_ATTACK, player.x, player.y, player.z, 1, 0.0, 0.0, 0.0, 0.0)
        spawnColorSpiral(level, player.x, player.y, player.z, player.bbHeight)
    }

    /** Dibuja una hélice corta de partículas pequeñas alrededor de una entidad. */
    private fun spawnColorSpiral(level: ServerLevel, x: Double, y: Double, z: Double, entityHeight: Float) {
        val points = 27
        val turns = 2.25
        val radius = 0.48
        val height = (entityHeight * 0.85).coerceAtLeast(1.0)

        for (i in 0 until points) {
            val progress = i.toDouble() / (points - 1)
            val angle = progress * turns * 2.0 * PI
            val currentRadius = radius * (0.78 + 0.22 * sin(progress * PI))
            val px = x + cos(angle) * currentRadius
            val py = y + 0.12 + progress * height
            val pz = z + sin(angle) * currentRadius
            val color = spiralColors[i % spiralColors.size]
            val particle = DustParticleOptions(Vector3f(color.x(), color.y(), color.z()), 0.62f)
            level.sendParticles(particle, px, py, pz, 1, 0.0, 0.0, 0.0, 0.0)
        }
    }
}
