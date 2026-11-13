package ru.robloxgang.utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import java.util.*;
/** Reads only already loaded client chunks. No packets, chunk requests or world changes. */
public final class BarrierOutlines {
    private static final Map<Long,List<BlockPos>> found=new HashMap<>();
    private static net.minecraft.client.multiplayer.ClientLevel level;
    private static int cursor;
    public static void tick(Minecraft mc){
        if(mc.level!=level){found.clear();level=mc.level;cursor=0;}
        if(mc.player==null || !RobloxGangUtilities.config.barriers){found.clear();return;}
        int radius=mc.options.getEffectiveRenderDistance(), side=radius*2+1;
        int cx=mc.player.blockPosition().getX()>>4,cz=mc.player.blockPosition().getZ()>>4;
        found.keySet().removeIf(k->Math.abs((int)(long)k-cx)>radius || Math.abs((int)(k>>32)-cz)>radius);
        for(int n=0;n<8;n++){
            int i=cursor++%(side*side),x=cx+i%side-radius,z=cz+i/side-radius;
            long key=((long)z<<32)|(x&0xffffffffL);
            LevelChunk chunk=level.getChunkSource().getChunk(x,z,ChunkStatus.FULL,false);
            if(chunk==null){found.remove(key);continue;}
            List<BlockPos> positions=new ArrayList<>();LevelChunkSection[] sections=chunk.getSections();
            for(int s=0;s<sections.length;s++){
                LevelChunkSection section=sections[s];if(section.hasOnlyAir() || !section.getStates().maybeHas(state->state.is(Blocks.BARRIER)))continue;
                int y0=level.getMinY()+s*16;
                for(int yy=0;yy<16;yy++)for(int zz=0;zz<16;zz++)for(int xx=0;xx<16;xx++)
                    if(section.getBlockState(xx,yy,zz).is(Blocks.BARRIER))positions.add(new BlockPos(x*16+xx,y0+yy,z*16+zz));
            }
            if(positions.isEmpty())found.remove(key);else found.put(key,positions);
        }
    }
    public static void render(GuiGraphics g){
        Minecraft mc=Minecraft.getInstance();if(mc.player==null || !RobloxGangUtilities.config.barriers)return;
        for(List<BlockPos> list:found.values())for(BlockPos p:list){
            if(!mc.level.getBlockState(p).is(Blocks.BARRIER))continue;
            // Project each corner with the game's active camera (including current FOV).
            Vec3[] points=new Vec3[8];
            for(int i=0;i<8;i++){
                Vec3 v=mc.gameRenderer.projectPointToScreen(new Vec3(p.getX()+(i&1),p.getY()+((i>>1)&1),p.getZ()+((i>>2)&1)));
                if(!Double.isFinite(v.x) || !Double.isFinite(v.y) || v.z<0 || v.z>=1){continue;}
                points[i]=new Vec3((v.x+1)*g.guiWidth()/2,(1-v.y)*g.guiHeight()/2,v.z);
            }
            for(int i=0;i<8;i++)for(int bit=1;bit<=4;bit*=2)if((i&bit)==0 && points[i]!=null && points[i|bit]!=null)line(g,points[i],points[i|bit]);
        }
    }
    private static void line(GuiGraphics g,Vec3 a,Vec3 b){
        if((a.x<0&&b.x<0)||(a.y<0&&b.y<0)||(a.x>g.guiWidth()&&b.x>g.guiWidth())||(a.y>g.guiHeight()&&b.y>g.guiHeight()))return;
        float dx=(float)(b.x-a.x),dy=(float)(b.y-a.y),length=(float)Math.hypot(dx,dy);
        if(length<.5f)return;
        g.pose().pushMatrix();g.pose().translate((float)a.x,(float)a.y);g.pose().rotate((float)Math.atan2(dy,dx));
        g.fill(0,0,(int)Math.ceil(length),1,0x709B9B9B);g.pose().popMatrix();
    }
}
