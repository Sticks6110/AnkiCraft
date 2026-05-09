package net.sticks.ankicraft.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sticks.ankicraft.AnkiCraft;

public record SelectAnswerPacket(int answer) implements CustomPacketPayload {
    public static final SelectAnswerPacket.Type<SelectAnswerPacket> TYPE = new SelectAnswerPacket.Type<>(ResourceLocation.fromNamespaceAndPath(AnkiCraft.MODID, "answer_fc_packet"));

    public static final StreamCodec<ByteBuf, SelectAnswerPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            SelectAnswerPacket::answer,
            SelectAnswerPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
