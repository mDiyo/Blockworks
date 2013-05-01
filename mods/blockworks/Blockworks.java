package mods.blockworks;

import mods.blockworks.items.*;
import mods.blockworks.util.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;

/* Unlimited Blockworks! 
 */

@Mod(modid = "Blockworks", name = "Unlimited Blockworks", version = "0.0.1")
public class Blockworks
{
    @Instance("Blockworks")
    public static Blockworks instance;
    
    @SidedProxy(clientSide = "mods.blockworks.util.BProxyClient", serverSide = "mods.blockworks.util.BProxyCommon")
    public static BProxyCommon proxy;
    
    @PreInit
    public void preInit (FMLPreInitializationEvent evt)
    {
        PHBlockworks.initProps();
        tab = new TabBlockworks("Blockworks");
        cubeWand = new CubeWand(PHBlockworks.cubeWand).setUnlocalizedName("wand.cube");
        LanguageRegistry.addName(cubeWand, "Cube Wand");
        proxy.registerTickHandler();
    }
    
    public static CreativeTabs tab;
    public static Item cubeWand;
    public static Item cylinderWand;
    public static Item sphereWand;
    public static Item copyWand;
}
