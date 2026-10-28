package tintiksavo.magic.item

import software.bernie.geckolib.animatable.GeoItem
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager
import software.bernie.geckolib.util.GeckoLibUtil
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item
import java.util.function.Consumer
import java.util.function.Supplier

class MagicArmorItem(
    material: ArmorMaterial,
    type: ArmorItem.Type,
    properties: Item.Properties,
    val armorId: String
) : ArmorItem(material, type, properties), GeoItem {
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)
    private val renderProvider: Supplier<Any?> = GeoItem.makeRenderer(this)

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar) {}
    override fun getAnimatableInstanceCache(): AnimatableInstanceCache = cache

    override fun createRenderer(consumer: Consumer<Any>) {
        val renderer = MagicArmorBridge.providerFactory?.get()
        if (renderer != null) consumer.accept(renderer)
    }

    override fun getRenderProvider(): Supplier<Any?> = renderProvider
}
