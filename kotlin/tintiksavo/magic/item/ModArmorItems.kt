package tintiksavo.magic.item

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.Item
import tintiksavo.magic.Magic

object ModArmorItems {
    val SETS: MutableMap<String, List<MagicArmorItem>> = LinkedHashMap()
    val MAGIC_ARMOR: List<MagicArmorItem> = registerSet("magic_armor", "magic", MagicArmorMaterial)

    fun registerSet(armorId: String, itemPrefix: String, material: net.minecraft.world.item.ArmorMaterial): List<MagicArmorItem> {
        val items = listOf(
            piece(armorId, "${itemPrefix}_helmet", material, ArmorItem.Type.HELMET),
            piece(armorId, "${itemPrefix}_chestplate", material, ArmorItem.Type.CHESTPLATE),
            piece(armorId, "${itemPrefix}_leggings", material, ArmorItem.Type.LEGGINGS),
            piece(armorId, "${itemPrefix}_boots", material, ArmorItem.Type.BOOTS)
        )
        SETS[armorId] = items
        return items
    }

    private fun piece(armorId: String, itemId: String, material: net.minecraft.world.item.ArmorMaterial, type: ArmorItem.Type): MagicArmorItem =
        Registry.register(BuiltInRegistries.ITEM, Magic.id(itemId), MagicArmorItem(material, type, Item.Properties(), armorId))

    // El método register() está vacío en el bytecode del JAR: el set se registra en el inicializador estático.
    fun register() {}
}
