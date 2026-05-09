package net.sticks.ankicraft.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sticks.ankicraft.AnkiCraft;
import net.sticks.ankicraft.packets.SelectAnswerPacket;

import java.util.ArrayList;
import java.util.List;

public class FlashcardScreen extends Screen {
    private static final int CARD_WIDTH = 256;
    private static final int CARD_HEIGHT = 128;
    private static final int BUTTON_WIDTH = 128;
    private static final int BUTTON_HEIGHT = 20;
    private static final int QUESTION_WIDTH = 256;
    private static final int MAX_QUESTION_LINES = 8;
    private static final int QUESTION_LINE_HEIGHT = 16;

    private static final ResourceLocation FLASHCARD_BACKGROUND = ResourceLocation.fromNamespaceAndPath(AnkiCraft.MODID, "textures/gui/index_card.png");
    private static final ResourceLocation BUTTON_SPRITE = ResourceLocation.fromNamespaceAndPath(AnkiCraft.MODID, "textures/gui/button.png");
    private static final ResourceLocation HOVERED_BUTTON_SPRITE = ResourceLocation.fromNamespaceAndPath(AnkiCraft.MODID, "textures/gui/button_highlighted.png");

    private final String question;
    private final List<String> answers;
    private final List<AbstractButton> answerButtons = new ArrayList<>();

    private List<FormattedCharSequence> questionLines = List.of();

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public FlashcardScreen(Component title, String question, List<String> answers) {
        super(title);
        this.question = question;
        this.answers = answers;
    }

    @Override
    protected void init() {
        super.init();

        answerButtons.clear();

        List<FormattedCharSequence> splitQuestion = this.font.split(Component.literal(question), QUESTION_WIDTH);
        questionLines = new ArrayList<>(splitQuestion.subList(0, Math.min(splitQuestion.size(), MAX_QUESTION_LINES)));

        int left = this.width / 2 - BUTTON_WIDTH;
        int right = this.width / 2;
        int top = this.height / 2 + CARD_HEIGHT / 2;

        addAnswerButton(0, left, top);
        addAnswerButton(1, left, top + BUTTON_HEIGHT);
        addAnswerButton(2, right, top);
        addAnswerButton(3, right, top + BUTTON_HEIGHT);
    }

    private void addAnswerButton(int answerIndex, int x, int y) {
        AbstractButton button = Button.builder(Component.literal(answers.get(answerIndex)), btn -> submitAnswer(answerIndex))
                .bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        answerButtons.add(this.addRenderableWidget(button));
    }

    private void submitAnswer(int index) {
        this.onClose();
        PacketDistributor.sendToServer(new SelectAnswerPacket(index));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);

        g.blit(FLASHCARD_BACKGROUND, this.width / 2 - CARD_WIDTH / 2, this.height / 2 - CARD_HEIGHT / 2, 0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_WIDTH, CARD_HEIGHT);

        int y = this.height / 2 - CARD_HEIGHT / 2 + 6;

        for (FormattedCharSequence line : questionLines) {
            g.drawCenteredString(this.font, line, this.width / 2, y, 0x7B6960);
            y += QUESTION_LINE_HEIGHT;
        }

        // The buttons still handle inputs, but we draw the skin ourselves so the UI matches the card art.
        for (AbstractButton button : answerButtons) {
            ResourceLocation sprite = button.isMouseOver(mouseX, mouseY) ? HOVERED_BUTTON_SPRITE : BUTTON_SPRITE;
            g.blit(sprite, button.getX(), button.getY(), 0, 0, BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
            String label = this.font.plainSubstrByWidth(button.getMessage().getString(), BUTTON_WIDTH - 8);
            g.drawCenteredString(this.font, label, button.getX() + BUTTON_WIDTH / 2, button.getY() + 6, 0xFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

}
