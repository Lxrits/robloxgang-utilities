package ru.robloxgang.utilities.mixin;

import net.minecraft.client.ToggleKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ToggleKeyMapping.class)
public interface KeyMappingAccess {
    @Invoker("reset") void robloxgang$reset();
}
