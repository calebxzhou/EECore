package com.endlessepoch.core.event;

import com.endlessepoch.core.EECore;
import com.endlessepoch.core.registry.Menus;
import com.endlessepoch.core.screen.creative.CreativeConsumerScreen;
import com.endlessepoch.core.screen.creative.CreativeGeneratorScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = EECore.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(Menus.CREATIVE_GENERATOR.get(), CreativeGeneratorScreen::new);
        event.register(Menus.CREATIVE_CONSUMER.get(), CreativeConsumerScreen::new);
    }
}