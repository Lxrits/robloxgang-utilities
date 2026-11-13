package ru.robloxgang.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

final class FeaturePhotos {
    static boolean draw(GuiGraphics g,int feature,int x,int y,int w,int h) {
        String name=feature==0?"sneak":feature==2?"gang":feature==4?"chat":"reminder";
        ResourceLocation id=ResourceLocation.fromNamespaceAndPath("robloxgang_utilities","textures/demo/"+name+".png");
        if(Minecraft.getInstance().getResourceManager().getResource(id).isEmpty())return false;
        // Unedited frames from the 1280 x 720 multiplayer client.
        // Zoom into the actual tab and surrounding world without clipping any of the five names.
        if(feature==2){g.blit(RenderPipelines.GUI_TEXTURED,id,x,y,240f,0f,w,h,800,450,1280,720);return true;}
        g.blit(RenderPipelines.GUI_TEXTURED,id,x,y,0f,0f,w,h,1280,720,1280,720);
        return true;
    }
}
