package ru.robloxgang.utilities.mixin;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(Screen.class)
public interface ScreenAccess {
    @Invoker("fadeWidgets") void robloxgang$fadeWidgets(float opacity);
}
