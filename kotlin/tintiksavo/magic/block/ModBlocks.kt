package tintiksavo.magic.block

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import tintiksavo.magic.Magic

/** Reconstruido a partir del bytecode de ModBlocks.class del JAR magic-1.0.0.jar. */
object ModBlocks {
    val NINJA_BLOCK: Block = registerBlock("ninja_block", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val PILAR_NINJA_BLOCK: Block = registerBlock("pilar_ninja_block", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val CENTER_BLOCK: Block = registerBlock("center_block", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val CIRCUS_BLOCK: Block = registerBlock("circus_block", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val BLUESTONE_BRICKS: Block = registerBlock("bluestone_bricks", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val BLUESTONE_BRICK: Block = registerBlock("bluestone_brick", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val CHISELED_POLISHED__BLUESTONE: Block = registerBlock("chiseled_polished_bluestone", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val POLISHED__BLUESTONE: Block = registerBlock("polished_bluestone", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val CRACKED_POLISHED__BLUESTONE_BRICKS: Block = registerBlock("cracked_polished_bluestone_bricks", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val POLISHED__BLUESTONE_BRICKS: Block = registerBlock("polished_bluestone_bricks", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val BLUESLATE_BRICKS: Block = registerBlock("blueslate_bricks", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val BLUESLATE_TILES: Block = registerBlock("blueslate_tiles", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val CHISELED_BLUESLATE: Block = registerBlock("chiseled_blueslate", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val BLUESLATE: Block = registerBlock("blueslate", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val CRACKED_BLUESLATE_BRICKS: Block = registerBlock("cracked_blueslate_bricks", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))
    val DARK_BRICKS: Block = registerBlock("dark_bricks", Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)))

    val STAIRS_MAP: Map<Block, Block> = mapOf(
        BLUESTONE_BRICKS to registerBlock(
            "bluestone_bricks_stairs",
            StairBlock(BLUESTONE_BRICKS.defaultBlockState(), BlockBehaviour.Properties.copy(BLUESTONE_BRICKS))
        ),
        BLUESTONE_BRICK to registerBlock(
            "bluestone_brick_stairs",
            StairBlock(BLUESTONE_BRICK.defaultBlockState(), BlockBehaviour.Properties.copy(BLUESTONE_BRICK))
        ),
        POLISHED__BLUESTONE to registerBlock(
            "polished_bluestone_stairs",
            StairBlock(POLISHED__BLUESTONE.defaultBlockState(), BlockBehaviour.Properties.copy(POLISHED__BLUESTONE))
        ),
        POLISHED__BLUESTONE_BRICKS to registerBlock(
            "polished_bluestone_bricks_stairs",
            StairBlock(POLISHED__BLUESTONE_BRICKS.defaultBlockState(), BlockBehaviour.Properties.copy(POLISHED__BLUESTONE_BRICKS))
        ),
        BLUESLATE_BRICKS to registerBlock(
            "blueslate_bricks_stairs",
            StairBlock(BLUESLATE_BRICKS.defaultBlockState(), BlockBehaviour.Properties.copy(BLUESLATE_BRICKS))
        ),
        BLUESLATE_TILES to registerBlock(
            "blueslate_tiles_stairs",
            StairBlock(BLUESLATE_TILES.defaultBlockState(), BlockBehaviour.Properties.copy(BLUESLATE_TILES))
        ),
        BLUESLATE to registerBlock(
            "blueslate_stairs",
            StairBlock(BLUESLATE.defaultBlockState(), BlockBehaviour.Properties.copy(BLUESLATE))
        ),
        DARK_BRICKS to registerBlock(
            "dark_bricks_stairs",
            StairBlock(DARK_BRICKS.defaultBlockState(), BlockBehaviour.Properties.copy(DARK_BRICKS))
        )
    )

    private fun registerBlock(name: String, block: Block): Block {
        val id = Magic.id(name)
        val registeredBlock = Registry.register(BuiltInRegistries.BLOCK, id, block)
        Registry.register(BuiltInRegistries.ITEM, id, BlockItem(registeredBlock, Item.Properties()))
        return registeredBlock
    }

    // El método está vacío en el bytecode original.
    fun register() {}
}
