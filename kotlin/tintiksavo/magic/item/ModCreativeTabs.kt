package tintiksavo.magic.item

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import tintiksavo.magic.Magic
import tintiksavo.magic.block.ModBlocks

object ModCreativeTabs {
    val MAGIC_TAB: CreativeModeTab = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        Magic.id("magic_tab"),
        FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.magic.magic_tab"))
            .icon { ItemStack(ModBlocks.CENTER_BLOCK) }
            .displayItems { _, entries ->
                listOf(
                    ModBlocks.NINJA_BLOCK, ModBlocks.PILAR_NINJA_BLOCK, ModBlocks.CIRCUS_BLOCK,
                    ModBlocks.CENTER_BLOCK, ModBlocks.BLUESTONE_BRICKS, ModBlocks.BLUESTONE_BRICK,
                    ModBlocks.CHISELED_POLISHED__BLUESTONE, ModBlocks.POLISHED__BLUESTONE,
                    ModBlocks.CRACKED_POLISHED__BLUESTONE_BRICKS, ModBlocks.POLISHED__BLUESTONE_BRICKS,
                    ModBlocks.BLUESLATE_BRICKS, ModBlocks.BLUESLATE_TILES, ModBlocks.CHISELED_BLUESLATE,
                    ModBlocks.BLUESLATE, ModBlocks.CRACKED_BLUESLATE_BRICKS, ModBlocks.DARK_BRICKS
                ).forEach(entries::accept)
                ModBlocks.STAIRS_MAP.values.forEach { entries.accept(it) }


                ModArmorItems.SETS.values.flatten().forEach { entries.accept(it) }
            }.build()
    )

    fun register() {}
}
