package tintiksavo.magic.effect

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffect
import tintiksavo.magic.Magic

object ModEffects {
    val SHAKING: MobEffect = Registry.register(
        BuiltInRegistries.MOB_EFFECT,
        Magic.id("shaking"),
        ShakingEffect()
    )

    // El bytecode original deja este método vacío; la instancia estática ya registra el efecto.
    fun register() {}
}
