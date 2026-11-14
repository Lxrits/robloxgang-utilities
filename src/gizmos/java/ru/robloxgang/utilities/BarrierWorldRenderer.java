package ru.robloxgang.utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.GizmoStyle;
import java.util.List;
/** Minecraft 1.21.11 and 26.x: default gizmos are depth tested. */
public final class BarrierWorldRenderer {
    public static void init(){}
    public static void submit(Minecraft mc,List<BlockPos> blocks){
        try(var ignored=mc.collectPerTickGizmos()){
            for(BlockPos p:blocks)Gizmos.cuboid(p,GizmoStyle.stroke(0x709B9B9B,1));
            // Do not setAlwaysOnTop: terrain must occlude hidden edges.
        }
    }
}
