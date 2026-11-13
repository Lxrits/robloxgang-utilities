package ru.robloxgang.utilities;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.*;

final class MembersScreen extends Screen {
    private final Screen parent;
    private final Config config;
    MembersScreen(Screen parent,Config config) { super(Component.literal("Участники RobloxGang")); this.parent=parent;this.config=config; }
    @Override protected void init() {
        int x=width/2-148, y=48;
        for(Gang.Member m:Gang.MEMBERS) {
            String id=m.uuid().toString();
            String name=RobloxGangUtilities.names.names().getOrDefault(id,m.initialName());
            addRenderableWidget(Button.builder(Component.literal((config.disabledMembers.contains(id)?"○ ":"● ")+name), b->{
                if(!config.disabledMembers.remove(id))config.disabledMembers.add(id);rebuildWidgets();
            }).bounds(x,y,207,20).build());
            EditBox hex=addRenderableWidget(new EditBox(font,x+211,y,85,20,Component.literal("Цвет HEX")));
            hex.setMaxLength(7);hex.setValue(String.format("#%06X",config.memberColors.getOrDefault(id,m.color())));
            hex.setResponder(value->{
                if(value.matches("#[0-9a-fA-F]{6}")){ config.memberColors.put(id,Integer.parseInt(value.substring(1),16));hex.setTextColor(0xE2E8F0); }
                else hex.setTextColor(0xFF5555);
            });y+=26;
        }
        addRenderableWidget(Button.builder(Component.literal("Готово"),b->onClose()).bounds(x,height-29,296,20).build());
    }
    @Override public void render(GuiGraphics g,int mx,int my,float dt) {
        g.fill(0,0,width,height,0xFF0D111C);
        g.drawCenteredString(font,title,width/2,17,0xFFA78BFA);
        g.drawCenteredString(font,"Нажми на ник для вкл/выкл. Цвет: #RRGGBB",width/2,32,0xFF94A3B8);
        super.render(g,mx,my,dt);
    }
    @Override public void onClose(){ minecraft.setScreen(parent); }
}
