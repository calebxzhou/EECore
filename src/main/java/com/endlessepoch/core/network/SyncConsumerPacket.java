package com.endlessepoch.core.network;

import com.endlessepoch.core.EECore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.math.BigInteger;

public record SyncConsumerPacket(
        BlockPos pos,
        BigInteger storedEnergy,
        BigInteger totalReceived,
        boolean logToChat
) implements CustomPacketPayload {

    public static final Type<SyncConsumerPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(EECore.MOD_ID, "sync_consumer"));

    public static final StreamCodec<FriendlyByteBuf, SyncConsumerPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    SyncConsumerPacket::pos,
                    NetworkCodecs.BIG_INTEGER,
                    SyncConsumerPacket::storedEnergy,
                    NetworkCodecs.BIG_INTEGER,
                    SyncConsumerPacket::totalReceived,
                    ByteBufCodecs.BOOL,
                    SyncConsumerPacket::logToChat,
                    SyncConsumerPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}