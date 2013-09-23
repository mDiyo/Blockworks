package blockworks;

import java.io.File;
import java.io.IOException;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import blockworks.items.CopyWand;
import blockworks.items.CubeWand;
import blockworks.items.WallWand;
import blockworks.util.BProxyCommon;
import blockworks.util.PHBlockworks;
import blockworks.util.TabBlockworks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;

/* Unlimited Blockworks! 
 */

@Mod(modid = "Blockworks", name = "Unlimited Blockworks", version = "0.0.2")
public class Blockworks
{
    @Instance("Blockworks")
    public static Blockworks instance;
    
    @SidedProxy(clientSide = "blockworks.util.BProxyClient", serverSide = "blockworks.util.BProxyCommon")
    public static BProxyCommon proxy;

    public static File structureFolder;
    
    @EventHandler
    public void preInit (FMLPreInitializationEvent evt)
    {
        File config = evt.getModConfigurationDirectory();
        structureFolder = new File(config.getAbsolutePath()+"/../Blockworks");
        structureFolder.mkdirs();
        try
        {
            structureFolder.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        PHBlockworks.initProps(evt.getModConfigurationDirectory());
        tab = new TabBlockworks("Blockworks");
        cubeWand = new CubeWand(PHBlockworks.cubeWand).setUnlocalizedName("wand.cube").setTextureName("blockworks:cubewand");
        wallWand = new WallWand(PHBlockworks.wallWand).setUnlocalizedName("wand.wall").setTextureName("blockworks:wallwand");
        copyWand = new CopyWand(PHBlockworks.copyWand).setUnlocalizedName("wand.copy").setTextureName("blockworks:copywand");
        LanguageRegistry.addName(cubeWand, "Cube Wand");
        LanguageRegistry.addName(wallWand, "Wall Wand");
        LanguageRegistry.addName(copyWand, "Copy Wand");
        LanguageRegistry.instance().addStringLocalization("itemGroup.Blockworks", "Blockworks");
        proxy.registerTickHandler();
    }
    
    public static CreativeTabs tab;
    public static Item cubeWand;
    public static Item wallWand;
    public static Item cylinderWand;
    public static Item sphereWand;
    public static Item pyramidWand;
    public static Item copyWand;
}
