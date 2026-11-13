package ru.robloxgang.utilities.mixin;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import ru.robloxgang.utilities.IntroVideo;
@Mixin(LoadingOverlay.class)
public abstract class LoadingVideoMixin {
    @Inject(method="render",at=@At("TAIL"))
    private void robloxgang$video(GuiGraphics g,int x,int y,float delta,CallbackInfo ci){IntroVideo.render(g,this);}
    @Inject(method="isReadyToFadeOut",at=@At("HEAD"),cancellable=true)
    private void robloxgang$wait(CallbackInfoReturnable<Boolean> ci){if(IntroVideo.holding(this))ci.setReturnValue(false);}
}
