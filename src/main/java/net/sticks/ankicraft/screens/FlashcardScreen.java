package net.sticks.ankicraft.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sticks.ankicraft.packets.SelectAnswerPacket;

import java.util.ArrayList;
import java.util.List;

public class FlashcardScreen extends Screen {

    private final String question;
    private List<FormattedCharSequence> lines;
    private final List<String> answers;

    private static final ResourceLocation FlashCardBackground = ResourceLocation.fromNamespaceAndPath("ankicraft", "textures/gui/index_card.png");
    private static final ResourceLocation ButtonSprite = ResourceLocation.fromNamespaceAndPath("ankicraft", "textures/gui/button.png");
    private static final ResourceLocation DButtonSprite = ResourceLocation.fromNamespaceAndPath("ankicraft", "textures/gui/button_disabled.png");
    private static final ResourceLocation HButtonSprite = ResourceLocation.fromNamespaceAndPath("ankicraft", "textures/gui/button_highlighted.png");

    private AbstractButton Button1;
    private AbstractButton Button2;
    private AbstractButton Button3;
    private AbstractButton Button4;

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

        List<FormattedCharSequence> split = this.font.split(Component.literal(question), 256);
        lines = new ArrayList<>(split.subList(0, Math.min(split.size(), 8)));

        Button1 = this.addRenderableWidget(Button.builder(Component.literal(answers.get(0)), btn -> submitAnswer(0)).bounds(this.width / 2 - 128, this.height / 2 + 64, 128, 20).build());
        Button2 = this.addRenderableWidget(Button.builder(Component.literal(answers.get(1)), btn -> submitAnswer(1)).bounds(this.width / 2 - 128, this.height / 2 + 64 + 20, 128, 20).build());
        Button3 = this.addRenderableWidget(Button.builder(Component.literal(answers.get(2)), btn -> submitAnswer(2)).bounds(this.width / 2, this.height / 2 + 64, 128, 20).build());
        Button4 = this.addRenderableWidget(Button.builder(Component.literal(answers.get(3)), btn -> submitAnswer(3)).bounds(this.width / 2, this.height / 2 + 64 + 20, 128, 20).build());
    }

    private void submitAnswer(int index) {
        this.onClose();
        PacketDistributor.sendToServer(new SelectAnswerPacket(index));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);

        g.blit(FlashCardBackground, this.width / 2 - 128, this.height / 2 - 64, 0, 0, 256, 128, 256, 128);

        int y = this.height / 2 - 64 + 6;

        for (FormattedCharSequence line : lines) {
            g.drawCenteredString(this.font, line, this.width / 2, y, 0x7B6960);
            y += 16;
        }

        //super.render(g, mouseX, mouseY, partialTick);
        for(Renderable renderable : this.renderables) {
            renderable.render(g, mouseX, mouseY, partialTick);
        }

        g.blit((Button1.isHovered()) ? HButtonSprite : ButtonSprite, this.width / 2 - 128, this.height / 2 + 64, 0, 0, 128, 20, 128, 20);
        g.blit((Button2.isHovered()) ? HButtonSprite : ButtonSprite, this.width / 2 - 128, this.height / 2 + 64 + 20, 0, 0, 128, 20, 128, 20);
        g.blit((Button3.isHovered()) ? HButtonSprite : ButtonSprite, this.width / 2, this.height / 2 + 64, 0, 0, 128, 20, 128, 20);
        g.blit((Button4.isHovered()) ? HButtonSprite : ButtonSprite, this.width / 2, this.height / 2 + 64 + 20, 0, 0, 128, 20, 128, 20);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

}
