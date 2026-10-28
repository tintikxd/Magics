package tintiksavo.magic.client.render

import net.minecraft.resources.ResourceLocation
import software.bernie.geckolib.animatable.GeoItem
import software.bernie.geckolib.model.GeoModel
import tintiksavo.magic.Magic

// Un solo archivo de modelo para TODAS las armaduras de GeckoLib
class DynamicArmorModel<T>(private val armorName: String) : GeoModel<T>() where T : GeoItem {
    override fun getModelResource(animatable: T): ResourceLocation =
        Magic.id("geo/armor/$armorName.geo.json")

    override fun getTextureResource(animatable: T): ResourceLocation =
        Magic.id("textures/armor/$armorName.png")

    override fun getAnimationResource(animatable: T): ResourceLocation =
        Magic.id("animations/armor/$armorName.animation.json")
}
