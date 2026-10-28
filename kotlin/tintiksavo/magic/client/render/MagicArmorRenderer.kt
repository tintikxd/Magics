package tintiksavo.magic.client.render

import net.minecraft.world.entity.EquipmentSlot
import software.bernie.geckolib.renderer.GeoArmorRenderer
import tintiksavo.magic.item.MagicArmorItem


class MagicArmorRenderer : GeoArmorRenderer<MagicArmorItem>(MagicArmorModel()) {

    /**
     * Slot actualmente renderizado.
     * Lo usa MagicCosmeticGeoLayer.
     */
    var armorSlot: EquipmentSlot? = null

    init {
        addRenderLayer(MagicCosmeticGeoLayer(this))
    }
}