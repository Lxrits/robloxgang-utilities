package ru.robloxgang.utilities;

import net.minecraft.network.chat.*;
import java.util.*;

public final class NameColors {
    public enum Place { TAB, CHAT, NAMETAG }
    public static Component apply(Component original, UUID uuid, String name, Place place) {
        if(RobloxGangUtilities.config==null || RobloxGangUtilities.names==null)return original;
        return apply(original,uuid,name,place,RobloxGangUtilities.config,RobloxGangUtilities.names.names());
    }
    static Component apply(Component original,UUID uuid,String name,Place place,Config c,Map<String,String> names) {
        if (original == null || c == null || !c.colors || name == null) return original;
        if ((place == Place.TAB && !c.tab) || (place == Place.CHAT && !c.chat) || (place == Place.NAMETAG && !c.nametag)) return original;
        Gang.Member member = Gang.find(uuid, name, c.fallback, names);
        if (member == null || c.disabledMembers.contains(member.uuid().toString())) return original;
        String text = original.getString();
        int start = nameOffset(text, name);
        if (start < 0) return original;
        int end = start + name.length();
        // Resolve inherited styles before checking; even explicit white belongs to the server.
        List<Run> runs = new ArrayList<>();
        original.visit((style, str) -> { runs.add(new Run(style, str)); return Optional.empty(); }, Style.EMPTY);
        int pos = 0;
        for (Run run : runs) {
            if (c.serverColorPriority && pos < end && pos + run.text.length() > start && run.style.getColor() != null) return original;
            pos += run.text.length();
        }
        int color = c.memberColors.getOrDefault(member.uuid().toString(), member.color());
        MutableComponent result = Component.empty(); pos = 0;
        for (Run run : runs) {
            int a = Math.max(0, start - pos), b = Math.min(run.text.length(), end - pos);
            if (a < b) {
                if (a > 0) result.append(Component.literal(run.text.substring(0,a)).setStyle(run.style));
                result.append(Component.literal(run.text.substring(a,b)).setStyle(run.style.withColor(color)));
                if (b < run.text.length()) result.append(Component.literal(run.text.substring(b)).setStyle(run.style));
            } else result.append(Component.literal(run.text).setStyle(run.style));
            pos += run.text.length();
        }
        return result;
    }
    private record Run(Style style, String text) {}
    static int nameOffset(String text, String name) {
        for (int i = 0; i + name.length() <= text.length(); i++) {
            if (text.regionMatches(true, i, name, 0, name.length())
                && (i == 0 || !usernameChar(text.charAt(i-1)))
                && (i+name.length() == text.length() || !usernameChar(text.charAt(i+name.length())))) return i;
        }
        return -1;
    }
    private static boolean usernameChar(char c) { return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_'; }
}
