package ru.robloxgang.utilities;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.function.*;

public final class SettingsScreen extends Screen {
    private final Screen parent;
    private Config draft;
    private int page;
    private int left, top, controlWidth, rowStep;
    private boolean wide;
    private static final String[] TABS = {"Присед", "52", "RobloxGang", "Вид"};
    public SettingsScreen(Screen parent) {
        super(Component.literal("RobloxGang Utilities")); this.parent = parent;
        draft = RobloxGangUtilities.config.copy();
    }
    SettingsScreen(Screen parent,int page) { this(parent);this.page=page; }
    @Override protected void init() {
        wide = width >= 640;
        int area = Math.min(width-24, 700);
        left = (width-area)/2; controlWidth = wide ? area-304 : area;
        top = 83; rowStep = Math.min(24, Math.max(16,(height-132)/6));
        button("Демо",width-72,16,60,20,()->minecraft.setScreen(new FeatureDemoScreen(this,page)));
        for (int i=0;i<TABS.length;i++) {
            final int tab=i;
            int tw = (area-9)/4;
            Button b = button(TABS[i],left+i*(tw+3),51,tw,20,() -> { page=tab; rebuildWidgets(); });
            b.active = page != i;
        }
        switch (page) {
            case 0 -> sneak(); case 1 -> reminder(); case 2 -> gang(); default -> appearance();
        }
        int y = height-28;
        button("Сохранить",left,y,(area-12)/3,20,() -> {
            if(draft.save()){RobloxGangUtilities.config=draft;onClose();}
            else Notifications.show("Ошибка сохранения","Проверьте место на диске и права",0xFF5555,false);
        });
        button("Отмена",left+(area-12)/3+6,y,(area-12)/3,20,this::onClose);
        button("Сбросить",left+2*((area-12)/3+6),y,(area-12)/3,20,() -> { draft=new Config(); rebuildWidgets(); });
    }
    private Button button(String text,int x,int y,int w,int h,Runnable action) {
        return addRenderableWidget(new UtilityButton(text,x,y,w,h,action));
    }
    private void row(String text,BooleanSupplier get,Consumer<Boolean> set) {
        button(text+": "+(get.getAsBoolean()?"Вкл":"Выкл"),left,top,controlWidth,rowStep-3,() -> {set.accept(!get.getAsBoolean());rebuildWidgets();}); top+=rowStep;
    }
    private void action(String title,Runnable r) { button(title,left,top,controlWidth,rowStep-3,r); top+=rowStep; }
    private void sneak() {
        row("Переключение приседа",()->draft.sneak,v->draft.sneak=v);
        row("Уведомление о режиме",()->draft.sneakToast,v->draft.sneakToast=v);
        action("Назначить клавиши…",this::keys);
        action("Показать уведомление",()->Notifications.show("Режим приседа","Переключение • Toggle",0xA78BFA,false,draft));
    }
    private void reminder() {
        row("Напоминание в :52",()->draft.reminder,v->draft.reminder=v);
        row("Быстрая отправка 52",()->draft.quickChat,v->draft.quickChat=v);
        row("Звук напоминания",()->draft.sound,v->draft.sound=v);
        action("Громкость: "+Math.round(draft.volume*100)+"%",()->{draft.volume=draft.volume>=.99f?0:Math.min(1,draft.volume+.1f);rebuildWidgets();});
        action("Назначить клавишу «52»…",this::keys);
        action("Тест напоминания и звука",()->{
            RobloxGangUtilities.preview52(draft);
            RobloxGangUtilities.previewChime(draft);
        });
    }
    private void keys() { minecraft.setScreen(new KeyBindsScreen(this,minecraft.options)); }
    private void gang() {
        row("Цвета участников",()->draft.colors,v->draft.colors=v);
        action("Приоритет цвета: "+(draft.serverColorPriority?"Сервер":"Клиент"),()->{draft.serverColorPriority=!draft.serverColorPriority;rebuildWidgets();});
        // Compact controls keep all five members accessible even at the minimum GUI height.
        int half=(controlWidth-4)/2;
        button("Таб: "+yes(draft.tab),left,top,half,rowStep-3,()->{draft.tab=!draft.tab;rebuildWidgets();});
        button("Чат: "+yes(draft.chat),left+half+4,top,half,rowStep-3,()->{draft.chat=!draft.chat;rebuildWidgets();});top+=rowStep;
        button("Неймтэг: "+yes(draft.nametag),left,top,half,rowStep-3,()->{draft.nametag=!draft.nametag;rebuildWidgets();});
        button("По нику: "+yes(draft.fallback),left+half+4,top,half,rowStep-3,()->{draft.fallback=!draft.fallback;rebuildWidgets();});top+=rowStep;
        action("Участники и цвета…",()->minecraft.setScreen(new MembersScreen(this,draft)));
        action("Обновить ники Mojang",()->RobloxGangUtilities.names.refresh(true));
    }
    private static String yes(boolean v) { return v?"Вкл":"Выкл"; }
    private void appearance() {
        row("Уменьшить анимацию",()->draft.reducedMotion,v->draft.reducedMotion=v);
        action("Длительность: "+draft.duration+" сек.",()->{draft.duration=draft.duration>=15?2:draft.duration+1;rebuildWidgets();});
        action("Масштаб: "+Math.round(draft.scale*100)+"%",()->{draft.scale=draft.scale>=1.49f?.75f:draft.scale+.25f;rebuildWidgets();});
        action("Отступ: "+draft.margin+" px",()->{draft.margin=draft.margin>=32?4:draft.margin+4;rebuildWidgets();});
        action("Предпросмотр",()->RobloxGangUtilities.preview52(draft));
    }
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta) {
        g.fill(0,0,width,height,0xFF0D111C);
        g.fill(0,0,width,3,0xFFA78BFA);
        g.drawString(font,"ROBLOXGANG",left,17,0xFFA78BFA,false);
        g.drawString(font,"Utilities",left+82,17,0xFFF1F5F9,false);
        g.drawString(font,"Твои настройки. Твоя банда.",left,33,0xFF94A3B8,false);
        if (wide) preview(g,left+controlWidth+16,83);
        int infoY=Math.min(top+9,height-51);
        if (page==2) {
            long ts=RobloxGangUtilities.names.lastUpdate();
            String update=ts==0?"Кэш ещё не обновлялся": "Кэш: "+DateTimeFormatter.ofPattern("dd.MM HH:mm").format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()));
            g.drawString(font,font.plainSubstrByWidth(update,controlWidth),left,infoY,0xFF94A3B8,false);
            String status=RobloxGangUtilities.names.busy()?"Обновление…":RobloxGangUtilities.names.status;
            g.drawString(font,font.plainSubstrByWidth(status,controlWidth),left,infoY+11,0xFF94A3B8,false);
        } else if(page==0) g.drawString(font,"Бинд: "+RobloxGangUtilities.sneakKey.getTranslatedKeyMessage().getString(),left,infoY,0xFF94A3B8,false);
        else if(page==1) g.drawString(font,"Бинд: "+RobloxGangUtilities.chatKey.getTranslatedKeyMessage().getString(),left,infoY,0xFF94A3B8,false);
        super.render(g,mouseX,mouseY,delta);
        Notifications.render(g);
    }
    private void preview(GuiGraphics g,int x,int y) {
        g.drawString(font,"ПРЕДПРОСМОТР",x,y,0xFF64748B,false); y+=20;
        if(FeaturePhotos.draw(g,page,x,y,272,153)) {
            g.drawString(font,page==2?"Все 5 ников в табе. Чат — в «Демо».":"Снимок из мира на локальном сервере.",x,y+162,0xFF94A3B8,false);
            return;
        }
        if(page==0) {
            Notifications.card(g,x,y,272,62,"Режим приседа","Переключение • Toggle",0xA78BFA);
            g.drawString(font,"Меняет штатную настройку Minecraft.",x,y+82,0xFF94A3B8,false);
        } else if(page==1 || page==3) {
            Notifications.card(g,x,y,272,80,"Уже 14:52","Время написать 52!",0xFBBF24);
            g.drawString(font,"["+RobloxGangUtilities.chatKey.getTranslatedKeyMessage().getString()+"]  отправить 52",x+14,y+49,0xFFCBD5E1,false);
            g.drawString(font,"По местному времени компьютера.",x,y+98,0xFF94A3B8,false);
        } else {
            int yy=y;
            for(Gang.Member m:Gang.MEMBERS) {
                String n=RobloxGangUtilities.names.names().getOrDefault(m.uuid().toString(),m.initialName());
                g.fill(x,yy,x+272,yy+22,0xFF1B2030);
                g.drawString(font,n,x+10,yy+7,0xFF000000|draft.memberColors.getOrDefault(m.uuid().toString(),m.color()),false); yy+=25;
            }
            g.drawString(font,"Приоритет: "+(draft.serverColorPriority?"сервер":"клиент"),x,yy+8,0xFF94A3B8,false);
        }
    }
    @Override public void onClose() { minecraft.setScreen(parent); }
}
