package tintiksavo.magic

import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.slf4j.LoggerFactory
import tintiksavo.magic.block.ModBlocks
import tintiksavo.magic.command.BossTransformationManager
import tintiksavo.magic.command.ModCommands
import tintiksavo.magic.effect.ModEffects
import tintiksavo.magic.entity.ModEntities
import tintiksavo.magic.item.ModArmorItems
import tintiksavo.magic.item.ModCreativeTabs
import tintiksavo.magic.item.ModItems
import tintiksavo.magic.music.MusicServerManager
import tintiksavo.magic.network.KatanaAbilityHandler



object Magic : ModInitializer {
    const val MOD_ID: String = "magic"

    private val LOGGER = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        ModEffects.register()
        ModBlocks.register()
        ModArmorItems.register()
        ModCreativeTabs.register()
        ModCommands.register()
        ModEntities.register()
        ModItems.register()

        ModCommands.register()
        BossTransformationManager.register()
        KatanaAbilityHandler.registerServerPacket()
        MusicServerManager.register()
        LOGGER.info("Magic enemies loaded.")
    }

    fun id(path: String): ResourceLocation = ResourceLocation(MOD_ID, path)
}