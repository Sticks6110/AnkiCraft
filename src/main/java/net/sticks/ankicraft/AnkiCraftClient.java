package net.sticks.ankicraft;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.sticks.ankicraft.packets.OpenFlashcardPacket;
import net.sticks.ankicraft.screens.FlashcardScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod(value = AnkiCraft.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AnkiCraft.MODID, value = Dist.CLIENT)
public class AnkiCraftClient {
    public AnkiCraftClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }

    public static void openFlashcard(OpenFlashcardPacket payload) {
        List<String> answers = new ArrayList<>(Arrays.asList(payload.a1(), payload.a2(), payload.a3(), payload.a4()));
        Minecraft.getInstance().setScreen(new FlashcardScreen(Component.literal("Question"), payload.question(), answers));
    }
}
