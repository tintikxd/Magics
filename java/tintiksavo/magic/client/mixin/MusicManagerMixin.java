package tintiksavo.magic.client.mixin;

import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tintiksavo.magic.client.music.MusicClientController;

/** Prevents vanilla background music from playing underneath an active Magic layer. */
@Mixin(MusicManager.class)
public abstract class MusicManagerMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void magic$suppressVanillaMusic(CallbackInfo callbackInfo) {
        if (MusicClientController.INSTANCE.shouldSuppressVanillaMusic()) {
            ((MusicManager) (Object) this).stopPlaying();
            callbackInfo.cancel();
        }
    }
}
