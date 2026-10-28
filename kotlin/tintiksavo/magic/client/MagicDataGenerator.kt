package tintiksavo.magic.client

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import tintiksavo.magic.block.ModBlocks

object MagicDataGenerator : DataGeneratorEntrypoint {

	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()
		pack.addProvider(::ModModelProvider)
	}

	private class ModModelProvider(output: net.fabricmc.fabric.api.datagen.v1.FabricDataOutput) : FabricModelProvider(output) {

		override fun generateBlockStateModels(blockStateModelGenerator: BlockModelGenerators) {
			// Lista con todos tus bloques cúbicos
			val allCubicBlocks = listOf(
				ModBlocks.NINJA_BLOCK,
				ModBlocks.PILAR_NINJA_BLOCK,
				ModBlocks.CIRCUS_BLOCK,
				ModBlocks.CENTER_BLOCK,

				ModBlocks.BLUESTONE_BRICKS,
				ModBlocks.BLUESTONE_BRICK,
				ModBlocks.CHISELED_POLISHED__BLUESTONE,
				ModBlocks.POLISHED__BLUESTONE,
				ModBlocks.CRACKED_POLISHED__BLUESTONE_BRICKS,
				ModBlocks.POLISHED__BLUESTONE_BRICKS,

				ModBlocks.BLUESLATE_BRICKS,
				ModBlocks.BLUESLATE_TILES,
				ModBlocks.CHISELED_BLUESLATE,
				ModBlocks.BLUESLATE,
				ModBlocks.CRACKED_BLUESLATE_BRICKS,
				ModBlocks.DARK_BRICKS
			)

			// 1. REGISTRO DE BLOQUES CÚBICOS BASE
			// Descarta los cubos que actúan como base en el mapa de escaleras para evitar conflictos
			allCubicBlocks.filterNot { ModBlocks.STAIRS_MAP.containsKey(it) }.forEach { cubicBlock ->
				blockStateModelGenerator.createTrivialCube(cubicBlock)
			}

			// 2. GENERADOR AUTOMÁTICO DE ESCALERAS
			ModBlocks.STAIRS_MAP.forEach { (baseBlock, stairsBlock) ->
				// .family() registra automáticamente tanto el cubo base como el blockstate,
				// el modelo físico .json de la escalera, y su respectivo ítem de inventario.
				val familyProvider = blockStateModelGenerator.family(baseBlock)
				familyProvider.stairs(stairsBlock)
			}
		}

		override fun generateItemModels(itemModelGenerator: ItemModelGenerators) {
			// Se deja vacío ya que la función familyProvider.stairs() de arriba
			// ya genera de manera interna los modelos correspondientes para los ítems.
		}
	}
}
