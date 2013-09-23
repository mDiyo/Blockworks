package blockworks.util;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import blockworks.items.WandBase;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class BKeyHandler extends KeyHandler
{
    public static KeyBinding save = new KeyBinding("blockworks.savekey", 44);
    public static KeyBinding load = new KeyBinding("blockworks.loadkey", 45);
    Minecraft mc = Minecraft.getMinecraft();

    public BKeyHandler()
    {
        super(new KeyBinding[] { save, load }, new boolean[] { false, false });
    }

    @Override
    public String getLabel ()
    {
        return null;
    }

    @Override
    public void keyDown (EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
    {
        if (mc.theWorld != null)
        {
            if (kb == save)
            {
                EntityPlayer player = mc.thePlayer;
                ItemStack equip = player.getCurrentEquippedItem();
                if (equip != null && equip.getItem() instanceof WandBase)
                {
                    ((WandBase) equip.getItem()).saveKey();
                }
            }
            if (kb == load)
            {
                EntityPlayer player = mc.thePlayer;
                ItemStack equip = player.getCurrentEquippedItem();
                if (equip != null && equip.getItem() instanceof WandBase)
                {
                    ((WandBase) equip.getItem()).loadKey();
                }
            }
        }
    }

    @Override
    public void keyUp (EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
    {
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.CLIENT);
    }
}
