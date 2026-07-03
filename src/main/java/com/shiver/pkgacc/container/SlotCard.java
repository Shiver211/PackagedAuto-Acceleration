package com.shiver.pkgacc.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.IntSupplier;

public class SlotCard extends Slot {

    private final Item card;
    private final IntSupplier stackLimit;

    public SlotCard(IInventory inventory, int index, int xPosition, int yPosition, Item card, IntSupplier stackLimit) {
        super(inventory, index, xPosition, yPosition);
        this.card = card;
        this.stackLimit = stackLimit;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() == card;
    }

    @Override
    public int getSlotStackLimit() {
        return stackLimit.getAsInt();
    }
}
