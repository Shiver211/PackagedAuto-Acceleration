package com.shiver.pkgacc.mixin;

import com.shiver.pkgacc.energy.EnergyCardHelper;
import com.shiver.pkgacc.speed.SpeedCardHelper;
import net.minecraft.util.ITickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thelm.packagedauto.tile.TileBase;

import java.util.Map;
import java.util.WeakHashMap;

@Pseudo
@Mixin(targets = {
        "thelm.packagedauto.tile.TileCrafter",
        "thelm.packagedauto.tile.TilePackager",
        "thelm.packagedauto.tile.TilePackagerExtension",
        "thelm.packagedavaritia.tile.TileExtremeCrafter",
        "thelm.packagedexcrafting.tile.TileBasicCrafter",
        "thelm.packagedexcrafting.tile.TileAdvancedCrafter",
        "thelm.packagedexcrafting.tile.TileEliteCrafter",
        "thelm.packagedexcrafting.tile.TileUltimateCrafter",
        "thelm.packagedexcrafting.tile.TileCombinationCrafter",
        "thelm.packagedexcrafting.tile.TileEnderCrafter",
        "thelm.packagedastral.tile.TileDiscoveryCrafter",
        "thelm.packagedastral.tile.TileAttunementCrafter",
        "thelm.packagedastral.tile.TileConstellationCrafter",
        "thelm.packagedastral.tile.TileTraitCrafter",
        "thelm.packageddraconic.tile.TileFusionCrafter",
        "thelm.packagedthaumic.tile.TileArcaneCrafter",
        "thelm.packagedthaumic.tile.TileVirialArcaneCrafter",
        "thelm.packagedthaumic.tile.TileCrucibleCrafter",
        "thelm.packagedthaumic.tile.TileInfusionCrafter"
}, remap = false)
public abstract class MixinTileUpdate {

    private static final Map<TileBase, Double> PackagedAcceleration$extraTickProgress = new WeakHashMap<>();
    private static final ThreadLocal<Boolean> PackagedAcceleration$runningExtraTicks = ThreadLocal.withInitial(()->false);

    @Inject(method = "update", at = @At("RETURN"), remap = false)
    private void PackagedAcceleration$runExtraUpdates(CallbackInfo ci) {
        if(PackagedAcceleration$runningExtraTicks.get()) {
            return;
        }
        TileBase tile = (TileBase)(Object)this;
        if(tile.getWorld() == null || tile.getWorld().isRemote || tile.isInvalid()) {
            return;
        }
        if(EnergyCardHelper.isSupported(tile)) {
            EnergyCardHelper.applyCapacity(tile);
        }
        int extraTicks = PackagedAcceleration$getExtraTicks(tile);
        if(extraTicks <= 0) {
            return;
        }
        PackagedAcceleration$runningExtraTicks.set(true);
        try {
            ITickable tickable = (ITickable) this;
            for(int i = 0; i < extraTicks && !tile.isInvalid(); ++i) {
                tickable.update();
            }
        }
        finally {
            PackagedAcceleration$runningExtraTicks.set(false);
        }
    }

    private int PackagedAcceleration$getExtraTicks(TileBase tile) {
        if(!SpeedCardHelper.isSupported(tile)) {
            PackagedAcceleration$extraTickProgress.remove(tile);
            return 0;
        }
        int cards = SpeedCardHelper.getCards(tile);
        if(cards <= 0) {
            PackagedAcceleration$extraTickProgress.remove(tile);
            return 0;
        }
        double progress = PackagedAcceleration$extraTickProgress.getOrDefault(tile, 0D)+SpeedCardHelper.getMultiplier(cards)-1D;
        int ticks = (int)progress;
        progress -= ticks;
        if(progress > 0D) {
            PackagedAcceleration$extraTickProgress.put(tile, progress);
        }
        else {
            PackagedAcceleration$extraTickProgress.remove(tile);
        }
        return ticks;
    }
}
