package net.sticks.ankicraft.managers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sticks.ankicraft.Config;
import net.sticks.ankicraft.flashcard.ActiveCard;
import net.sticks.ankicraft.flashcard.Flashcard;
import net.sticks.ankicraft.packets.OpenFlashcardPacket;

import java.lang.reflect.Array;
import java.util.*;

public class PlayerManager {
    private static Map<UUID, Integer> PlayerTimers = new HashMap<>();
    public static Map<UUID, ActiveCard> ActivePlayerCards = new HashMap<>();
    private static int questionInterval;
    private static final RandomSource RANDOM = RandomSource.create();

    public static void Initialize() {
        questionInterval = Config.QUESTION_INTERVAL.getAsInt() * 20;
    }

    public static void ConfigChange() {
        questionInterval = Config.QUESTION_INTERVAL.getAsInt() * 20;
    }

    public static void Tick(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            UUID id = player.getUUID();

            if(ActivePlayerCards.getOrDefault(id, new ActiveCard(-1)).AnswerID != -1) {
                continue;
            }

            int time = PlayerTimers.getOrDefault(id, 0) + 1;

            if(time >= questionInterval) {
                time = 0;
                Flashcard card = DeckManager.GetRandomCard();
                List<String> answers = Arrays.asList(card.Answers);
                Collections.shuffle(answers);
                int correct_index = answers.indexOf(card.CorrectAnswer);
                ActivePlayerCards.put(id, new ActiveCard(correct_index));

                PacketDistributor.sendToPlayer(player, new OpenFlashcardPacket(card.Question,answers.get(0),answers.get(1),answers.get(2),answers.get(3)));
            }

            PlayerTimers.put(id, time);
        }
    }
}
