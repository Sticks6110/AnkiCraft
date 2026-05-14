package net.sticks.ankicraft;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.sticks.ankicraft.flashcard.ActiveCard;
import net.sticks.ankicraft.managers.DeckManager;
import net.sticks.ankicraft.managers.PlayerManager;
import net.sticks.ankicraft.managers.RewardManager;
import net.sticks.ankicraft.packets.ChangeToggledDecksPacket;
import net.sticks.ankicraft.packets.OpenDecksSelectionPacket;
import net.sticks.ankicraft.packets.OpenFlashcardPacket;
import net.sticks.ankicraft.packets.SelectAnswerPacket;

import java.util.UUID;

public class ModEvents {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(OpenFlashcardPacket.TYPE, OpenFlashcardPacket.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient()) {
                context.enqueueWork(() -> AnkiCraftClient.openFlashcard(payload));
            }
        });

        registrar.playToClient(OpenDecksSelectionPacket.TYPE, OpenDecksSelectionPacket.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient()) {
                context.enqueueWork(() -> AnkiCraftClient.openDecksSelection(payload));
            }
        });

        registrar.playToServer(ChangeToggledDecksPacket.TYPE, ChangeToggledDecksPacket.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist.isClient()) {
                context.enqueueWork(() -> DeckManager.toggleDecks(payload.Decks()));
            }
        });

        registrar.playToServer(SelectAnswerPacket.TYPE, SelectAnswerPacket.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() != null) {
                    ServerPlayer player = (ServerPlayer) context.player();
                    UUID playerId = player.getUUID();
                    ActiveCard activeCard = PlayerManager.ACTIVE_PLAYER_CARDS.get(playerId);

                    if (activeCard == null) {
                        return;
                    }

                    if (activeCard.answerId == payload.answer()) {
                        handleCorrectAnswer(player);
                    } else {
                        handleWrongAnswer(player);
                    }

                    PlayerManager.ACTIVE_PLAYER_CARDS.remove(playerId);
                }
            });
        });
    }

    private static void handleCorrectAnswer(ServerPlayer player) {
        player.playNotifySound(SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.PLAYERS, 1f, 1.5f);
        player.awardStat(Statistics.QUESTIONS_CORRECT);
        player.awardStat(Statistics.QUESTIONS_ROW_CURRENT);

        int correct = player.getStats().getValue(Stats.CUSTOM.get(Statistics.QUESTIONS_CORRECT));
        int currentStreak = player.getStats().getValue(Stats.CUSTOM.get(Statistics.QUESTIONS_ROW_CURRENT));
        int bestStreak = player.getStats().getValue(Stats.CUSTOM.get(Statistics.QUESTIONS_ROW_MAX));

        if (currentStreak > bestStreak) {
            player.awardStat(Statistics.QUESTIONS_ROW_MAX, currentStreak - bestStreak);
            bestStreak = currentStreak;
        }

        RewardManager.sendTitleToPlayer(player, String.valueOf(correct), currentStreak + " Streak | " + bestStreak + " Highest Streak");

        for (ItemStack stack : RewardManager.rollRewards(getRewardLevel(correct))) {
            player.addItem(stack);
        }
    }

    private static void handleWrongAnswer(ServerPlayer player) {
        player.playNotifySound(SoundEvents.AMBIENT_CAVE.value(), SoundSource.PLAYERS, 1f, 1f);
        player.awardStat(Statistics.QUESTIONS_WRONG);
        player.resetStat(Stats.CUSTOM.get(Statistics.QUESTIONS_ROW_CURRENT));
    }

    private static int getRewardLevel(int correctAnswers) {
        if (correctAnswers % 1000 == 0) {
            return 6;
        } else if (correctAnswers % 100 == 0) {
            return 5;
        } else if (correctAnswers % 50 == 0) {
            return 4;
        } else if (correctAnswers % 25 == 0) {
            return 3;
        } else if (correctAnswers % 10 == 0) {
            return 2;
        }

        return 1;
    }
}
