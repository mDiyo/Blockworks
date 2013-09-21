package blockworks.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
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

public class WallWand extends Item
{
    public WallWand(int id)
    {
        super(id);
        setCreativeTab(Blockworks.tab);
    }

    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        if (player.isSneaking())
        {
            chooseNewBlock(player, stack);
        }
        else
        {
            placeBlock(player, stack);
        }
        return stack;
    }

    @Override
    public boolean onEntitySwing (EntityLiving entityLiving, ItemStack stack)
    {
        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entityLiving;
            placeBlock(player, stack);
        }
        return true;
    }

    public void chooseNewBlock (EntityPlayer player, ItemStack stack)
    {
        World world = player.worldObj;
        MovingObjectPosition mop = this.raytraceFromPlayer(world, player, false);

        if (mop != null)
        {
            int xPos = mop.blockX;
            int yPos = mop.blockY;
            int zPos = mop.blockZ;

            int blockID = world.getBlockId(xPos, yPos, zPos);
            int blockMeta = world.getBlockMetadata(xPos, yPos, zPos);
            ItemStack blockStack = new ItemStack(blockID, 1, blockMeta);
            if (!player.worldObj.isRemote)
                player.addChatMessage("New block: " + blockStack.getDisplayName());

            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("Wandworks");
            tags.setInteger("mainID", blockID);
            tags.setInteger("mainMeta", blockMeta);
        }
        else
        {
            int blockID = 0;
            int blockMeta = 0;
            if (!player.worldObj.isRemote)
                player.addChatMessage("New block: Air");

            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("Wandworks");
            tags.setInteger("mainID", blockID);
            tags.setInteger("mainMeta", blockMeta);
        }
    }

    public void placeBlock (EntityPlayer player, ItemStack stack)
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
            case UP:
                yPos += 1;
                break;
            case DOWN:
                yPos -= 1;
                break;
            case NORTH:
                zPos -= 1;
                break;
            case SOUTH:
                zPos += 1;
                break;
            case EAST:
                xPos += 1;
                break;
            case WEST:
                xPos -= 1;
                break;
            default:
                break;
            }
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("Wandworks");
            byte clicks = tags.getByte("clicks");
            if (clicks == 0)
            {
                tags.setIntArray("clickOne", new int[] { xPos, yPos, zPos });
                if (!player.worldObj.isRemote)
                    player.addChatMessage("First coord set at X: " + xPos + ", Y: " + yPos + ", Z: " + zPos);
                tags.setByte("clicks", (byte) 1);
            }
            else
            {
                int[] start = tags.getIntArray("clickOne");
                int startX = xPos < start[0] ? xPos : start[0];
                int startY = yPos < start[1] ? yPos : start[1];
                int startZ = zPos < start[2] ? zPos : start[2];
                int endX = xPos > start[0] ? xPos : start[0];
                int endY = yPos > start[1] ? yPos : start[1];
                int endZ = zPos > start[2] ? zPos : start[2];

                for (int x = startX; x <= endX; x++)
                {
                    for (int y = startY; y <= endY; y++)
                    {
                        for (int z = startZ; z <= endZ; z++)
                        {
                            player.worldObj.setBlock(x, y, z, tags.getInteger("mainID"), tags.getInteger("mainMeta"), 3);
                        }
                    }
                }
                if (!player.worldObj.isRemote)
                    player.addChatMessage("Second coord set at X: " + xPos + ", Y: " + yPos + ", Z: " + zPos);
                tags.setByte("clicks", (byte) 0);
                tags.setIntArray("clickOne", new int[0]);
            }
            //player.worldObj.setBlock(xPos, yPos, zPos, tags.getInteger("mainID"), tags.getInteger("mainMeta"), 3);
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
    public void registerIcons (IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("blockworks:wallwand");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add("\u00a7bCreates a wall of blocks");
        list.add("\u00a7bbetween two points");
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("Wandworks");
        int mainID = tags.getInteger("mainID");
        if (mainID == 0)
        {
            list.add("Selected block: Air");
        }
        else
        {
            ItemStack blockStack = new ItemStack(mainID, 1, tags.getInteger("mainMeta"));
            list.add("Selected block: "+blockStack.getDisplayName());
        }
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
        tags.setByte("clicks", (byte) 0);

        tags.setInteger("mainID", Block.sand.blockID);
        tags.setInteger("mainMeta", 0);
        tags.setInteger("altID", 0);
        tags.setInteger("altMeta", 0);

        stack.setTagCompound(compound);

        list.add(stack);
    }
}
