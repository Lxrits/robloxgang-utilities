package ru.robloxgang.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.*;
import java.util.regex.*;

public final class ChatNames {
    private static final Pattern SENDER=Pattern.compile("^(?:\\[[^\\]\\r\\n]{1,32}\\]\\s*){0,3}(?:<([A-Za-z0-9_]{1,16})>|([A-Za-z0-9_]{1,16})\\s*[:»›])(?:\\s|$)");
    public static Component system(Component message) {
        Matcher match=SENDER.matcher(message.getString());
        if (!match.find()) return message;
        String name=match.group(1)!=null?match.group(1):match.group(2);
        PlayerInfo info=online(name);
        return info==null?message:NameColors.apply(message,info.getProfile().id(),info.getProfile().name(),NameColors.Place.CHAT);
    }
    public static ChatType.Bound disguised(ChatType.Bound bound) {
        if (RobloxGangUtilities.config.serverColorPriority && bound.chatType().value().chat().style().getColor()!=null) return bound;
        PlayerInfo info=online(bound.name().getString());
        if(info==null)return bound;
        Component colored=NameColors.apply(bound.name(),info.getProfile().id(),info.getProfile().name(),NameColors.Place.CHAT);
        return colored==bound.name()?bound:new ChatType.Bound(bound.chatType(),colored,bound.targetName());
    }
    private static PlayerInfo online(String name) {
        var connection=Minecraft.getInstance().getConnection();
        if(connection==null)return null;
        for(PlayerInfo info:connection.getOnlinePlayers())if(info.getProfile().name().equalsIgnoreCase(name))return info;
        return null;
    }
}
