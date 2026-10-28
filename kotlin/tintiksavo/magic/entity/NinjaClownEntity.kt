package tintiksavo.magic.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import tintiksavo.magic.item.ModItems

class NinjaClownEntity(entityType: EntityType<out Monster>, level: Level) : MagicEnemyEntity(entityType, level) {
    init {
        if (!level.isClientSide) {
            setItemSlot(EquipmentSlot.MAINHAND, ItemStack(ModItems.KATANA))
        }
    }

    override val magicVariant: MagicEnemyVariant
        get() = MagicEnemyVariant.NINJACLOWN
}
