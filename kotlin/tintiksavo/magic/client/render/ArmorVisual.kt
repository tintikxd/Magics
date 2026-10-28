package tintiksavo.magic.client.render

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlot
import tintiksavo.magic.Magic

/**
 * Configuración visual de UN set de armadura. Es el equivalente a MagicEnemyVariant pero para armaduras.
 *
 * @param baseTexture      textura del render principal (todo el set).
 * @param cosmeticTexture  textura de la segunda pasada, solo para los huesos cosméticos. null = sin cosméticos.
 * @param cosmeticBones    por slot, nombres de huesos cosméticos. Un nombre terminado en "*" es prefijo
 *                         (ej. "horns*"). Los hijos de un hueso cosmético también cuentan como cosméticos.
 */
class ArmorVisual(
    val baseTexture: ResourceLocation,
    val cosmeticTexture: ResourceLocation? = null,
    private val cosmeticBones: Map<EquipmentSlot, List<String>> = emptyMap(),
) {
    fun hasCosmetics(slot: EquipmentSlot): Boolean =
        cosmeticTexture != null && cosmeticBones[slot].orEmpty().isNotEmpty()

    fun isCosmeticBone(boneName: String, slot: EquipmentSlot): Boolean =
        cosmeticBones[slot].orEmpty().any { pattern ->
            if (pattern.endsWith("*")) boneName.startsWith(pattern.dropLast(1)) else boneName == pattern
        }

    companion object {
        /** Set sin cosméticos: geo y textura por convención, una sola pasada. */
        fun plain(armorId: String) =
            ArmorVisual(Magic.id("textures/armor/$armorId.png"))

    }
}

object ArmorVisuals {
    private val configs = HashMap<String, ArmorVisual>()

    fun register(armorId: String, visual: ArmorVisual) {
        configs[armorId] = visual
    }

    fun get(armorId: String): ArmorVisual = configs.getOrPut(armorId) { ArmorVisual.plain(armorId) }
}