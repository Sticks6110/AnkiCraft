package net.sticks.ankicraft.managers;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sticks.ankicraft.Config;
import net.sticks.ankicraft.flashcard.ActiveCard;
import net.sticks.ankicraft.flashcard.Flashcard;
import net.sticks.ankicraft.packets.OpenFlashcardPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private static final int TICKS_PER_SECOND = 20;

    private static final Map<UUID, Integer> PLAYER_TIMERS = new HashMap<>();

    public static final Map<UUID, ActiveCard> ACTIVE_PLAYER_CARDS = new HashMap<>();

    private static int questionInterval;

    public static void initialize() {
        updateQuestionInterval();
    }

    public static void onConfigChange() {
        updateQuestionInterval();
    }

    public static void tick(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            UUID id = player.getUUID();

            if (ACTIVE_PLAYER_CARDS.containsKey(id)) {
                continue;
            }

            int time = PLAYER_TIMERS.getOrDefault(id, 0) + 1;

            if (time >= questionInterval) {
                time = 0;

                Flashcard card = DeckManager.getRandomCard();

                if(card != null) {
                    // Shuffle a copy so the deck's stored answer order stays stable.
                    List<String> answers = new ArrayList<>(List.of(card.Answers));
                    Collections.shuffle(answers);
                    int correctIndex = answers.indexOf(card.Answers[card.CorrectAnswer]);

                    ACTIVE_PLAYER_CARDS.put(id, new ActiveCard(correctIndex));

                    PacketDistributor.sendToPlayer(player, new OpenFlashcardPacket(card.Question, answers.get(0), answers.get(1), answers.get(2), answers.get(3)));
                }
            }

            PLAYER_TIMERS.put(id, time);
        }
    }

    private static void updateQuestionInterval() {
        questionInterval = Config.QUESTION_INTERVAL.getAsInt() * TICKS_PER_SECOND;
    }
}
