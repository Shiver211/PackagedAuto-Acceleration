package com.shiver.pkgacc.client;

import com.shiver.packaged_acceleration.Tags;
import com.shiver.pkgacc.item.ItemEnergyCard;
import com.shiver.pkgacc.item.ItemSpeedCard;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID, value = Side.CLIENT)
public class ClientEventHandler {

    private ClientEventHandler() {}

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ItemSpeedCard.INSTANCE.registerModel();
        ItemEnergyCard.INSTANCE.registerModel();
    }
}
