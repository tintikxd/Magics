package tintiksavo.magic.entity

import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EntityType.EntityFactory
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.SpawnPlacements
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.Heightmap
import tintiksavo.magic.Magic

/** Entity registrations recovered from the mod JAR. */
object ModEntities {
    val CLOWN: EntityType<ClownEntity> = registerEntity("clown", MagicEnemyVariant.CLOWN, { type, level -> ClownEntity(type, level) })
    val NINJACLOWN: EntityType<NinjaClownEntity> = registerEntity("ninjaclown", MagicEnemyVariant.NINJACLOWN, { type, level -> NinjaClownEntity(type, level) })
    val MAGICENTO: EntityType<MagicentoEntity> = registerEntity("magicento", MagicEnemyVariant.MAGICENTO, { type, level -> MagicentoEntity(type, level) })
    val MAGIQUICENTO: EntityType<MagiquicentoEntity> = registerEntity("magiquicento", MagicEnemyVariant.MAGIQUICENTO, { type, level -> MagiquicentoEntity(type, level) })
    val BUFONBOSS: EntityType<BufonBossEntity> = registerEntity("bufonboss", MagicEnemyVariant.BUFONBOSS, { type, level -> BufonBossEntity(type, level) })
    val MOTHERWITCH: EntityType<MotherwitchEntity> = registerEntity("motherwitch", MagicEnemyVariant.MOTHERWITCH, { type, level -> MotherwitchEntity(type, level) })
    val ETERNALWITCH: EntityType<EternalwitchEntity> = registerEntity("eternalwitch", MagicEnemyVariant.ETERNALWITCH, { type, level -> EternalwitchEntity(type, level) })
    val ENGINEEREVOKER: EntityType<EngineerevokerEntity> = registerEntity("engineerevoker", MagicEnemyVariant.ENGINEEREVOKER, { type, level -> EngineerevokerEntity(type, level) })
    val BUFONBOSS_PROXY: EntityType<BufonBossProxyEntity> = registerEntity("bufonboss_proxy", MagicEnemyVariant.BUFONBOSS, { type, level -> BufonBossProxyEntity(type, level) })

    fun register() {
        FabricDefaultAttributeRegistry.register(CLOWN, MagicEnemyEntity.createAttributes(MagicEnemyVariant.CLOWN))
        FabricDefaultAttributeRegistry.register(NINJACLOWN, MagicEnemyEntity.createAttributes(MagicEnemyVariant.NINJACLOWN))
        FabricDefaultAttributeRegistry.register(MAGICENTO, MagicEnemyEntity.createAttributes(MagicEnemyVariant.MAGICENTO))
        FabricDefaultAttributeRegistry.register(MAGIQUICENTO, MagicEnemyEntity.createAttributes(MagicEnemyVariant.MAGIQUICENTO))
        FabricDefaultAttributeRegistry.register(BUFONBOSS, MagicEnemyEntity.createAttributes(MagicEnemyVariant.BUFONBOSS))
        FabricDefaultAttributeRegistry.register(MOTHERWITCH, MagicEnemyEntity.createAttributes(MagicEnemyVariant.MOTHERWITCH))
        FabricDefaultAttributeRegistry.register(ETERNALWITCH, MagicEnemyEntity.createAttributes(MagicEnemyVariant.ETERNALWITCH))
        FabricDefaultAttributeRegistry.register(ENGINEEREVOKER, MagicEnemyEntity.createAttributes(MagicEnemyVariant.ENGINEEREVOKER))
        FabricDefaultAttributeRegistry.register(BUFONBOSS_PROXY, MagicEnemyEntity.createAttributes(MagicEnemyVariant.BUFONBOSS))
        registerNaturalSpawns()
    }

    private fun registerNaturalSpawns() {
        registerMonsterSpawn(CLOWN, 45, 1, 3)
        registerMonsterSpawn(NINJACLOWN, 28, 1, 2)
        registerMonsterSpawn(MAGICENTO, 18, 1, 1)
        registerMonsterSpawn(MAGIQUICENTO, 14, 1, 1)
        registerMonsterSpawn(ETERNALWITCH, 8, 1, 1)
        registerMonsterSpawn(ENGINEEREVOKER, 6, 1, 1)
    }

    private fun <T : Monster> registerMonsterSpawn(type: EntityType<T>, weight: Int, minGroup: Int, maxGroup: Int) {
        SpawnPlacements.register(
            type,
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES
        ) { entityType, level, spawnType, pos, random ->
            Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random)
        }
        BiomeModifications.addSpawn(
            BiomeSelectors.foundInOverworld(), MobCategory.MONSTER, type, weight, minGroup, maxGroup
        )
    }

    private fun <T : Monster> registerEntity(
        name: String,
        variant: MagicEnemyVariant,
        factory: EntityFactory<T>
    ): EntityType<T> {
        val builder = FabricEntityTypeBuilder.create(MobCategory.MONSTER, factory)
            .dimensions(EntityDimensions.scalable(variant.width, variant.height))
            .trackRangeBlocks(if (variant.boss) 12 else 8)
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, Magic.id(name), builder.build())
    }
}
