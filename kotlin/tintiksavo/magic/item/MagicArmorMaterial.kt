package tintiksavo.magic.item

import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial

import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient

object MagicArmorMaterial : ArmorMaterial {
    override fun getDurabilityForType(type: ArmorItem.Type): Int = when (type) {
        ArmorItem.Type.HELMET -> 11 * 45
        ArmorItem.Type.CHESTPLATE -> 16 * 45
        ArmorItem.Type.LEGGINGS -> 15 * 45
        ArmorItem.Type.BOOTS -> 13 * 45
    }
    override fun getDefenseForType(type: ArmorItem.Type): Int = when (type) {
        ArmorItem.Type.HELMET -> 4
        ArmorItem.Type.CHESTPLATE -> 9
        ArmorItem.Type.LEGGINGS -> 7
        ArmorItem.Type.BOOTS -> 4
    }
    override fun getEnchantmentValue(): Int = 20
    override fun getEquipSound(): SoundEvent = SoundEvents.ARMOR_EQUIP_NETHERITE
    override fun getRepairIngredient(): Ingredient = Ingredient.of(Items.NETHERITE_INGOT)
    override fun getName(): String = "magic_armor"
    override fun getToughness(): Float = 11.5f
    override fun getKnockbackResistance(): Float = 0.1f
}
