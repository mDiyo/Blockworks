package blockworks.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import blockworks.Blockworks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/*
 * mDiyo's development building item
 * Builds everything on right-click!
 */

public class CubeWand extends Item
{
    public CubeWand(int id)
    {
        super(id);
        setCreativeTab(Blockworks.tab);
    }
    
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        placeBlock(player, stack);
        return stack;
    }

    @Override
    public boolean onEntitySwing (EntityLivingBase entityLiving, ItemStack stack)
    {
        if (entityLiving instanceof EntityPlayer)// && !mod_Test.lseftClick)
        {
            EntityPlayer player = (EntityPlayer) entityLiving;
            placeBlock(player, stack);
            //mod_Test.leftClick = true;
        }
        return true;
    }
    
    public void placeBlock(EntityPlayer player, ItemStack stack)
    {
        MovingObjectPosition mop = this.raytraceFromPlayer(player.worldObj, player, false);

        if (mop != null)
        {
            int xPos = mop.blockX;
            int yPos = mop.blockY;
            int zPos = mop.blockZ;
            ForgeDirection sideHit = ForgeDirection.getOrientation(mop.sideHit);
            switch (sideHit)
            {
            case UP: yPos += 1; break;
            case DOWN: yPos -= 1; break;
            case NORTH: zPos -= 1; break;
            case SOUTH: zPos += 1; break;
            case EAST: xPos += 1; break;
            case WEST: xPos -= 1; break;
            default: break;
            }
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("Wandworks");
            player.worldObj.setBlock(xPos, yPos, zPos, tags.getInteger("mainID"), tags.getInteger("mainMeta"), 3);
        }
    }

    public MovingObjectPosition raytraceFromPlayer (World par1World, EntityPlayer par2EntityPlayer, boolean par3)
    {
        float f = 1.0F;
        float f1 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * f;
        float f2 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * f;
        double d0 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * (double) f;
        double d1 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * (double) f + 1.62D - (double) par2EntityPlayer.yOffset;
        double d2 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * (double) f;
        Vec3 vec3 = par1World.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 500.0D; //Max range
        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return par1World.rayTraceBlocks_do_do(vec3, vec31, par3, !par3);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("blockworks:cubewand");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add("Creates blocks in a cube shape");
    }
    
    @Override
    public void getSubItems (int id, CreativeTabs tabs, List list)
    {
        ItemStack stack = new ItemStack(id, 1, 0);
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound tags = new NBTTagCompound();
        compound.setCompoundTag("Wandworks", tags);

        tags.setInteger("Height", 1);
        tags.setInteger("Width", 1);
        tags.setInteger("Length", 1);
        
        tags.setByte("mode", (byte) 1);

        tags.setInteger("mainID", Block.stone.blockID);
        tags.setInteger("mainMeta", 0);
        tags.setInteger("altID", 0);
        tags.setInteger("altMeta", 0);

        stack.setTagCompound(compound);

        list.add(stack);
    }
}
