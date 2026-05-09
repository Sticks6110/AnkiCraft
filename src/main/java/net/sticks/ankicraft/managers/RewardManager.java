package net.sticks.ankicraft.managers;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
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
import net.sticks.ankicraft.AnkiCraft;

import java.util.ArrayList;
import java.util.List;

public class RewardManager {
    private static final String[] REWARD_TABLES = {"1", "10", "25", "50", "100", "1000"};
    private static final List<LootTable> LOOT_TABLES = new ArrayList<>();

    private static LootParams PARAMS;

    public static void initialize(MinecraftServer server) {
        LOOT_TABLES.clear();

        for (String table : REWARD_TABLES) {
            ResourceKey<LootTable> rewardKey = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AnkiCraft.MODID, "rewards/" + table));
            LOOT_TABLES.add(server.reloadableRegistries().getLootTable(rewardKey));
        }

        PARAMS = new LootParams.Builder(server.overworld()).create(LootContextParamSets.EMPTY);
    }

    public static ObjectArrayList<ItemStack> rollRewards(int level) {
        if (LOOT_TABLES.isEmpty()) {
            return new ObjectArrayList<>();
        }

        int tableIndex = Math.max(0, Math.min(level - 1, LOOT_TABLES.size() - 1));
        return LOOT_TABLES.get(tableIndex).getRandomItems(PARAMS);
    }

    public static void sendTitleToPlayer(Player player, String title, String subtitle) {
        if (player instanceof ServerPlayer serverPlayer) {
            ClientboundSetTitlesAnimationPacket timesPacket = new ClientboundSetTitlesAnimationPacket(10, 70, 20);

            ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(Component.literal(title));
            ClientboundSetActionBarTextPacket subtitlePacket = new ClientboundSetActionBarTextPacket(Component.literal(subtitle));

            serverPlayer.connection.send(timesPacket);
            serverPlayer.connection.send(subtitlePacket);
            serverPlayer.connection.send(titlePacket);
        }
    }
}
