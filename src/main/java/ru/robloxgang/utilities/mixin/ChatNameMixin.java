package ru.robloxgang.utilities.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import ru.robloxgang.utilities.NameColors;
import ru.robloxgang.utilities.ChatNames;

@Mixin(ChatListener.class)
public abstract class ChatNameMixin {
    @ModifyVariable(method="handleSystemMessage",at=@At("HEAD"),argsOnly=true)
    private Component robloxgang$system(Component message) { return ChatNames.system(message); }
    @ModifyVariable(method="handleDisguisedChatMessage",at=@At("HEAD"),argsOnly=true)
    private ChatType.Bound robloxgang$disguised(ChatType.Bound bound) { return ChatNames.disguised(bound); }
    @ModifyVariable(method="handlePlayerChatMessage",at=@At("HEAD"),argsOnly=true)
    private ChatType.Bound robloxgang$sender(ChatType.Bound bound,PlayerChatMessage message,GameProfile profile,ChatType.Bound original) {
        if (ru.robloxgang.utilities.RobloxGangUtilities.config.serverColorPriority && bound.chatType().value().chat().style().getColor()!=null) return bound;
        Component colored=NameColors.apply(bound.name(),profile.id(),profile.name(),NameColors.Place.CHAT);
        return colored==bound.name()?bound:new ChatType.Bound(bound.chatType(),colored,bound.targetName());
    }
}
