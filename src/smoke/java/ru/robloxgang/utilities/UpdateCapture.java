package ru.robloxgang.utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import java.lang.reflect.*;
import java.util.function.Consumer;
final class UpdateCapture {
 private static int ticks,start,introTicks;private static boolean connected;
 static void tick(Minecraft mc)throws Exception{
  if(mc.getOverlay()!=null && ++introTicks==50)capture(mc,"intro");
  if(!mc.isGameLoadFinished() || mc.getOverlay()!=null)return;
  if(!connected){if(++start<20)return;
   mc.options.pauseOnLostFocus=false;mc.options.guiScale().set(2);mc.options.renderDistance().set(6);mc.options.bobView().set(false);mc.options.gamma().set(1.0);mc.options.tutorialStep=net.minecraft.client.tutorial.TutorialSteps.NONE;
   ConnectScreen.startConnecting(new TitleScreen(),mc,ServerAddress.parseString("127.0.0.1:25575"),new ServerData("Update test","127.0.0.1:25575",ServerData.Type.OTHER),false,null);connected=true;return;
  }
  if(mc.player==null || mc.level==null)return;
  int t=++ticks;if(t<210 || t>220)mc.setScreen(null);mc.player.setYRot(0);mc.player.setXRot(10);
  if(t==20){mc.player.connection.sendCommand("tp @s 0 152 0");mc.player.connection.sendCommand("fill -8 150 -8 8 150 8 minecraft:smooth_stone");mc.player.connection.sendCommand("fill -1 152 5 1 154 5 minecraft:barrier");mc.player.connection.sendCommand("time set day");mc.player.connection.sendCommand("weather clear");}
  if(t==60){mc.getToastManager().clear();mc.gui.getChat().clearMessages(true);}
  if(t==180)capture(mc,"barriers");
  if(t==190){mc.player.connection.sendCommand("say Проверка: 42 / сорок два / FORTY TWO / Player42");}
  if(t==210)mc.setScreen(new ChatScreen("",false));
  if(t==215)capture(mc,"blur42");
  if(t==230){Notifications.show("На часы смотреть нельзя!","Сейчас 14:42",0xEF4444,false);}
  if(t==245)capture(mc,"reminder42");
  if(t==370){Method press=MemeEffects.class.getDeclaredMethod("press",int.class,long.class,Minecraft.class);press.setAccessible(true);long now=System.nanoTime()/1000000;for(int i=0;i<6;i++)press.invoke(null,i%2==0?6:7,now-500+i*100,mc);}
  if(t==395)capture(mc,"overdose");
  if(t==550){Method play=MemeEffects.class.getDeclaredMethod("play",String.class,float.class);play.setAccessible(true);play.invoke(null,"anthem",.3f);Notifications.show("Гимн России","Пять быстрых нажатий Z",0xFFFFFF,false);}
  if(t==575)capture(mc,"anthem");
  if(t==590){mc.setScreen(new SettingsScreen(null,4));}
  if(t==595)capture(mc,"memes");
  if(t==610){org.slf4j.LoggerFactory.getLogger("Update Capture").info("RG_UPDATE_CAPTURE_OK");mc.stop();}
 }
 private static void capture(Minecraft mc,String name)throws Exception{
  Object target=mc.getClass().getMethod("getMainRenderTarget").invoke(mc);
  for(Method m:Class.forName("net.minecraft.client.Screenshot").getMethods())if(m.getName().equals("grab")&&m.getParameterCount()==5){m.invoke(null,mc.gameDirectory,"update-"+name+".png",target,1,(Consumer<Component>)msg->{});return;}
 }
}
