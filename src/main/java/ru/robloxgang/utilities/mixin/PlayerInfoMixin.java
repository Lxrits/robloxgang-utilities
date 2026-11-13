package ru.robloxgang.utilities.mixin;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.robloxgang.utilities.NameColors;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerInfoMixin {
    @Inject(method="getNameForDisplay",at=@At("RETURN"),cancellable=true)
    private void robloxgang$tab(PlayerInfo info,CallbackInfoReturnable<Component> cir) {
        cir.setReturnValue(NameColors.apply(cir.getReturnValue(),info.getProfile().id(),info.getProfile().name(),NameColors.Place.TAB));
    }
}
