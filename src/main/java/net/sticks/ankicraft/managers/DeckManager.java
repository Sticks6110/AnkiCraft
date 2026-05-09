package net.sticks.ankicraft.managers;

import com.google.gson.Gson;
import net.minecraft.util.RandomSource;
import net.neoforged.fml.loading.FMLPaths;
import net.sticks.ankicraft.AnkiCraft;
import net.sticks.ankicraft.flashcard.Deck;
import net.sticks.ankicraft.flashcard.Flashcard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeckManager {
    public static final Map<String, Deck> DECKS = new HashMap<>();

    private static final Gson GSON = new Gson();
    private static final RandomSource RANDOM = RandomSource.create();
    private static final File DECK_FOLDER = FMLPaths.GAMEDIR.get().resolve("decks").toFile();

    public static void initialize() {
        createFolder();
        DECKS.clear();

        String[] deckNames = getDeckNames();
        Deck[] decks = getDeckObjects(deckNames);
        for (Deck deck : decks) {
            if (deck.ID == null || deck.Cards == null) {
                AnkiCraft.LOGGER.warn("Skipped a deck with missing ID or cards.");
                continue;
            }

            DECKS.put(deck.ID, deck);
        }

        AnkiCraft.LOGGER.info("Loaded {} deck(s) with {} total card(s).", DECKS.size(), getCardCount());
    }

    public static String[] getDeckNames() {
        if (!DECK_FOLDER.exists()) {
            return new String[0];
        }

        String[] decks = DECK_FOLDER.list((dir, name) -> name.endsWith(".deck"));
        if (decks == null) {
            AnkiCraft.LOGGER.warn("Could not read deck folder: {}", DECK_FOLDER.getPath());
            return new String[0];
        }

        for (int i = 0; i < decks.length; i++) {
            decks[i] = decks[i].replace(".deck", "");
            AnkiCraft.LOGGER.info("Found deck: {}", decks[i]);
        }

        return decks;
    }

    public static void createFolder() {
        if (!DECK_FOLDER.exists()) {
            if (DECK_FOLDER.mkdirs()) {
                AnkiCraft.LOGGER.info("Created deck folder: {}", DECK_FOLDER.getPath());
            } else {
                AnkiCraft.LOGGER.warn("Failed to create deck folder: {}", DECK_FOLDER.getPath());
            }
        }
    }

    public static Deck[] getDeckObjects(String[] names) {
        List<Deck> decks = new ArrayList<>();

        for (String name : names) {
            File deckFile = new File(DECK_FOLDER, name + ".deck");
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(deckFile), StandardCharsets.UTF_8)) {
                Deck deck = GSON.fromJson(reader, Deck.class);
                if (deck != null) {
                    decks.add(deck);
                }
            } catch (IOException e) {
                AnkiCraft.LOGGER.warn("Failed to load deck file: {}", deckFile.getPath(), e);
            }
        }

        return decks.toArray(new Deck[0]);
    }

    public static Flashcard getRandomCard() {
        //TODO: Precompile
        List<Flashcard> cards = new ArrayList<>();

        for (Deck deck : DECKS.values()) {
            if (deck.Cards == null) {
                continue;
            }

            for (Flashcard card : deck.Cards) {
                if (card != null && isPlayable(card)) {
                    cards.add(card);
                }
            }
        }

        if (cards.isEmpty()) {
            return null;
        }

        return cards.get(RANDOM.nextInt(cards.size()));
    }

    public static int getCardCount() {
        int amount = 0;

        for (Deck deck : DECKS.values()) {
            if (deck.Cards != null) {
                amount += deck.Cards.length;
            }
        }

        return amount;
    }

    public static boolean isPlayable(Flashcard card) {
        if (card == null || card.Question == null || card.Answers == null || card.Answers.length != 4 || card.CorrectAnswer == null) {
            return false;
        }

        for (String answer : card.Answers) {
            if (answer == null) {
                return false;
            }

            if (card.CorrectAnswer.equals(answer)) {
                return true;
            }
        }

        return false;
    }
}
