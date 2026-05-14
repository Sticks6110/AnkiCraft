package net.sticks.ankicraft.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.sticks.ankicraft.AnkiCraft;

import java.util.HashMap;
import java.util.Map;

public record ChangeToggledDecksPacket(Map<String, Boolean> Decks) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeToggledDecksPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AnkiCraft.MODID, "change_deck_selection_packet"));

    public static final StreamCodec<ByteBuf, ChangeToggledDecksPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    ByteBufCodecs.BOOL
            ),
            ChangeToggledDecksPacket::Decks,
            ChangeToggledDecksPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
