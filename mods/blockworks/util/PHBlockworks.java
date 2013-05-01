package mods.blockworks.util;

import java.io.File;
import java.io.IOException;

import mods.tinker.tconstruct.TConstruct;
import net.minecraftforge.common.Configuration;

public class PHBlockworks
{

    public static void initProps ()
    {
        /* Here we will set up the config file for the mod 
         * First: Create a folder inside the config folder
         * Second: Create the actual config file
         * Note: Configs are a pain, but absolutely necessary for every mod.
         */
        File file = new File(TConstruct.proxy.getLocation() + "/config");
        file.mkdir();
        File newFile = new File(TConstruct.proxy.getLocation() + "/config/Blockworks.txt");

        /* Some basic debugging will go a long way */
        try
        {
            newFile.createNewFile();
        }
        catch (IOException e)
        {
            System.out.println("Could not create configuration file for Blockworks. Reason:");
            System.out.println(e);
        }

        /* [Forge] Configuration class, used as config method */
        Configuration config = new Configuration(newFile);

        /* Load the configuration file */
        config.load();

        /* Define the mod's IDs. 
         * Avoid values below 4096 for items and in the 250-450 range for blocks
         */

        cubeWand = config.getItem("Cube Wand", 27861).getInt(27861);
        cylinderWand = config.getItem("Cylinder Wand", 27862).getInt(27862);
        sphereWand = config.getItem("Sphere Wand", 27863).getInt(27863);
        copyWand = config.getItem("Copy Wand", 27864).getInt(27864);

        /* Save the configuration file */
        config.save();
    }

    //Blocks
    public static int cubeWand;
    public static int cylinderWand;
    public static int sphereWand;
    public static int copyWand;
}