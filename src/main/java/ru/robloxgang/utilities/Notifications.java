package ru.robloxgang.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import java.util.*;

public final class Notifications {
    private record Toast(String title, String text, int color, boolean reminder, long created,Config preview) {}
    private static final Deque<Toast> TOASTS = new ArrayDeque<>();
    public static void show(String title, String text, int color, boolean reminder) {
        show(title,text,color,reminder,null);
    }
    public static void show(String title,String text,int color,boolean reminder,Config preview) {
        if (TOASTS.size() >= 3) TOASTS.removeFirst();
        TOASTS.addLast(new Toast(title, text, color, reminder, System.nanoTime(),preview==null?null:preview.copy()));
    }
    public static void render(GuiGraphics g) {
        long now = System.nanoTime();
        TOASTS.removeIf(t -> (now-t.created)/1e9 > (t.preview==null?RobloxGangUtilities.config:t.preview).duration + .35);
        int index = 0;
        for (Toast t : TOASTS) {
            Config c=t.preview==null?RobloxGangUtilities.config:t.preview;
            double age = (now-t.created)/1e9;
            double entrance = Math.min(1, age/.3), exit = Math.max(0, Math.min(1, (age-c.duration)/.35));
            double progress = c.reducedMotion ? 1 : (1-Math.pow(1-entrance,3))*(1-exit*exit);
            int w = 272, h = t.reminder ? 76 : 58;
            float s = Math.min(c.scale,Math.min((g.guiWidth()-2f*c.margin)/w,(g.guiHeight()-2f*c.margin)/(84f*Math.max(1,TOASTS.size()))));
            int x = Math.round(g.guiWidth()/s) - w - c.margin + (int)((1-progress)*(w+c.margin));
            int y = Math.round(c.margin/s) + index * 84;
            g.pose().pushMatrix(); g.pose().scale(s,s);
            card(g,x,y,w,h,t.title,t.text,t.color);
            if (t.reminder) {
                String key = !c.quickChat?"Быстрая отправка отключена":RobloxGangUtilities.chatKey.isUnbound() ? "Назначьте клавишу в управлении" : "[" + RobloxGangUtilities.chatKey.getTranslatedKeyMessage().getString() + "]  отправить 52";
                g.drawString(Minecraft.getInstance().font,key,x+14,y+48,0xFFCBD5E1,false);
            }
            int bar = (int)((w-16)*Math.max(0,1-age/c.duration));
            g.fill(x+8,y+h-5,x+8+bar,y+h-3,0xFF000000|t.color);
            g.pose().popMatrix(); index++;
        }
    }
    public static void card(GuiGraphics g,int x,int y,int w,int h,String title,String text,int color) {
        g.fill(x+3,y+4,x+w+3,y+h+4,0x44000000);
        g.fill(x+4,y,x+w-4,y+h,0xF21B2030);
        g.fill(x,y+4,x+w,y+h-4,0xF21B2030);
        g.fill(x+1,y+9,x+4,y+h-9,0xFF000000|color);
        g.drawString(Minecraft.getInstance().font,title,x+14,y+12,0xFF000000|color,false);
        g.drawString(Minecraft.getInstance().font,text,x+14,y+29,0xFFF1F5F9,false);
    }
}
