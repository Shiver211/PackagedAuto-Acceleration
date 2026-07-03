package com.shiver.pkgacc.speed;

import com.shiver.pkgacc.config.PackagedAccelerationConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedauto.tile.TileUnpackager;

public class SpeedCardHelper {

    private static final String SPEED_CARDS_KEY = "PackagedAccelerationSpeedCards";

    private SpeedCardHelper() {}

    public static boolean isSupported(TileEntity tile) {
        return tile instanceof TileBase && !(tile instanceof TileUnpackager) && ((TileBase)tile).getEnergyStorage().getMaxEnergyStored() > 0;
    }

    public static int getCards(TileEntity tile) {
        return tile instanceof TileBase ? tile.getTileData().getInteger(SPEED_CARDS_KEY) : 0;
    }

    public static void setCards(TileEntity tile, int count) {
        if(isSupported(tile)) {
            NBTTagCompound data = tile.getTileData();
            int clamped = clampCards(count);
            if(clamped > 0) {
                data.setInteger(SPEED_CARDS_KEY, clamped);
            }
            else {
                data.removeTag(SPEED_CARDS_KEY);
            }
            tile.markDirty();
        }
    }

    public static int clampCards(int count) {
        return Math.max(0, Math.min(PackagedAccelerationConfig.maxSpeedCards, count));
    }

    public static double getMultiplier(int cards) {
        return 1D + cards * PackagedAccelerationConfig.speedPerCard;
    }

    public static void writeSync(TileEntity tile, NBTTagCompound nbt) {
        nbt.setInteger(SPEED_CARDS_KEY, getCards(tile));
    }

    public static void readSync(TileEntity tile, NBTTagCompound nbt) {
        if(tile instanceof TileBase) {
            int cards = clampCards(nbt.getInteger(SPEED_CARDS_KEY));
            NBTTagCompound data = tile.getTileData();
            if(cards > 0) {
                data.setInteger(SPEED_CARDS_KEY, cards);
            }
            else {
                data.removeTag(SPEED_CARDS_KEY);
            }
        }
    }
}
