package ru.robloxgang.utilities;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import java.util.List;
/** Minecraft 1.21.10: world-space lines use the terrain depth buffer. */
public final class BarrierWorldRenderer {
    public static void init(){
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context->{
            if(!RobloxGangUtilities.config.barriers)return;
            var camera=context.worldState().cameraRenderState.pos;
            var pose=context.matrices().last();
            var lines=context.consumers().getBuffer(RenderType.lines());
            for(BlockPos p:BarrierOutlines.snapshot())
                ShapeRenderer.renderLineBox(pose,lines,p.getX()-camera.x,p.getY()-camera.y,p.getZ()-camera.z,
                    p.getX()+1-camera.x,p.getY()+1-camera.y,p.getZ()+1-camera.z,.61f,.61f,.61f,.44f);
        });
    }
    public static void submit(Minecraft mc,List<BlockPos> blocks){}
}
