package tintiksavo.magic.item

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.SpawnEggItem
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers
import tintiksavo.magic.Magic
import tintiksavo.magic.entity.MagicEnemyVariant
import tintiksavo.magic.entity.ModEntities
import tintiksavo.magic.item.ModArmorItems
object ModItems {
    val CLOWN_SPAWN_EGG: Item = spawnEgg("clown", MagicEnemyVariant.CLOWN, ModEntities.CLOWN)
    val NINJACLOWN_SPAWN_EGG: Item = spawnEgg("ninjaclown", MagicEnemyVariant.NINJACLOWN, ModEntities.NINJACLOWN)
    val MAGICENTO_SPAWN_EGG: Item = spawnEgg("magicento", MagicEnemyVariant.MAGICENTO, ModEntities.MAGICENTO)
    val MAGIQUICENTO_SPAWN_EGG: Item = spawnEgg("magiquicento", MagicEnemyVariant.MAGIQUICENTO, ModEntities.MAGIQUICENTO)
    val BUFONBOSS_SPAWN_EGG: Item = spawnEgg("bufonboss", MagicEnemyVariant.BUFONBOSS, ModEntities.BUFONBOSS)
    val KATANA: Item = Registry.register(BuiltInRegistries.ITEM, Magic.id("katana"), SwordItem(Tiers.DIAMOND, 0, 0.5f, Item.Properties()))

    private fun spawnEgg(id: String, variant: MagicEnemyVariant, type: EntityType<out Mob>): Item = Registry.register(
        BuiltInRegistries.ITEM, Magic.id("${id}_spawn_egg"),
        SpawnEggItem(type, variant.primaryEggColor, variant.secondaryEggColor, Item.Properties())
    )

    fun register() {
        ItemGroupEvents.modifyEntriesEvent(net.minecraft.world.item.CreativeModeTabs.COMBAT).register { entries ->
            entries.accept(CLOWN_SPAWN_EGG)
            entries.accept(NINJACLOWN_SPAWN_EGG)
            entries.accept(MAGICENTO_SPAWN_EGG)
            entries.accept(MAGIQUICENTO_SPAWN_EGG)
            entries.accept(KATANA)
            entries.accept(BUFONBOSS_SPAWN_EGG)

        }
    }
}
