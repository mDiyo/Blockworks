package blockworks.util;

import blockworks.Blockworks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class TabBlockworks extends CreativeTabs
{
    ItemStack display;
    
    public TabBlockworks(String label) 
    {
        super(label);
    }
    
    public ItemStack getIconItemStack()
    {
        return new ItemStack(Blockworks.cubeWand);
    }
}