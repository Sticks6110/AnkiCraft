package net.sticks.ankicraft;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.sticks.ankicraft.flashcard.ActiveCard;
import net.sticks.ankicraft.managers.PlayerManager;
import net.sticks.ankicraft.packets.OpenFlashcardPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.sticks.ankicraft.packets.SelectAnswerPacket;
import net.sticks.ankicraft.managers.RewardManager;
import net.sticks.ankicraft.screens.FlashcardScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ModEvents {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(OpenFlashcardPacket.TYPE, OpenFlashcardPacket.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                Minecraft.getInstance().setScreen(
                        new FlashcardScreen(Component.literal("Question"), payload.question(), new ArrayList<>(Arrays.asList(payload.a1(), payload.a2(), payload.a3(), payload.a4())))
                );
            });
        });

        registrar.playToServer(SelectAnswerPacket.TYPE, SelectAnswerPacket.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() != null) {
                    ServerPlayer player = (ServerPlayer) context.player();
                    UUID playerID = context.player().getUUID();

                    if(PlayerManager.ActivePlayerCards.getOrDefault(playerID, new ActiveCard(-1)).AnswerID == payload.answer()) {
                        player.playNotifySound(SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.PLAYERS, 1f, 1.5f);

                        player.awardStat(Statistics.QUESTIONS_CORRECT);
                        player.awardStat(Statistics.QUESTIONS_ROW_CURRENT);
                        int correct = player.getStats().getValue(Stats.CUSTOM.get(Statistics.QUESTIONS_CORRECT));
                        int current_row = player.getStats().getValue(Stats.CUSTOM.get(Statistics.QUESTIONS_ROW_CURRENT));
                        int row_max = player.getStats().getValue(Stats.CUSTOM.get(Statistics.QUESTIONS_ROW_MAX));
                        int wrong = player.getStats().getValue(Stats.CUSTOM.get(Statistics.QUESTIONS_WRONG));

                        if(current_row > row_max) {
                            player.awardStat(Statistics.QUESTIONS_ROW_MAX, current_row - row_max);
                            row_max = current_row;
                        }

                        RewardManager.sendTitleToPlayer(context.player(), String.valueOf(correct), String.valueOf(current_row) + " Streak : " + String.valueOf(row_max) + " Highest Streak");

                        int level = 1;
                        if(correct % 1000 == 0) {
                            level = 6;
                        } else if(correct % 100 == 0) {
                            level = 5;
                        } else if(correct % 50 == 0) {
                            level = 4;
                        } else if(correct % 25 == 0) {
                            level = 3;
                        } else if(correct % 10 == 0) {
                            level = 2;
                        }

                        var items = RewardManager.RollRewards(level);

                        for (ItemStack stack : items) {
                            context.player().addItem(stack);
                        }
                    } else {
                        context.player().playNotifySound(SoundEvents.AMBIENT_CAVE.value(), SoundSource.PLAYERS, 1f, 1f);
                        player.awardStat(Statistics.QUESTIONS_WRONG);
                        player.resetStat(Stats.CUSTOM.get(Statistics.QUESTIONS_ROW_CURRENT));
                    }

                    PlayerManager.ActivePlayerCards.remove(playerID);
                }
            });
        });
    }
}
