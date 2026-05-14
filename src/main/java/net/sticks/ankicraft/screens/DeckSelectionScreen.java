package net.sticks.ankicraft.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.StringUtil;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sticks.ankicraft.AnkiCraft;
import net.sticks.ankicraft.flashcard.Deck;
import net.sticks.ankicraft.managers.DeckManager;
import net.sticks.ankicraft.packets.ChangeToggledDecksPacket;
import net.sticks.ankicraft.packets.OpenDecksSelectionPacket;

import java.util.Comparator;
import java.util.Map;

public class DeckSelectionScreen extends Screen {
    private static final int LIST_WIDTH = 280;
    private static final int ENTRY_HEIGHT = 26;
    private static final int FOOTER_HEIGHT = 32;

    private final Map<String, Boolean> decks;

    private DeckListWidget deckList;

    public DeckSelectionScreen(Component title, Map<String, Boolean> decks) {
        super(title);
        this.decks = decks;
    }

    @Override
    protected void init() {
        super.init();

        int listWidth = Math.min(LIST_WIDTH, Math.max(120, this.width - 40));
        int top = 32;
        int bottom = this.height - FOOTER_HEIGHT;

        this.deckList = this.addRenderableWidget(new DeckListWidget(this.minecraft, listWidth, top, bottom));
        this.deckList.setX((this.width - listWidth) / 2);
        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> DoneSelected()).bounds(this.width / 2 - (LIST_WIDTH / 2) + 10, this.height - 24, LIST_WIDTH - 20, 20).build());
    }

    public void DoneSelected() {
        PacketDistributor.sendToServer(new ChangeToggledDecksPacket(this.decks));
        this.onClose();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(this.font, this.title, this.width / 2, 14, 0xFFFFFF);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean isDeckEnabled(String deckId) {
        return this.decks.getOrDefault(deckId, false);
    }

    private void toggleDeck(String deckId) {
        this.decks.put(deckId, !isDeckEnabled(deckId));
    }

    public class DeckListWidget extends ObjectSelectionList<DeckEntry> {
        private final int listWidth;

        public DeckListWidget(Minecraft minecraft, int listWidth, int top, int bottom) {
            super(minecraft, listWidth, bottom - top, top, ENTRY_HEIGHT);
            this.listWidth = listWidth;
            refreshList();
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getX() + this.listWidth - 6;
        }

        @Override
        public int getRowWidth() {
            return this.listWidth;
        }

        public void refreshList() {
            this.clearEntries();
            DeckManager.DECKS.entrySet().stream()
                    .sorted(Comparator.comparing(entry -> getDeckName(entry.getKey(), entry.getValue()), String.CASE_INSENSITIVE_ORDER))
                    .forEach(entry -> this.addEntry(new DeckEntry(entry.getKey(), entry.getValue())));
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

            if (this.getItemCount() == 0) {
                guiGraphics.drawCenteredString(DeckSelectionScreen.this.font, "No decks found", this.getX() + this.getWidth() / 2, this.getY() + 10, 0xAAAAAA);
            }
        }
    }

    public class DeckEntry extends ObjectSelectionList.Entry<DeckEntry> {
        private final String deckId;
        private final Deck deck;

        public DeckEntry(String deckId, Deck deck) {
            this.deckId = deckId;
            this.deck = deck;
        }

        @Override
        public Component getNarration() {
            return Component.translatable("narrator.select", getDeckName(this.deckId, this.deck));
        }

        @Override
        public void render(GuiGraphics guiGraphics, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            Font font = DeckSelectionScreen.this.font;
            boolean enabled = isDeckEnabled(this.deckId);
            int textWidth = entryWidth - 24;
            Component name = Component.literal(stripControlCodes(getDeckName(this.deckId, this.deck)));
            Component detail = Component.literal(getDeckDetail(this.deck, enabled));

            guiGraphics.drawString(font, enabled ? "[x]" : "[ ]", left + 3, top + 8, enabled ? 0x55FF55 : 0xAAAAAA, false);
            guiGraphics.drawString(font, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(name, textWidth))), left + 25, top + 3, 0xFFFFFF, false);
            guiGraphics.drawString(font, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(detail, textWidth))), left + 25, top + 3 + font.lineHeight, 0xCCCCCC, false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            toggleDeck(this.deckId);
            DeckSelectionScreen.this.deckList.setSelected(this);
            return true;
        }

        @Override
        public void setFocused(boolean focused) {
            if (focused) {
                DeckSelectionScreen.this.deckList.setSelected(this);
            }
        }

        @Override
        public boolean isFocused() {
            return DeckSelectionScreen.this.deckList.getSelected() == this;
        }
    }

    private static String stripControlCodes(String value) {
        return StringUtil.stripColor(value == null ? "" : value);
    }

    private static String getDeckName(String deckId, Deck deck) {
        if (deck != null && deck.Name != null && !deck.Name.isBlank()) {
            return deck.Name;
        }

        return deckId;
    }

    private static String getDeckDetail(Deck deck, boolean enabled) {
        String status = enabled ? "Enabled" : "Disabled";

        if (deck == null || deck.Cards == null) {
            return status + " - 0 cards";
        }

        String cardCount = deck.Cards.length == 1 ? "1 card" : deck.Cards.length + " cards";
        return status + " - " + cardCount;
    }
}
