package ru.robloxgang.utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import java.lang.reflect.*;
import java.util.function.Consumer;
/** Real-server depth-buffer regression captures: visible, covered and partly covered. */
final class OcclusionCapture {
 private static int start,ticks;private static boolean connected;
 static void tick(Minecraft client)throws Exception{
  if(!client.isGameLoadFinished() || client.getOverlay()!=null)return;
  if(!connected){if(++start<10)return;
   client.options.pauseOnLostFocus=false;client.options.guiScale().set(2);client.options.renderDistance().set(6);client.options.bobView().set(false);client.options.gamma().set(1.0);client.options.tutorialStep=net.minecraft.client.tutorial.TutorialSteps.NONE;
   String address="127.0.0.1:"+Integer.getInteger("robloxgang.occlusionPort",25575);
   ConnectScreen.startConnecting(new TitleScreen(),client,ServerAddress.parseString(address),new ServerData("Occlusion test",address,ServerData.Type.OTHER),false,null);connected=true;return;
  }
  if(client.player==null || client.level==null)return;
  int t=++ticks;client.setScreen(null);client.player.setYRot(0);client.player.setXRot(0);
  if(t==20){command(client,"tp @s 0 152 0");command(client,"fill -8 150 -8 8 150 8 minecraft:smooth_stone");command(client,"fill -4 151 2 4 157 6 minecraft:air");command(client,"fill -1 152 5 1 154 5 minecraft:barrier");command(client,"time set day");command(client,"weather clear");}

  if(t>=170)client.gui.getChat().clearMessages(true);
  if(t==180){if(BarrierOutlines.snapshot().size()<9)throw new AssertionError("Barriers not discovered");capture(client,"visible-on");}
  if(t==185)RobloxGangUtilities.config.barriers=false;
  if(t==195)capture(client,"visible-off");
  if(t==200){RobloxGangUtilities.config.barriers=true;command(client,"fill -3 151 3 3 156 3 minecraft:stone");}
  if((t==230 || t==280) && BarrierOutlines.snapshot().size()<9)throw new AssertionError("Occluded barriers missing from render snapshot");
  if(t==230)capture(client,"hidden-on");
  if(t==235)RobloxGangUtilities.config.barriers=false;
  if(t==245)capture(client,"hidden-off");
  if(t==250){RobloxGangUtilities.config.barriers=true;command(client,"fill -3 153 3 3 156 3 minecraft:air");}
  if(t==280)capture(client,"partial-on");
  if(t==285)RobloxGangUtilities.config.barriers=false;
  if(t==295)capture(client,"partial-off");
  if(t==300){RobloxGangUtilities.config.barriers=true;org.slf4j.LoggerFactory.getLogger("Occlusion Capture").info("RG_OCCLUSION_CAPTURE_OK");client.stop();}
 }
 private static void command(Minecraft client,String cmd){client.player.connection.sendCommand(cmd);}
 private static void capture(Minecraft client,String name)throws Exception{
  Object target;
  try{target=client.getClass().getMethod("getMainRenderTarget").invoke(client);}catch(NoSuchMethodException e){target=client.gameRenderer.getClass().getMethod("mainRenderTarget").invoke(client.gameRenderer);}
  for(Method m:Class.forName("net.minecraft.client.Screenshot").getMethods())if(m.getName().equals("grab")&&m.getParameterCount()==5){m.invoke(null,client.gameDirectory,"occlusion-"+name+".png",target,1,(Consumer<Component>)msg->{});return;}
  throw new AssertionError("Screenshot API missing");
 }
}
