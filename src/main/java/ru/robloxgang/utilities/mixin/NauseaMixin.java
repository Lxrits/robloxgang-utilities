package ru.robloxgang.utilities.mixin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.*;
import net.minecraft.core.Holder;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.robloxgang.utilities.MemeEffects;
/** Inject the visual blend only; do not add/remove or replace any server potion effect. */
@Mixin(LivingEntity.class)
public abstract class NauseaMixin {
 @Inject(method="getEffectBlendFactor",at=@At("RETURN"),cancellable=true)
 private void robloxgang$nausea(Holder<MobEffect> effect,float delta,CallbackInfoReturnable<Float> ci){
  if(effect.equals(MobEffects.NAUSEA) && (Object)this==Minecraft.getInstance().player)ci.setReturnValue(Math.max(ci.getReturnValue(),MemeEffects.nausea()));
 }
}
