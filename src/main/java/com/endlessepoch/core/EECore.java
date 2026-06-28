package com.endlessepoch.core;

import com.endlessepoch.core.api.EECoreCapabilities;
import com.endlessepoch.core.network.SyncConsumerPacket;
import com.endlessepoch.core.network.SyncGeneratorPacket;
import com.endlessepoch.core.registry.BlockEntities;
import com.endlessepoch.core.registry.Blocks;
import com.endlessepoch.core.registry.Items;
import com.endlessepoch.core.registry.Menus;
import com.endlessepoch.core.blockentity.creative.CreativeConsumerBlockEntity;
import com.endlessepoch.core.blockentity.creative.CreativeGeneratorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.function.Supplier;

@Mod(EECore.MOD_ID)
public class EECore {

    public static final String MOD_ID = "eecore";
    public static final String MOD_NAME = "Endless Epoch Core";
    public static final String VERSION = "0.1.0";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final Supplier<CreativeModeTab> EECORE_TAB = CREATIVE_TABS.register(
            "eecore_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.eecore"))
                    .icon(() -> Items.CREATIVE_GENERATOR_ITEM.get().getDefaultInstance())
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .displayItems((params, output) -> {
                        output.accept(Items.CREATIVE_GENERATOR_ITEM.get());
                        output.accept(Items.CREATIVE_CONSUMER_ITEM.get());
                    })
                    .build()
    );

    public EECore(IEventBus modEventBus, ModContainer container) {
        LOGGER.info(MOD_NAME + " v" + VERSION + " 加载中...");

        Blocks.BLOCKS.register(modEventBus);
        Items.ITEMS.register(modEventBus);
        BlockEntities.BLOCK_ENTITIES.register(modEventBus);
        Menus.MENUS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerPayloadHandlers);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info(MOD_NAME + " 初始化完成");
        LOGGER.info("能量单位: Ω (1Ω = 2FE)");
        LOGGER.info("电压等级: ELV · LV · MV · HV · EHV · UHV · PHV · XHV · PLV · SV · BV · QV");
        LOGGER.info("创造机器已注册");
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                EECoreCapabilities.OMEGA_ENERGY,
                BlockEntities.CREATIVE_GENERATOR.get(),
                (be, side) -> be
        );
        event.registerBlockEntity(
                EECoreCapabilities.OMEGA_ENERGY,
                BlockEntities.CREATIVE_CONSUMER.get(),
                (be, side) -> be
        );
    }

    private void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // Generator sync
        registrar.playToClient(
                SyncGeneratorPacket.TYPE,
                SyncGeneratorPacket.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        var level = context.player().level();
                        var be = level.getBlockEntity(payload.pos());
                        if (be instanceof CreativeGeneratorBlockEntity genBe) {
                            genBe.updateFromSync(payload);
                            level.sendBlockUpdated(payload.pos(), be.getBlockState(), be.getBlockState(), 3);
                        }
                    });
                }
        );

        // Consumer sync
        registrar.playToClient(
                SyncConsumerPacket.TYPE,
                SyncConsumerPacket.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        var level = context.player().level();
                        var be = level.getBlockEntity(payload.pos());
                        if (be instanceof CreativeConsumerBlockEntity consumerBe) {
                            consumerBe.updateFromSync(payload);
                            level.sendBlockUpdated(payload.pos(), be.getBlockState(), be.getBlockState(), 3);
                        }
                    });
                }
        );
    }
}