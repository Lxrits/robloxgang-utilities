package ru.robloxgang.utilities;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import ru.robloxgang.utilities.mixin.KeyMappingAccess;
import java.lang.reflect.*;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;

public final class SmokeClient implements ClientModInitializer {
    private int ticks;
    private boolean hidden;
    @Override public void onInitializeClient() { ClientTickEvents.END_CLIENT_TICK.register(this::tick); }
    private void tick(Minecraft client) {
        if(Boolean.getBoolean("robloxgang.updateCapture")){
            try{Class.forName("ru.robloxgang.utilities.UpdateCapture").getDeclaredMethod("tick",Minecraft.class).invoke(null,client);}
            catch(Exception e){throw new RuntimeException(e);}return;
        }
        if(Boolean.getBoolean("robloxgang.serverCapture")){
            try{Class.forName("ru.robloxgang.utilities.ServerCapture").getDeclaredMethod("tick",Minecraft.class).invoke(null,client);}
            catch(Exception e){throw new RuntimeException(e);}return;
        }
        if(!hidden && client.getWindow()!=null) {
            try {
                client.options.getClass().getField("pauseOnLostFocus").setBoolean(client.options,false);
                long handle=(long)client.getWindow().getClass().getMethod("handle").invoke(client.getWindow());
                try{Class.forName("org.lwjgl.glfw.GLFW").getMethod("glfwHideWindow",long.class).invoke(null,handle);}
                catch(ClassNotFoundException e){Class.forName("org.lwjgl.sdl.SDLVideo").getMethod("SDL_HideWindow",long.class).invoke(null,handle);}
            }catch(Exception e){LoggerFactory.getLogger("RobloxGang Smoke").warn("Unable to hide test window",e);}
            hidden=true;
        }
        if(!client.isGameLoadFinished())return;
        ticks++;
        try {
            if(ticks==30) {
                if(RobloxGangUtilities.sneakKey.isUnbound() || RobloxGangUtilities.chatKey.isUnbound())throw new AssertionError("Default binds must be assigned");
                boolean previous=client.options.toggleCrouch().get();
                client.options.toggleCrouch().set(true);
                client.options.keyShift.setDown(true);
                if(!client.options.keyShift.isDown())throw new AssertionError("Toggle crouch did not latch");
                ((KeyMappingAccess)client.options.keyShift).robloxgang$reset();
                if(client.options.keyShift.isDown())throw new AssertionError("Toggle crouch stuck after reset");
                client.options.toggleCrouch().set(previous);
                Class.forName("net.minecraft.client.multiplayer.chat.ChatListener");
                Class.forName("net.minecraft.client.gui.components.PlayerTabOverlay");
                client.setScreen(new SettingsScreen(null));
            }
            if(ticks==35) { RobloxGangUtilities.preview52(); RobloxGangUtilities.chime(); }
            if(ticks==40) client.setScreen(new net.minecraft.client.gui.screens.options.controls.KeyBindsScreen(new SettingsScreen(null),client.options));
            if(ticks==45) client.setScreen(new SettingsScreen(null,2));
            if(ticks==50)client.setScreen(new FeatureDemoScreen(null,0));
            if(ticks==70)client.setScreen(new FeatureDemoScreen(null,1));
            if(ticks==90)client.setScreen(new FeatureDemoScreen(null,2));
            if(Boolean.getBoolean("robloxgang.capture")) {
                if(ticks==49)capture(client,"settings");
                if(ticks==65)capture(client,"sneak");
                if(ticks==85)capture(client,"reminder");
                if(ticks==105)capture(client,"gang");
            }
            if(ticks==110)client.setScreen(new SettingsScreen(null,4));
            if(ticks==115)client.setScreen(new FeatureDemoScreen(null,5));
            if(ticks==120) {
                LoggerFactory.getLogger("RobloxGang Smoke").info("RG_SMOKE_OK: client, mixins, settings, demos and toggle reset verified");
                client.stop();
            }
        } catch(Throwable e) { throw new AssertionError("RobloxGang client verification failed",e); }
    }
    private void capture(Minecraft client,String name)throws Exception {
        // Reflection keeps this development helper usable across the screenshot API rename.
        Object target=client.getClass().getMethod("getMainRenderTarget").invoke(client);
        for(Method m:Class.forName("net.minecraft.client.Screenshot").getMethods()) {
            if(m.getName().equals("grab") && m.getParameterCount()==5) {
                m.invoke(null,client.gameDirectory,"demo-"+name+".png",target,1,(Consumer<Component>)message->LoggerFactory.getLogger("RobloxGang Smoke").info(message.getString()));
                return;
            }
        }
        throw new IllegalStateException("Screenshot API not found");
    }
}
