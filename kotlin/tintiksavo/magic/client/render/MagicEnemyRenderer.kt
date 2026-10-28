package tintiksavo.magic.client.render

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import software.bernie.geckolib.cache.`object`.GeoBone
import software.bernie.geckolib.renderer.GeoEntityRenderer
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer
import tintiksavo.magic.entity.MagicEnemyEntity

class MagicEnemyRenderer<T : MagicEnemyEntity>(
	context: EntityRendererProvider.Context
) : GeoEntityRenderer<T>(
	context,
	MagicEnemyModel()
) {

	init {

		addRenderLayer(
			object : BlockAndItemGeoLayer<T>(this) {

				override fun getStackForBone(
					bone: GeoBone,
					animatable: T
				): ItemStack? {

					if (
						bone.name ==
						"RightHandItem"
					) {

						return animatable.getItemBySlot(
							EquipmentSlot.MAINHAND
						)
					}

					return null
				}

				override fun getTransformTypeForStack(
					bone: GeoBone,
					stack: ItemStack,
					animatable: T
				): ItemDisplayContext {

					return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
				}
			}
		)
	}

	override fun getMotionAnimThreshold(
		animatable: T
	): Float =
		0.01f
}