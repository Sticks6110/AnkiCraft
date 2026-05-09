package net.sticks.ankicraft;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Statistics {
    public static final ResourceLocation QUESTIONS_CORRECT = ResourceLocation.fromNamespaceAndPath(AnkiCraft.MODID, "questions_correct");
    public static final ResourceLocation QUESTIONS_WRONG = ResourceLocation.fromNamespaceAndPath(AnkiCraft.MODID, "questions_wrong");
    public static final ResourceLocation QUESTIONS_ROW_CURRENT = ResourceLocation.fromNamespaceAndPath(AnkiCraft.MODID, "questions_row_current");
    public static final ResourceLocation QUESTIONS_ROW_MAX = ResourceLocation.fromNamespaceAndPath(AnkiCraft.MODID, "questions_row_max");

    public static final DeferredRegister<ResourceLocation> FLASHCARD_STATS = DeferredRegister.create(Registries.CUSTOM_STAT, AnkiCraft.MODID);
    public static final DeferredHolder<ResourceLocation, ResourceLocation> QUESTIONS_CORRECT_STAT = FLASHCARD_STATS.register("questions_correct", () -> QUESTIONS_CORRECT);
    public static final DeferredHolder<ResourceLocation, ResourceLocation> QUESTIONS_WRONG_STAT = FLASHCARD_STATS.register("questions_wrong", () -> QUESTIONS_WRONG);
    public static final DeferredHolder<ResourceLocation, ResourceLocation> QUESTIONS_ROW_CURRENT_STAT = FLASHCARD_STATS.register("questions_row_current", () -> QUESTIONS_ROW_CURRENT);
    public static final DeferredHolder<ResourceLocation, ResourceLocation> QUESTIONS_ROW_MAX_STAT = FLASHCARD_STATS.register("questions_row_max", () -> QUESTIONS_ROW_MAX);

    public static void register(IEventBus eventBus) {
        FLASHCARD_STATS.register(eventBus);
    }
}
