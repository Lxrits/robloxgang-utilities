package ru.robloxgang.utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import java.time.ZonedDateTime;

public final class MemeEffects {
    private static KeyMapping six,seven,z;
    private static boolean a,b,c;
    private static final SpamTrigger combo=new SpamTrigger(6,1600,12000), anthem=new SpamTrigger(5,1400,15000);
    private static int lastKey, alternating;
    private static long lastPress,end;
    private static final ReminderClock clock=new ReminderClock(42);
    private static SimpleSoundInstance song,siren;
    public static void init(KeyMapping.Category category){
        six=KeyBindingHelper.registerKeyBinding(new KeyMapping("key.robloxgang_utilities.six",InputConstants.KEY_6,category));
        seven=KeyBindingHelper.registerKeyBinding(new KeyMapping("key.robloxgang_utilities.seven",InputConstants.KEY_7,category));
        z=KeyBindingHelper.registerKeyBinding(new KeyMapping("key.robloxgang_utilities.anthem",InputConstants.KEY_Z,category));
    }
    public static void tick(Minecraft mc){
        Config cfg=RobloxGangUtilities.config;
        long now=System.nanoTime()/1000000;
        boolean allowed=mc.player!=null && mc.level!=null && mc.screen==null && mc.isWindowActive();
        boolean aa=six.isDown(),bb=seven.isDown(),cc=z.isDown();
        // Drain queued clicks so opening a menu cannot defer an activation.
        while(six.consumeClick()){} while(seven.consumeClick()){} while(z.consumeClick()){}
        if(allowed){
            if(cfg.overdose){ if(aa&&!a)press(6,now,mc); if(bb&&!b)press(7,now,mc); }
            if(cfg.anthem && cc&&!c && anthem.press(now) && (song==null || !mc.getSoundManager().isActive(song))){song=play("anthem",cfg.volume);Notifications.show("Гимн России","Пять быстрых нажатий "+z.getTranslatedKeyMessage().getString(),0xFFFFFF,false); }

        }else{combo.clear();anthem.clear();alternating=0;lastKey=0;}
        if(mc.player!=null && mc.level!=null){if(cfg.reminder42){ZonedDateTime time=ZonedDateTime.now();if(clock.poll(time))Notifications.show("На часы смотреть нельзя!","Сейчас "+String.format("%02d:42",time.getHour()),0xEF4444,false);}}
        a=aa;b=bb;c=cc;
        if((!cfg.anthem || mc.player==null) && song!=null){mc.getSoundManager().stop(song);song=null;}
        if(mc.player==null || !cfg.overdose){end=0;if(siren!=null){mc.getSoundManager().stop(siren);siren=null;}}

    }
    private static void press(int key,long now,Minecraft mc){
        if(now-lastPress>1600 || lastKey==key){combo.clear();alternating=0;}
        lastKey=key;lastPress=now;alternating++;
        if(combo.press(now) && alternating>=6){
            end=now+8000;
            mc.gui.setTimes(10,100,20);mc.gui.setTitle(Component.literal("Передоз 67").withStyle(net.minecraft.ChatFormatting.RED));
            siren=play("siren",RobloxGangUtilities.config.volume);
        }
    }
    private static SimpleSoundInstance play(String id,float volume){
        SimpleSoundInstance sound=SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("robloxgang_utilities",id)),1,volume);
        Minecraft.getInstance().getSoundManager().play(sound);return sound;
    }
    public static float nausea(){
        Config cfg=RobloxGangUtilities.config;
        if(cfg==null || !cfg.overdose || cfg.reducedMotion)return 0;
        double remaining=(end-System.nanoTime()/1000000)/1000.0;
        return remaining<=0?0:(float)(.85*Math.max(0,Math.min(1,Math.min(remaining,8-remaining))));
    }
    public static void render(GuiGraphics g){
        if(!RobloxGangUtilities.config.overdose || Minecraft.getInstance().player==null)return;
        double remaining=(end-System.nanoTime()/1000000)/1000.0;
        if(remaining<=0)return;
        float intensity=(float)Math.min(1,Math.min(remaining,8-remaining));
        int alpha=(int)(75*intensity);
        g.fill(0,0,g.guiWidth(),g.guiHeight(),(alpha<<24)|0xB00000);
    }
}
