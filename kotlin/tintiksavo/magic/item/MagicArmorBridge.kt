package tintiksavo.magic.item

import java.util.function.Supplier

object MagicArmorBridge {
    @Volatile
    var providerFactory: Supplier<Any?>? = null
}
