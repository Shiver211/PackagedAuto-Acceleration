package com.shiver.pkgacc.network;

import com.shiver.pkgacc.client.gui.GuiCard;
import com.shiver.pkgacc.container.ContainerCard;
import com.shiver.pkgacc.speed.SpeedCardHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final GuiHandler INSTANCE = new GuiHandler();

    public static final int GUI_CARDS = 0;

    private GuiHandler() {}

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return SpeedCardHelper.isSupported(tile) ? new ContainerCard(player.inventory, tile) : null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return SpeedCardHelper.isSupported(tile) ? new GuiCard(player.inventory, tile) : null;
    }
}
