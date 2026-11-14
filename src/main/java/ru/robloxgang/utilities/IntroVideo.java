package ru.robloxgang.utilities;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import javax.sound.sampled.*;
import java.io.*;
import org.slf4j.LoggerFactory;
/** Predecoded video pages avoid native codecs and work before resource reload completes. */
public final class IntroVideo {
    private static Object owner;
    private static long start;
    private static boolean finished,failed;
    private static int page=-1;
    private static ResourceLocation texture;
    private static volatile SourceDataLine audio;
    public static boolean enabled(Object candidate){if(owner==null)owner=candidate; return owner==candidate && RobloxGangUtilities.config!=null && RobloxGangUtilities.config.intro && !failed;}
    public static boolean holding(Object candidate){return enabled(candidate) && (start==0 || (System.nanoTime()-start)<11120000000L);}
    public static void finish(){
        finished=true;stop();
        if(texture!=null){Minecraft.getInstance().getTextureManager().release(texture);texture=null;}
    }
    public static void render(GuiGraphics g,Object candidate){render(g,candidate,1);}
    public static void render(GuiGraphics g,Object candidate,float opacity){
        if(!enabled(candidate) || opacity<=0)return;
        if(start==0){start=System.nanoTime();sound();}
        double elapsed=(System.nanoTime()-start)/1e9;
        if(elapsed>=11.12 && !finished){finished=true;stop();}
        int frame=Math.min(333,(int)(elapsed*30)),wanted=frame/8;
        try{
            if(wanted!=page){
                ResourceLocation next=ResourceLocation.fromNamespaceAndPath("robloxgang_utilities","intro/page_"+wanted);
                try(InputStream in=IntroVideo.class.getResourceAsStream("/assets/robloxgang_utilities/intro/page_"+wanted+".png")){
                    if(in==null)throw new IOException("Missing intro page "+wanted);
                    Minecraft.getInstance().getTextureManager().register(next,new DynamicTexture(()->"RobloxGang intro",NativeImage.read(in)));
                }
                if(texture!=null)Minecraft.getInstance().getTextureManager().release(texture);
                texture=next;page=wanted;
            }
            int alpha=Math.clamp(Math.round(opacity*255),0,255);
            g.fill(0,0,g.guiWidth(),g.guiHeight(),alpha<<24);
            int w=g.guiWidth(),h=w*9/16;if(h>g.guiHeight()){h=g.guiHeight();w=h*16/9;}
            g.blit(RenderPipelines.GUI_TEXTURED,texture,(g.guiWidth()-w)/2,(g.guiHeight()-h)/2,(frame%2)*480f,((frame%8)/2)*270f,w,h,480,270,960,1080,(alpha<<24)|0xFFFFFF);
        }catch(Exception e){failed=true;stop();LoggerFactory.getLogger("RobloxGang Utilities").warn("Intro unavailable; using vanilla loading screen",e);}
    }
    private static void sound(){
        float volume=RobloxGangUtilities.config.volume;
        Thread.ofPlatform().daemon().name("RobloxGang intro audio").start(()->{
            try(InputStream stream=IntroVideo.class.getResourceAsStream("/assets/robloxgang_utilities/intro/audio.wav");
                AudioInputStream pcm=AudioSystem.getAudioInputStream(new BufferedInputStream(stream))){
                SourceDataLine line=AudioSystem.getSourceDataLine(pcm.getFormat());audio=line;line.open(pcm.getFormat());
                if(line.isControlSupported(FloatControl.Type.MASTER_GAIN))((FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN)).setValue(volume<=0?-80:(float)(20*Math.log10(volume)));
                line.start();byte[] data=new byte[8192];int count;
                while(!finished&&!failed&&(count=pcm.read(data))!=-1)line.write(data,0,count);
                if(!finished&&!failed)line.drain();line.close();
            }catch(Exception e){LoggerFactory.getLogger("RobloxGang Utilities").warn("Intro audio unavailable",e);}
            finally{audio=null;}
        });
    }
    private static void stop(){SourceDataLine line=audio;if(line!=null){line.stop();line.flush();line.close();}}
}
