package tintiksavo.magic.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.level.Level

class MagicentoEntity(entityType: EntityType<out Monster>, level: Level) : MagicEnemyEntity(entityType, level) {
    override val magicVariant: MagicEnemyVariant get() = MagicEnemyVariant.MAGICENTO
}
