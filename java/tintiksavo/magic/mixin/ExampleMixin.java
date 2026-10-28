package tintiksavo.magic.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Reconstructed from ExampleMixin.class; the injected body in the JAR is empty. */
@Mixin(MinecraftServer.class)
public abstract class ExampleMixin {
    @Inject(method = "runServer", at = @At("HEAD"))
    private void magic$onServerStart(CallbackInfo ci) {
        // Empty in the compiled JAR.
    }
}
