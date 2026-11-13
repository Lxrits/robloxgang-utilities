package ru.robloxgang.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

final class UtilityButton extends Button {
    UtilityButton(String label,int x,int y,int width,int height,Runnable action) {
        super(x,y,width,height,Component.literal(label),button->action.run(),DEFAULT_NARRATION);
    }
    @Override protected void renderWidget(GuiGraphics g,int mx,int my,float delta) {
        boolean selected=isHoveredOrFocused();
        int bg=!active?0xFF302644:selected?0xFF313C56:0xFF1B2333;
        int edge=!active?0xFFA78BFA:selected?0xFF7C8FBD:0xFF303C53;
        int x=getX(),y=getY(),w=getWidth(),h=getHeight();
        g.fill(x+2,y,x+w-2,y+h,bg);g.fill(x,y+2,x+w,y+h-2,bg);
        g.fill(x+3,y+h-1,x+w-3,y+h,edge);
        var font=Minecraft.getInstance().font;
        String label=font.plainSubstrByWidth(getMessage().getString(),w-10);
        g.drawCenteredString(font,label,x+w/2,y+(h-8)/2,active?0xFFE2E8F0:0xFFC4B5FD);
    }
}
