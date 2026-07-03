package com.shiver.pkgacc.container;

import com.shiver.pkgacc.config.PackagedAccelerationConfig;
import com.shiver.pkgacc.energy.EnergyCardHelper;
import com.shiver.pkgacc.item.ItemEnergyCard;
import com.shiver.pkgacc.item.ItemSpeedCard;
import com.shiver.pkgacc.speed.SpeedCardHelper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class ContainerCard extends Container {

    private final InventoryCard speedInventory;
    private final InventoryCard energyInventory;

    public ContainerCard(InventoryPlayer playerInventory, TileEntity tile) {
        speedInventory = new InventoryCard(tile, ItemSpeedCard.INSTANCE, () -> PackagedAccelerationConfig.maxSpeedCards,
                SpeedCardHelper::getCards, SpeedCardHelper::setCards, "container.packaged_acceleration.speed_card");
        energyInventory = new InventoryCard(tile, ItemEnergyCard.INSTANCE, () -> PackagedAccelerationConfig.maxEnergyCards,
                EnergyCardHelper::getCards, EnergyCardHelper::setCards, "container.packaged_acceleration.energy_card");
        addSlotToContainer(new SlotCard(speedInventory, 0, 62, 36, ItemSpeedCard.INSTANCE, () -> PackagedAccelerationConfig.maxSpeedCards));
        addSlotToContainer(new SlotCard(energyInventory, 0, 98, 36, ItemEnergyCard.INSTANCE, () -> PackagedAccelerationConfig.maxEnergyCards));

        for(int row = 0; row < 3; row++) {
            for(int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for(int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player) {
        return speedInventory.isUsableByPlayer(player);
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int index) {
        ItemStack ret;
        Slot slot = inventorySlots.get(index);
        if(slot == null || !slot.getHasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getStack();
        ret = stack.copy();
        if(index < 2) {
            // From a card slot back into the player inventory.
            if(!mergeItemStack(stack, 2, inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        }
        else if(stack.getItem() == ItemSpeedCard.INSTANCE) {
            if(!mergeItemStack(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        }
        else if(stack.getItem() == ItemEnergyCard.INSTANCE) {
            if(!mergeItemStack(stack, 1, 2, false)) {
                return ItemStack.EMPTY;
            }
        }
        else {
            return ItemStack.EMPTY;
        }
        if(stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        }
        else {
            slot.onSlotChanged();
        }
        return ret;
    }
}
