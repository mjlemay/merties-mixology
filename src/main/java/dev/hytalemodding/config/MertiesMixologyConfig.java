package dev.hytalemodding.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class MertiesMixologyConfig {

    public static final BuilderCodec<MertiesMixologyConfig> CODEC = BuilderCodec.builder(MertiesMixologyConfig.class, MertiesMixologyConfig::new)
            .append(
                new KeyedCodec<>("EnableWelcomeMessage", Codec.BOOLEAN),
                (exConfig, aBoolean, extraInfo) -> exConfig.enabledWelcomeMessage = aBoolean,
                (exConfig, extraInfo) -> exConfig.enabledWelcomeMessage
            )
            .add()
            .build();

    private boolean enabledWelcomeMessage;

    private MertiesMixologyConfig() {}

    public boolean isEnabledWelcomeMessage() {
        return enabledWelcomeMessage;
    }
}
