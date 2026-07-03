package com.shiver.pkgacc.event;

import com.shiver.packaged_acceleration.Tags;
import com.shiver.pkgacc.item.ItemEnergyCard;
import com.shiver.pkgacc.item.ItemSpeedCard;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class RegistryEventHandler {

    private RegistryEventHandler() {}

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ItemSpeedCard.INSTANCE);
        event.getRegistry().register(ItemEnergyCard.INSTANCE);
    }
}
