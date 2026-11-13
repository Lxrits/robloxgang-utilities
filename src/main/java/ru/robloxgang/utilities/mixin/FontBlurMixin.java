package ru.robloxgang.utilities.mixin;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.robloxgang.utilities.Censor42;
@Mixin(Font.class)
public abstract class FontBlurMixin {
    @ModifyVariable(method="prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZI)Lnet/minecraft/client/gui/Font$PreparedText;",at=@At("HEAD"),argsOnly=true)
    private FormattedCharSequence robloxgang$blur(FormattedCharSequence text){return Censor42.apply(text);}
    @Inject(method="prepareText(Ljava/lang/String;FFIZI)Lnet/minecraft/client/gui/Font$PreparedText;",at=@At("HEAD"),cancellable=true)
    private void robloxgang$string(String text,float x,float y,int color,boolean shadow,int background,CallbackInfoReturnable<Font.PreparedText> ci){
        if(ru.robloxgang.utilities.RobloxGangUtilities.config!=null && ru.robloxgang.utilities.RobloxGangUtilities.config.blur42 && !Censor42.ranges(text).isEmpty())
            ci.setReturnValue(((Font)(Object)this).prepareText(Censor42.apply(Component.literal(text).getVisualOrderText()),x,y,color,shadow,background));
    }
    @ModifyVariable(method="split(Lnet/minecraft/network/chat/FormattedText;I)Ljava/util/List;",at=@At("HEAD"),argsOnly=true)
    private net.minecraft.network.chat.FormattedText robloxgang$wrap(net.minecraft.network.chat.FormattedText text){return Censor42.beforeWrap(text);}
}
