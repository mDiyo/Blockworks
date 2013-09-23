package blockworks.items;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration.UnicodeInputStreamReader;
import net.minecraftforge.common.ForgeDirection;
import blockworks.Blockworks;
import blockworks.util.BKeyHandler;

/*
 * The great copy wand
 * Move structures from one place to another!
 */

public class CopyWand extends WandBase
{
    public int[][][] blockIDs = new int[0][0][0];
    public int[][][] metadata = new int[0][0][0];
    public HashMap<List, Integer> blockCount = new HashMap<List, Integer>();

    public CopyWand(int id)
    {
        super(id);
    }

    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        copyArea(player, stack);
        return stack;
    }

    @Override
    public boolean onEntitySwing (EntityLivingBase player, ItemStack stack)
    {
        placeCopy(player, stack);
        return true;
    }

    public void copyArea (EntityLivingBase player, ItemStack stack)
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
                if (!player.worldObj.isRemote && player instanceof EntityPlayer)
                    ((EntityPlayer) player).addChatMessage("First coord set at X: " + xPos + ", Y: " + yPos + ", Z: " + zPos);
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

                blockIDs = new int[endX - startX + 1][endY - startY + 1][endZ - startZ + 1];
                metadata = new int[endX - startX + 1][endY - startY + 1][endZ - startZ + 1];

                blockCount.clear();
                for (int x = startX; x <= endX; x++)
                {
                    for (int y = startY; y <= endY; y++)
                    {
                        for (int z = startZ; z <= endZ; z++)
                        {
                            readBlockIntoArray(player.worldObj, x, y, z, startX, startY, startZ);
                            //player.worldObj.setBlock(x, y, z, tags.getInteger("mainID"), tags.getInteger("mainMeta"), 3);
                        }
                    }
                }
                if (!player.worldObj.isRemote && player instanceof EntityPlayer)
                    ((EntityPlayer) player).addChatMessage("Second coord set at X: " + xPos + ", Y: " + yPos + ", Z: " + zPos);
                tags.setByte("clicks", (byte) 0);
                tags.setIntArray("clickOne", new int[0]);
            }
        }
    }

    void readBlockIntoArray (World world, int x, int y, int z, int startX, int startY, int startZ)
    {
        int blockID = world.getBlockId(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        blockIDs[x - startX][y - startY][z - startZ] = blockID;
        metadata[x - startX][y - startY][z - startZ] = meta;

        List key = Arrays.asList(blockID, meta);
        int amount = 0;
        if (blockCount.containsKey(key))
            amount = blockCount.get(key);
        amount++;
        blockCount.put(key, amount);
    }

    public void placeCopy (EntityLivingBase player, ItemStack stack)
    {
        if (blockIDs.length > 0)
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

                World world = player.worldObj;
                for (int x = 0; x < blockIDs.length; x++)
                {
                    for (int y = 0; y < blockIDs[0].length; y++)
                    {
                        for (int z = 0; z < blockIDs[0][0].length; z++)
                        {
                            world.setBlock(xPos + x, yPos + y, zPos + z, blockIDs[x][y][z], metadata[x][y][z], 3);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void saveKey ()
    {
        System.out.println("Saving structure");
        if (blockIDs.length > 0)
        {
            File structure = new File(Blockworks.structureFolder.getAbsolutePath() + "/structure.txt");
            FileOutputStream fos;
            try
            {
                fos = new FileOutputStream(structure);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));

                writer.write("Structure: Test");
                writer.newLine();
                writer.write("Size - x:" + blockIDs.length + ", y:" + blockIDs[0].length + ", z:" + blockIDs[0][0].length);
                writer.newLine();
                writeBlockAmounts(writer);
                writer.newLine();

                for (int y = blockIDs[0].length - 1; y >= 0; y--)
                {
                    writer.write("startlayer y:" + y);
                    writer.newLine();
                    for (int x = 0; x < blockIDs.length; x++)
                    {
                        for (int z = 0; z < blockIDs[0][0].length; z++)
                        {
                            writer.write(blockIDs[x][y][z] + ":" + metadata[x][y][z] + ",");
                        }
                        writer.newLine();
                    }
                    writer.write("endlayer");
                    writer.newLine();
                    writer.newLine();
                }
                //buffer.write("Stuff");

                writer.close();
                fos.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    void writeBlockAmounts (BufferedWriter writer) throws IOException
    {
        blockCount.remove(Arrays.asList(0, 0));
        Iterator iter = blockCount.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            List list = (List) pairs.getKey();
            Object[] block = list.toArray();
            writer.write(block[0] + ":" + block[1] + " - " + pairs.getValue());
            writer.newLine();
        }
    }

    @Override
    public void loadKey ()
    {
        System.out.println("Loading structure");
        File file = new File(Blockworks.structureFolder.getAbsolutePath() + "/structure.txt");

        BufferedReader reader = null;
        UnicodeInputStreamReader input = null;
        try
        {
            input = new UnicodeInputStreamReader(new FileInputStream(file), "UTF-8");
            reader = new BufferedReader(input);

            reader.readLine();
            String line = reader.readLine();
            int[] size = new int[3];

            String[] sizeString = line.split(",");
            for (int i = 0; i < 3; i++)
            {
                String[] lsize = sizeString[i].split(":");
                size[i] = new Integer(lsize[1]);
            }

            blockIDs = new int[size[0]][size[1]][size[2]];
            metadata = new int[size[0]][size[1]][size[2]];

            while (true)
            {
                line = reader.readLine();

                if (line == null)
                {
                    break;
                }

                if (line.contains("startlayer"))
                {
                    String[] split = line.split(":");
                    Integer i = new Integer(split[1]);
                    readLayer(reader, new Integer(split[1]));
                }
            }

            input.close();
            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void readLayer (BufferedReader reader, Integer y) throws IOException
    {
        int z = 0;
        String line;
        while (true)
        {
            line = reader.readLine();
            if (line == null || line.contains("endlayer"))
            {
                break;
            }
            String[] blocks = line.split(",");
            for (int x = 0; x < blocks.length; x++)
            {
                String[] ids = blocks[x].split(":");
                blockIDs[x][y][z] = new Integer(ids[0]);
                metadata[x][y][z] = new Integer(ids[1]);
            }
            z++;
        }
    }

    @Override
    public void getSubItems (int id, CreativeTabs tabs, List list)
    {
        ItemStack stack = new ItemStack(id, 1, 0);
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound tags = new NBTTagCompound();

        compound.setCompoundTag("Wandworks", tags);
        stack.setTagCompound(compound);

        list.add(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add("\u00a7bCopies the world data between two points");
        list.add("\u00a7bRight-click: Select two points to copy");
        list.add("\u00a7bLeft-click: Paste copied data into the world");
        list.add("\u00a7bSave: "+BKeyHandler.save.keyCode);
        list.add("\u00a7bLoad: "+BKeyHandler.load.keyCode);
    }
}
