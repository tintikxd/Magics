package tintiksavo.magic.client.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.entity.EquipmentSlot
import software.bernie.geckolib.cache.`object`.BakedGeoModel
import software.bernie.geckolib.cache.`object`.GeoBone
import software.bernie.geckolib.renderer.layer.GeoRenderLayer
import tintiksavo.magic.item.MagicArmorItem


class MagicCosmeticGeoLayer(
    private val armorRenderer: MagicArmorRenderer
) : GeoRenderLayer<MagicArmorItem>(armorRenderer) {

    /** Estado original de un hueso: (oculto, hijos ocultos). */
    private class HiddenState(val self: Boolean, val children: Boolean)

    override fun render(
        poseStack: PoseStack,
        animatable: MagicArmorItem,
        bakedModel: BakedGeoModel,
        renderType: RenderType?,
        bufferSource: MultiBufferSource,
        buffer: VertexConsumer?,
        partialTick: Float,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val slot: EquipmentSlot = armorRenderer.armorSlot ?: return
        val visual = ArmorVisuals.get(animatable.armorId)
        if (!visual.hasCosmetics(slot)) return
        val cosmeticTexture = visual.cosmeticTexture ?: return

        val cosmeticType = armorRenderer.getRenderType(animatable, cosmeticTexture, bufferSource, partialTick) ?: return

        val saved = HashMap<GeoBone, HiddenState>()
        for (bone in bakedModel.topLevelBones()) saveState(bone, saved)

        try {
            for (bone in bakedModel.topLevelBones()) applyCosmeticVisibility(bone, slot, visual, false)

            armorRenderer.reRender(
                bakedModel, poseStack, bufferSource, animatable,
                cosmeticType, bufferSource.getBuffer(cosmeticType),
                partialTick, packedLight, packedOverlay,
                1f, 1f, 1f, 1f
            )
        } finally {
            for ((bone, state) in saved) bone.setHidden(state.self)
        }
    }

    private fun saveState(bone: GeoBone, out: MutableMap<GeoBone, HiddenState>) {
        out[bone] = HiddenState(bone.isHidden(), bone.isHidingChildren())
        for (child in bone.getChildBones()) saveState(child, out)
    }

    /**
     * Visible solo si el hueso (o un ancestro) es cosmético para este slot.
     * Los huesos no cosméticos se ocultan a sí mismos pero dejan pasar a sus hijos.
     */
    private fun applyCosmeticVisibility(bone: GeoBone, slot: EquipmentSlot, visual: ArmorVisual, insideCosmetic: Boolean) {
        val cosmetic = insideCosmetic || visual.isCosmeticBone(bone.getName(), slot)
        bone.setHidden(!cosmetic)
        for (child in bone.getChildBones()) applyCosmeticVisibility(child, slot, visual, cosmetic)
    }
}