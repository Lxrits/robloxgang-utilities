package ru.robloxgang.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.KeyMapping;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import java.nio.file.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import org.slf4j.LoggerFactory;

/** Development-only captures from a real connection to the local vanilla server. */
final class ServerCapture {
    private static int start, ticks;
    private static boolean connected;
    private static boolean hidden;
    private static double targetY;
    static void tick(Minecraft client) {
        if(!hidden && client.getWindow()!=null) {
            client.options.pauseOnLostFocus=false;
            org.lwjgl.glfw.GLFW.glfwHideWindow(client.getWindow().handle());
            hidden=true;
        }
        if(!client.isGameLoadFinished() || client.getOverlay()!=null)return;
        if(!connected) {
            if(++start<30)return;
            client.options.guiScale().set(2);
            client.options.pauseOnLostFocus=false;
            client.options.tutorialStep=net.minecraft.client.tutorial.TutorialSteps.NONE;
            client.options.renderDistance().set(6);
            client.options.bobView().set(false);
            client.options.gamma().set(1.0);
            RobloxGangUtilities.config.serverColorPriority=false;
            RobloxGangUtilities.config.duration=6;
            ConnectScreen.startConnecting(new TitleScreen(),client,ServerAddress.parseString("127.0.0.1:25575"),new ServerData("RobloxGang local demo","127.0.0.1:25575",ServerData.Type.OTHER),false,null);
            connected=true;return;
        }
        if(client.player==null || client.level==null || client.getConnection()==null)return;
        if(ticks==0 && client.level.getDayTime()%24000>11500)return;
        try {
            int t=++ticks;
            if(t==1) {
                targetY=126;
                client.player.getAbilities().flying=true;
                client.player.onUpdateAbilities();
                client.getToastManager().clear();
            }
            if(client.screen instanceof PauseScreen)client.setScreen(null);
            client.player.setYRot(125f);client.player.setXRot(24f);
            if(t<100 && Math.abs(client.player.getY()-targetY)>.25)client.player.setPos(client.player.getX(),client.player.getY()+Math.copySign(.25,targetY-client.player.getY()),client.player.getZ());
            if(t==140) {
                client.setScreen(null);
                Set<String> online=new HashSet<>();client.getConnection().getOnlinePlayers().forEach(p->online.add(p.getProfile().name()));
                for(Gang.Member m:Gang.MEMBERS)if(!online.contains(m.initialName()))throw new AssertionError("Bandit absent from actual tab: "+m.initialName());
                LoggerFactory.getLogger("RG Server Capture").info("Five bandits present in server tab: {}",online);
                client.gui.getChat().clearMessages(true);
                client.getToastManager().clear();
            }
            if(t>=160 && t<=190)client.options.keyPlayerList.setDown(true);
            if(t==180)capture(client,"gang");
            if(t==191)client.options.keyPlayerList.setDown(false);
            if(t==200)Files.writeString(Path.of(System.getProperty("robloxgang.botCommands")),"[\"52\",\"RobloxGang!\"]");
            if(t==215)client.player.connection.sendChat("52");
            if(t==240)client.setScreen(new ChatScreen("",false));
            if(t==260)capture(client,"chat");
            if(t==275)client.setScreen(null);
            if(t==310) {
                RobloxGangUtilities.sneakKey.setDown(true);
                KeyMapping.click(KeyBindingHelper.getBoundKeyOf(RobloxGangUtilities.sneakKey));
            }
            if(t==312)RobloxGangUtilities.sneakKey.setDown(false);
            if(t==335)capture(client,"sneak");
            if(t==460){RobloxGangUtilities.preview52();RobloxGangUtilities.chime();}
            if(t==485)capture(client,"reminder");
            if(t==510){RobloxGangUtilities.chatKey.setDown(true);KeyMapping.click(KeyBindingHelper.getBoundKeyOf(RobloxGangUtilities.chatKey));}
            if(t==512)RobloxGangUtilities.chatKey.setDown(false);
            if(t==550) {
                LoggerFactory.getLogger("RG Server Capture").info("RG_SERVER_CAPTURE_OK: real world, five server players, tab, chat, sneak bind and reminder");
                client.stop();
            }
        } catch(Exception e) {throw new RuntimeException(e);}
    }
    private static void capture(Minecraft client,String name)throws Exception {
        Object target=client.getClass().getMethod("getMainRenderTarget").invoke(client);
        for(Method m:Class.forName("net.minecraft.client.Screenshot").getMethods())if(m.getName().equals("grab")&&m.getParameterCount()==5){
            m.invoke(null,client.gameDirectory,"server-"+name+".png",target,1,(Consumer<Component>)msg->LoggerFactory.getLogger("RG Server Capture").info(msg.getString()));return;
        }
        throw new IllegalStateException("Screenshot API not found");
    }
}
