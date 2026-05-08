package net.sticks.ankicraft.managers;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.ArrayList;
import java.util.List;

public class RewardManager {
    private static List<LootTable> LootTables = new ArrayList<>();
    private static LootParams PARAMS;
    public static void Initialize(MinecraftServer server) {
        ResourceKey<LootTable> loot1 = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("ankicraft", "rewards/1"));
        LootTables.add(server.reloadableRegistries().getLootTable(loot1));

        ResourceKey<LootTable> loot10 = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("ankicraft", "rewards/10"));
        LootTables.add(server.reloadableRegistries().getLootTable(loot10));

        ResourceKey<LootTable> loot25 = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("ankicraft", "rewards/25"));
        LootTables.add(server.reloadableRegistries().getLootTable(loot25));

        ResourceKey<LootTable> loot50 = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("ankicraft", "rewards/50"));
        LootTables.add(server.reloadableRegistries().getLootTable(loot50));

        ResourceKey<LootTable> loot100 = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("ankicraft", "rewards/100"));
        LootTables.add(server.reloadableRegistries().getLootTable(loot100));

        ResourceKey<LootTable> loot1000 = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("ankicraft", "rewards/1000"));
        LootTables.add(server.reloadableRegistries().getLootTable(loot1000));

        PARAMS = new LootParams.Builder(server.overworld()).create(LootContextParamSets.EMPTY);
    }

    public static ObjectArrayList<ItemStack> RollRewards(int level) {
        return LootTables.get(level - 1).getRandomItems(PARAMS);
    }

    public static void sendTitleToPlayer(Player player, String title, String subtitle) {
        if (player instanceof ServerPlayer serverPlayer) {
            // Set times (Fade In, Stay, Fade Out) in ticks (20 ticks = 1 second)
            ClientboundSetTitlesAnimationPacket timesPacket =
                    new ClientboundSetTitlesAnimationPacket(10, 70, 20);

            // Title and Subtitle packets
            ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(Component.literal(title));
            ClientboundSetActionBarTextPacket subtitlePacket = new ClientboundSetActionBarTextPacket(Component.literal(subtitle));

            // Send packets
            serverPlayer.connection.send(timesPacket);
            serverPlayer.connection.send(subtitlePacket);
            serverPlayer.connection.send(titlePacket);
        }
    }
}
