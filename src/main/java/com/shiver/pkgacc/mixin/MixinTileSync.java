package com.shiver.pkgacc.mixin;

import com.shiver.pkgacc.energy.EnergyCardHelper;
import com.shiver.pkgacc.speed.SpeedCardHelper;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thelm.packagedauto.tile.TileBase;

@Pseudo
@Mixin(value = TileBase.class, remap = false)
public abstract class MixinTileSync {

    @Inject(method = "writeSyncNBT", at = @At("RETURN"), remap = false)
    private void PackagedAcceleration$writeCardSync(NBTTagCompound nbt, CallbackInfoReturnable<NBTTagCompound> cir) {
        TileBase tile = (TileBase)(Object)this;
        SpeedCardHelper.writeSync(tile, nbt);
        EnergyCardHelper.writeSync(tile, nbt);
    }

    @Inject(method = "readSyncNBT", at = @At("RETURN"), remap = false)
    private void PackagedAcceleration$readCardSync(NBTTagCompound nbt, CallbackInfo ci) {
        TileBase tile = (TileBase)(Object)this;
        SpeedCardHelper.readSync(tile, nbt);
        EnergyCardHelper.readSync(tile, nbt);
    }
}
