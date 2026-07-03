package com.shiver.pkgacc.container;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.shiver.pkgacc.speed.SpeedCardHelper;

import javax.annotation.Nonnull;
import java.util.function.IntSupplier;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

public class InventoryCard implements IInventory {

    private final TileEntity tile;
    private final Item card;
    private final IntSupplier stackLimit;
    private final ToIntFunction<TileEntity> getCards;
    private final ObjIntConsumer<TileEntity> setCards;
    private final String name;
    private final NonNullList<ItemStack> stack = NonNullList.withSize(1, ItemStack.EMPTY);

    public InventoryCard(TileEntity tile, Item card, IntSupplier stackLimit,
                         ToIntFunction<TileEntity> getCards, ObjIntConsumer<TileEntity> setCards, String name) {
        this.tile = tile;
        this.card = card;
        this.stackLimit = stackLimit;
        this.getCards = getCards;
        this.setCards = setCards;
        this.name = name;
        syncFromTile();
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        syncFromTile();
        return stack.get(0).isEmpty();
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack getStackInSlot(int index) {
        syncFromTile();
        return index == 0 ? stack.get(0) : ItemStack.EMPTY;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack decrStackSize(int index, int count) {
        if(index != 0 || count <= 0) {
            return ItemStack.EMPTY;
        }
        syncFromTile();
        ItemStack removed = ItemStackHelper.getAndSplit(stack, 0, count);
        syncToTile();
        return removed;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack removeStackFromSlot(int index) {
        if(index != 0) {
            return ItemStack.EMPTY;
        }
        syncFromTile();
        ItemStack removed = ItemStackHelper.getAndRemove(stack, 0);
        syncToTile();
        return removed;
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        if(index != 0) {
            return;
        }
        if(stack.isEmpty()) {
            this.stack.set(0, ItemStack.EMPTY);
        }
        else if(stack.getItem() == card) {
            ItemStack copy = stack.copy();
            copy.setCount(Math.min(copy.getCount(), getInventoryStackLimit()));
            this.stack.set(0, copy);
        }
        syncToTile();
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return stackLimit.getAsInt();
    }

    @Override
    public void markDirty() {
        syncToTile();
        tile.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
        return tile.getWorld().getTileEntity(tile.getPos()) == tile && tile.getPos().distanceSqToCenter(player.posX, player.posY, player.posZ) <= 64D;
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {}

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        return index == 0 && SpeedCardHelper.isSupported(tile) && stack.getItem() == card;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        stack.set(0, ItemStack.EMPTY);
        setCards.accept(tile, 0);
    }

    @Override
    @MethodsReturnNonnullByDefault
    public String getName() {
        return name;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ITextComponent getDisplayName() {
        return new TextComponentString(getName());
    }

    private void syncFromTile() {
        int cards = getCards.applyAsInt(tile);
        if(cards <= 0) {
            stack.set(0, ItemStack.EMPTY);
            return;
        }
        ItemStack current = stack.get(0);
        if(current.isEmpty() || current.getItem() != card || current.getCount() != cards) {
            stack.set(0, new ItemStack(card, cards));
        }
    }

    private void syncToTile() {
        ItemStack current = stack.get(0);
        setCards.accept(tile, current.isEmpty() ? 0 : current.getCount());
    }
}
