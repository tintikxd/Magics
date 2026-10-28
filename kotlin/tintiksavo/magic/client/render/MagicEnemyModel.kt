package tintiksavo.magic.client.render

import net.minecraft.resources.ResourceLocation
import software.bernie.geckolib.model.GeoModel
import tintiksavo.magic.Magic
import tintiksavo.magic.entity.MagicEnemyEntity

class MagicEnemyModel<T : MagicEnemyEntity> : GeoModel<T>() {
	override fun getModelResource(animatable: T): ResourceLocation =
		Magic.id("geo/entity/${animatable.variant.id}.geo.json")

	override fun getTextureResource(animatable: T): ResourceLocation =
		Magic.id("textures/entity/${animatable.variant.id}.png")

	override fun getAnimationResource(animatable: T): ResourceLocation =
		Magic.id("animations/entity/${animatable.variant.id}.animation.json")
}
