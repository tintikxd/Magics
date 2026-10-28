
package tintiksavo.magic.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.EquipmentSlot
import org.lwjgl.glfw.GLFW
import tintiksavo.magic.Magic
import tintiksavo.magic.client.music.MusicClientController
import tintiksavo.magic.client.render.ArmorVisual
import tintiksavo.magic.client.render.ArmorVisuals
import tintiksavo.magic.client.render.MagicArmorRenderProvider
import tintiksavo.magic.client.render.MagicEnemyRenderer
import tintiksavo.magic.entity.ModEntities
import tintiksavo.magic.item.MagicArmorBridge
import tintiksavo.magic.item.ModItems
import tintiksavo.magic.network.KatanaAbilityHandler
import java.util.function.Supplier

object MagicClient : ClientModInitializer {

	lateinit var katanaAbilityKey: KeyMapping

	override fun onInitializeClient() {
		// --- REGISTRO DE ARMADURAS GECKOLIB ---
		MagicArmorBridge.providerFactory = Supplier<Any?> {
			MagicArmorRenderProvider()
		}

		ArmorVisuals.register(
			"magic_armor",
			ArmorVisual(
				baseTexture = Magic.id("textures/armor/magic_armor.png"),
				cosmeticTexture = Magic.id("textures/armor/magic_armor_cosmetic.png")
			)
		)

		// --- REGISTRO DE RENDERIZADORES ---
		EntityRendererRegistry.register(ModEntities.CLOWN) { context ->
			MagicEnemyRenderer(context)
		}

		EntityRendererRegistry.register(ModEntities.NINJACLOWN) { context ->
			MagicEnemyRenderer(context)
		}

		EntityRendererRegistry.register(ModEntities.MAGICENTO) { context ->
			MagicEnemyRenderer(context)
		}

		EntityRendererRegistry.register(ModEntities.MAGIQUICENTO) { context ->
			MagicEnemyRenderer(context)
		}

		EntityRendererRegistry.register(ModEntities.BUFONBOSS) { context ->
			MagicEnemyRenderer(context)
		}

		EntityRendererRegistry.register(ModEntities.MOTHERWITCH) { context ->
			MagicEnemyRenderer(context)
		}

		EntityRendererRegistry.register(ModEntities.ETERNALWITCH) { context ->
			MagicEnemyRenderer(context)
		}

		EntityRendererRegistry.register(ModEntities.ENGINEEREVOKER) { context ->
			MagicEnemyRenderer(context)
		}

		EntityRendererRegistry.register(ModEntities.BUFONBOSS_PROXY) { context ->
			MagicEnemyRenderer(context)
		}

		// --- REGISTRO DE LA TECLA C ---
		katanaAbilityKey = KeyBindingHelper.registerKeyBinding(
			KeyMapping(
				"key.magic.katana_ability",
				GLFW.GLFW_KEY_C,
				"category.magic.general"
			)
		)

		// --- HABILIDAD DE LA KATANA ---
		ClientTickEvents.END_CLIENT_TICK.register { client ->
			val player = client.player ?: return@register

			while (katanaAbilityKey.consumeClick()) {
				if (player.mainHandItem.`is`(ModItems.KATANA)) {
					ClientPlayNetworking.send(
						KatanaAbilityHandler.ABILITY_PACKET_ID,
						PacketByteBufs.create()
					)
				}
			}
		}

		MusicClientController.register()
	}
}
