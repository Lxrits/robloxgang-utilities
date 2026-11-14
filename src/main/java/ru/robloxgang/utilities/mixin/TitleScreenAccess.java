package ru.robloxgang.utilities.mixin;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(TitleScreen.class)
public interface TitleScreenAccess {
    @Accessor("fading") void robloxgang$setFading(boolean value);
}
