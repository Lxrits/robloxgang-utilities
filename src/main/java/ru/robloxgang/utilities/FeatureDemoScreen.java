package ru.robloxgang.utilities;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Gallery of unedited screenshots from the local multiplayer demonstration. */
final class FeatureDemoScreen extends Screen {
    private final Screen parent;
    private final int feature;
    private int photo;
    FeatureDemoScreen(Screen parent,int feature) {
        super(Component.literal("Скриншоты из игры"));this.parent=parent;this.feature=feature;this.photo=feature;
    }
    @Override protected void init() {
        if(feature==2) {
            addRenderableWidget(new UtilityButton("Таб: все 5 участников",width/2-148,36,174,20,()->{photo=2;rebuildWidgets();}));
            addRenderableWidget(new UtilityButton("Чат",width/2+32,36,116,20,()->{photo=4;rebuildWidgets();}));
        }
        addRenderableWidget(new UtilityButton("Назад",width/2-80,height-29,160,20,this::onClose));
    }
    @Override public void render(GuiGraphics g,int mx,int my,float dt) {
        g.fill(0,0,width,height,0xFF0D111C);
        g.drawString(font,"ROBLOXGANG / СКРИНШОТЫ",16,15,0xFFA78BFA,false);
        int top=feature==2?65:40;
        int maxHeight=Math.max(1,height-top-48);
        int photoW=Math.min(Math.min(width-24,900),maxHeight*1280/720);
        int photoH=photoW*720/1280;
        if(!FeaturePhotos.draw(g,photo,(width-photoW)/2,top,photoW,photoH))
            g.drawCenteredString(font,"Скриншот недоступен",width/2,top+20,0xFF94A3B8);
        g.drawCenteredString(font,"Локальный сервер • тестовые профили участников",width/2,height-43,0xFF94A3B8);
        super.render(g,mx,my,dt);
    }
    @Override public void onClose(){minecraft.setScreen(parent);}
}
