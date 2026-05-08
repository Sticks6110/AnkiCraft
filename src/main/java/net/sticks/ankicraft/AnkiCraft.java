package net.sticks.ankicraft;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sticks.ankicraft.flashcard.ActiveCard;
import net.sticks.ankicraft.flashcard.Deck;
import net.sticks.ankicraft.managers.DeckManager;
import net.sticks.ankicraft.managers.PlayerManager;
import net.sticks.ankicraft.packets.OpenFlashcardPacket;
import net.sticks.ankicraft.managers.RewardManager;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.*;

@Mod(AnkiCraft.MODID)
public class AnkiCraft {
    public static final String MODID = "ankicraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AnkiCraft(IEventBus modEventBus, ModContainer modContainer) {
        Statistics.register(modEventBus);
        modEventBus.register(ModEvents.class);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(this::onConfigReload);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        RewardManager.Initialize(event.getServer());
        PlayerManager.Initialize();
        DeckManager.Initialize();
    }

    private void onConfigReload(ModConfigEvent event) {
        if (event.getConfig().getModId().equals("ankicraft")) {
            PlayerManager.ConfigChange();
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        PlayerManager.Tick(event);
    }
}
