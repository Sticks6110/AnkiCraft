package net.sticks.ankicraft;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue QUESTION_INTERVAL = BUILDER
            .comment("How often in seconds a player gets a question.")
            .defineInRange("questionInterval", 300, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
