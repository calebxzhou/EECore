package com.endlessepoch.core.registry;

import com.endlessepoch.core.EECore;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Items {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, EECore.MOD_ID);

    // ===== 方块对应的物品 =====
    public static final Supplier<BlockItem> CREATIVE_GENERATOR_ITEM =
            ITEMS.register("creative_generator",
                    () -> new BlockItem(Blocks.CREATIVE_GENERATOR.get(),
                            new Item.Properties().stacksTo(64))
            );

    public static final Supplier<BlockItem> CREATIVE_CONSUMER_ITEM =
            ITEMS.register("creative_consumer",
                    () -> new BlockItem(Blocks.CREATIVE_CONSUMER.get(),
                            new Item.Properties().stacksTo(64))
            );
}