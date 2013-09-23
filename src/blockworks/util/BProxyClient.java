package blockworks.util;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class BProxyClient extends BProxyCommon
{

    public void registerTickHandler ()
    {
        BTickHandler ticker = new BTickHandler();
        TickRegistry.registerTickHandler(ticker, Side.CLIENT);
        MinecraftForge.EVENT_BUS.register(ticker);
        KeyBindingRegistry.registerKeyBinding(new BKeyHandler());
    }
}
