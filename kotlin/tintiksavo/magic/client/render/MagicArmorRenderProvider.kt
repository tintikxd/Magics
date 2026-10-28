package tintiksavo.magic.client.render

import net.minecraft.client.model.HumanoidModel
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import software.bernie.geckolib.animatable.client.RenderProvider

/**
 * Un RenderProvider independiente para cada MagicArmorItem.
 *
 * GeoArmorRenderer recibe el HumanoidModel vanilla actual mediante
 * prepForRender(...) y usa esa pose para mover cabeza, cuerpo, brazos y piernas.
 */
class MagicArmorRenderProvider : RenderProvider {

    private var renderer: MagicArmorRenderer? = null

    override fun getHumanoidArmorModel(
        livingEntity: LivingEntity,
        itemStack: ItemStack,
        equipmentSlot: EquipmentSlot,
        original: HumanoidModel<LivingEntity>
    ): HumanoidModel<LivingEntity> {

        val armorRenderer = renderer
            ?: MagicArmorRenderer().also { renderer = it }

        armorRenderer.armorSlot = equipmentSlot

        armorRenderer.prepForRender(
            livingEntity,
            itemStack,
            equipmentSlot,
            original
        )

        @Suppress("UNCHECKED_CAST")
        return armorRenderer as HumanoidModel<LivingEntity>
    }
}