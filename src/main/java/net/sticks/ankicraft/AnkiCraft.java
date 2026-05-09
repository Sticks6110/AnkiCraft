package net.sticks.ankicraft;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.sticks.ankicraft.managers.DeckManager;
import net.sticks.ankicraft.managers.PlayerManager;
import net.sticks.ankicraft.managers.RewardManager;
import org.slf4j.Logger;

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

        DeckManager.initialize();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        RewardManager.initialize(event.getServer());
        PlayerManager.initialize();
    }

    private void onConfigReload(ModConfigEvent event) {
        if (event.getConfig().getModId().equals(MODID)) {
            PlayerManager.onConfigChange();
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        PlayerManager.tick(event);
    }
}
