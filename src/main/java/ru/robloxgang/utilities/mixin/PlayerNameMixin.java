package ru.robloxgang.utilities.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.robloxgang.utilities.NameColors;

@Mixin(Player.class)
public abstract class PlayerNameMixin {
    @Inject(method="getDisplayName",at=@At("RETURN"),cancellable=true)
    private void robloxgang$name(CallbackInfoReturnable<Component> cir) {
        Player self=(Player)(Object)this;
        if (!self.level().isClientSide()) return;
        cir.setReturnValue(NameColors.apply(cir.getReturnValue(),self.getUUID(),self.getGameProfile().name(),NameColors.Place.NAMETAG));
    }
}
