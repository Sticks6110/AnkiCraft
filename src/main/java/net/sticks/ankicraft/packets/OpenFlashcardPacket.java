package net.sticks.ankicraft.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sticks.ankicraft.AnkiCraft;

public record OpenFlashcardPacket(String question, String a1, String a2, String a3, String a4) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OpenFlashcardPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AnkiCraft.MODID, "open_fc_packet"));

    public static final StreamCodec<ByteBuf, OpenFlashcardPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            OpenFlashcardPacket::question,
            ByteBufCodecs.STRING_UTF8,
            OpenFlashcardPacket::a1,
            ByteBufCodecs.STRING_UTF8,
            OpenFlashcardPacket::a2,
            ByteBufCodecs.STRING_UTF8,
            OpenFlashcardPacket::a3,
            ByteBufCodecs.STRING_UTF8,
            OpenFlashcardPacket::a4,
            OpenFlashcardPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
