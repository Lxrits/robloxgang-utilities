package ru.robloxgang.utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import java.util.*;
/** Reads only already loaded client chunks. No packets, chunk requests or world changes. */
public final class BarrierOutlines {
    private static final Map<Long,List<BlockPos>> found=new HashMap<>();
    private static net.minecraft.client.multiplayer.ClientLevel level;
    private static int cursor;
    private static List<BlockPos> snapshot=List.of();
    public static List<BlockPos> snapshot(){return snapshot;}
    public static void tick(Minecraft mc){
        if(mc.level!=level){found.clear();snapshot=List.of();level=mc.level;cursor=0;}
        if(mc.level==null || mc.player==null || !RobloxGangUtilities.config.barriers){found.clear();snapshot=List.of();return;}
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
        submit(mc);
    }
    public static void submit(Minecraft mc){
        if(mc.level==null || mc.player==null || !RobloxGangUtilities.config.barriers){snapshot=List.of();return;}
        List<BlockPos> current=new ArrayList<>();
        for(List<BlockPos> positions:found.values())for(BlockPos p:positions)
            if(mc.level.getBlockState(p).is(Blocks.BARRIER))current.add(p);
        snapshot=List.copyOf(current);
        BarrierWorldRenderer.submit(mc,snapshot);
    }
}
