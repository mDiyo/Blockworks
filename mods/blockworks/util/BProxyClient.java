package mods.blockworks.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class BProxyClient extends BProxyCommon
{

    public void registerTickHandler()
    {
        BTickHandler ticker = new BTickHandler();
        TickRegistry.registerTickHandler(ticker, Side.CLIENT);
        BTickHandler.mc = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register(ticker);
    }
}
