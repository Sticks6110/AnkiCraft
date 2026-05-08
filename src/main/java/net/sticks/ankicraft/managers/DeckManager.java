package net.sticks.ankicraft.managers;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.sticks.ankicraft.AnkiCraft;
import net.sticks.ankicraft.flashcard.Deck;
import net.sticks.ankicraft.flashcard.Flashcard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DeckManager {
    public static Map<String, Deck> Decks = new HashMap<>();
    private static final File deckFolder = new File(Minecraft.getInstance().gameDirectory, "decks");
    private static final RandomSource RANDOM = RandomSource.create();

    private static String[] decks_on_file;

    public static void Initialize() {
        DeckManager.createFolder();
        decks_on_file = DeckManager.getDecks();
        AnkiCraft.LOGGER.info("DECKS ACQUIRED");
        Deck[] decks = DeckManager.getDeckObjects(decks_on_file);
        for (Deck deck : decks) {
            Decks.put(deck.ID, deck);
        }
        AnkiCraft.LOGGER.info("DECK OBJECTS ACQUIRED");
    }


    public static String[] getDecks() {
        if(!deckFolder.exists())
        {
            return new String[0];
        }

        String[] decks = deckFolder.list((dir, name) -> name.endsWith(".deck"));

        for(int i = 0; i < decks.length; i++)
        {
            decks[i] = decks[i].replace(".deck", "");
            AnkiCraft.LOGGER.info("Found deck: " + decks[i]);
        }

        return decks;

    }

    public static void createFolder() {
        if (!deckFolder.exists()) {
            if (deckFolder.mkdirs()) {
                AnkiCraft.LOGGER.info("Folder created: " + deckFolder.getPath());
            } else {
                AnkiCraft.LOGGER.info("Failed to create folder.");
            }
        }
    }

    public static Deck[] getDeckObjects(String[] names) {
        AnkiCraft.LOGGER.info("LOADING DECK OBJECTS");
        List<Deck> decks = new ArrayList<>();

        for (String name : names) {

            Gson gson = new Gson();

            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(deckFolder, name + ".deck")), StandardCharsets.UTF_8)) {
                //Deck deserializedDeck = mapper.readValue(new File(deckFolder, name + ".deck"), Deck.class);
                Deck deserializedDeck = gson.fromJson(reader, Deck.class);
                AnkiCraft.LOGGER.info(deserializedDeck.ID);
                decks.add(deserializedDeck);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        AnkiCraft.LOGGER.info("DONE LOADING DECK OBJECTS");
        return decks.toArray(new Deck[decks.size()]);
    }

    public static Flashcard GetRandomCard() {
        if(Decks.size() == 0 || GetCardCount() == 0) return null;
        Deck deck = Decks.get(Decks.keySet().toArray()[RANDOM.nextInt(Decks.size())]);

        if(deck.Cards.length == 0) return GetRandomCard();

        return deck.Cards[RANDOM.nextInt(deck.Cards.length)];

    }

    public static int GetCardCount() {
        int amount = 0;
        for (String s : Decks.keySet()) {
            amount += Decks.get(s).Cards.length;
        }
        return amount;
    }

}
