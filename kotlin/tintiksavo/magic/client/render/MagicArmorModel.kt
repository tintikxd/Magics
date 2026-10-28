package tintiksavo.magic.client.render

import net.minecraft.resources.ResourceLocation
import software.bernie.geckolib.model.GeoModel
import tintiksavo.magic.Magic
import tintiksavo.magic.item.MagicArmorItem


class MagicArmorModel : GeoModel<MagicArmorItem>() {

    override fun getModelResource(animatable: MagicArmorItem): ResourceLocation =
        Magic.id("geo/armor/${animatable.armorId}.geo.json")

    override fun getTextureResource(animatable: MagicArmorItem): ResourceLocation =
        ArmorVisuals.get(animatable.armorId).baseTexture

    override fun getAnimationResource(animatable: MagicArmorItem): ResourceLocation =
        Magic.id("animations/armor/${animatable.armorId}.animation.json")
}