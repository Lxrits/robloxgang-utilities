package ru.robloxgang.utilities.mixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import ru.robloxgang.utilities.IntroVideo;
import ru.robloxgang.utilities.IntroTransition;
@Mixin(LoadingOverlay.class)
public abstract class LoadingVideoMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private long fadeOutStart;
    @Unique private final IntroTransition robloxgang$transition=new IntroTransition();
    // Vanilla tick retains resource exception handling, onFinish and menu init.
    // Once that completes, bypass only the red/logo rendering and its fade timer.
    @Inject(method="render",at=@At("HEAD"),cancellable=true)
    private void robloxgang$crossfade(GuiGraphics g,int x,int y,float delta,CallbackInfo ci){
        if(!IntroVideo.enabled(this) || fadeOutStart<0 || minecraft.screen==null)return;
        if(!robloxgang$transition.started() && minecraft.screen instanceof TitleScreen){
            ((TitleScreenAccess)minecraft.screen).robloxgang$setFading(false);
            ((ScreenAccess)minecraft.screen).robloxgang$fadeWidgets(1);
        }
        float opacity=robloxgang$transition.opacity(System.nanoTime(),true);
        minecraft.screen.renderWithTooltipAndSubtitles(g,x,y,delta);
        g.nextStratum();
        if(opacity>0)IntroVideo.render(g,this,opacity);
        else {minecraft.setOverlay(null);IntroVideo.finish();}
        ci.cancel();
    }
    @Inject(method="render",at=@At("TAIL"))
    private void robloxgang$video(GuiGraphics g,int x,int y,float delta,CallbackInfo ci){IntroVideo.render(g,this);}
    @Inject(method="isReadyToFadeOut",at=@At("HEAD"),cancellable=true)
    private void robloxgang$wait(CallbackInfoReturnable<Boolean> ci){if(IntroVideo.holding(this))ci.setReturnValue(false);}
}
