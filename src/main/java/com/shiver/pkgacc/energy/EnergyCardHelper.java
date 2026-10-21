package com.shiver.pkgacc.energy;

import com.shiver.pkgacc.config.PackagedAccelerationConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.tile.TileBase;
import thelm.packagedauto.tile.TileUnpackager;

public class EnergyCardHelper {

    private static final String ENERGY_CARDS_KEY = "PackagedAccelerationEnergyCards";
    private static final String BASE_CAPACITY_KEY = "PackagedAccelerationBaseCapacity";

    private EnergyCardHelper() {}

    public static boolean isSupported(TileEntity tile) {
        return tile instanceof TileBase && !(tile instanceof TileUnpackager) && ((TileBase)tile).getEnergyStorage().getMaxEnergyStored() > 0;
    }

    public static int getCards(TileEntity tile) {
        return tile instanceof TileBase ? tile.getTileData().getInteger(ENERGY_CARDS_KEY) : 0;
    }

    public static void setCards(TileEntity tile, int count) {
        if(isSupported(tile)) {
            NBTTagCompound data = tile.getTileData();
            int clamped = clampCards(count);
            if(clamped > 0) {
                data.setInteger(ENERGY_CARDS_KEY, clamped);
            }
            else {
                data.removeTag(ENERGY_CARDS_KEY);
            }
            tile.markDirty();
            applyCapacity((TileBase)tile);
            ((TileBase)tile).syncTile(false);
        }
    }

    public static int clampCards(int count) {
        return Math.max(0, Math.min(PackagedAccelerationConfig.maxEnergyCards, count));
    }

    public static double getMultiplier(int cards) {
        return 1D + cards * PackagedAccelerationConfig.energyPerCard;
    }

    public static void writeSync(TileEntity tile, NBTTagCompound nbt) {
        nbt.setInteger(ENERGY_CARDS_KEY, getCards(tile));
        if(tile.getTileData().hasKey(BASE_CAPACITY_KEY)) {
            nbt.setInteger(BASE_CAPACITY_KEY, tile.getTileData().getInteger(BASE_CAPACITY_KEY));
        }
    }

    public static void readSync(TileEntity tile, NBTTagCompound nbt) {
        if(tile instanceof TileBase) {
            int cards = clampCards(nbt.getInteger(ENERGY_CARDS_KEY));
            NBTTagCompound data = tile.getTileData();
            if(cards > 0) {
                data.setInteger(ENERGY_CARDS_KEY, cards);
            }
            else {
                data.removeTag(ENERGY_CARDS_KEY);
            }
            if(nbt.hasKey(BASE_CAPACITY_KEY)) {
                data.setInteger(BASE_CAPACITY_KEY, nbt.getInteger(BASE_CAPACITY_KEY));
            }
            // 必须先恢复容量，避免后续读档或能量同步按原始容量截断。
            applyCapacity((TileBase)tile);
            if(nbt.hasKey("Energy")) {
                ((TileBase)tile).getEnergyStorage().readFromNBT(nbt);
            }
        }
    }

    public static void applyCapacity(TileBase tile) {
        EnergyStorage storage = tile.getEnergyStorage();
        if(storage.getMaxEnergyStored() <= 0) {
            return;
        }
        NBTTagCompound data = tile.getTileData();
        int base;
        if(data.hasKey(BASE_CAPACITY_KEY)) {
            base = data.getInteger(BASE_CAPACITY_KEY);
        }
        else {
            base = storage.getMaxEnergyStored();
            data.setInteger(BASE_CAPACITY_KEY, base);
        }
        int cards = getCards(tile);
        int target = (int)Math.min(Integer.MAX_VALUE, (long)(base * getMultiplier(cards)));
        if(storage.getMaxEnergyStored() != target) {
            storage.setCapacity(target);
        }
        // 封包机器的原始输入、提取上限均等于容量，升级和拆卡时一起恢复。
        storage.setMaxTransfer(target);
    }
}
