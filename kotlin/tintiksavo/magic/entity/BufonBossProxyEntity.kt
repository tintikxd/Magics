package tintiksavo.magic.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.level.Level
import java.util.UUID

/** Player-owned proxy used by /turn into form bufonboss. */
class BufonBossProxyEntity(entityType: EntityType<out Monster>, level: Level) : MagicEnemyEntity(entityType, level) {
    private var ownerUuid: UUID? = null

    override val magicVariant: MagicEnemyVariant get() = MagicEnemyVariant.BUFONBOSS

    fun setOwner(owner: Player) {
        ownerUuid = owner.uuid
    }

    fun isOwner(entity: Entity): Boolean = ownerUuid == entity.uuid

    override fun isAlliedTo(entity: Entity): Boolean = isOwner(entity) || super.isAlliedTo(entity)

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        ownerUuid?.let { tag.putUUID("OwnerUUID", it) }
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        ownerUuid = if (tag.hasUUID("OwnerUUID")) tag.getUUID("OwnerUUID") else null
    }
}
