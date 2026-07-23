package dev.hytalemodding;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import dev.hytalemodding.config.MertiesMixologyConfig;

import javax.annotation.Nonnull;

public class MertiesMixologyPlugin extends JavaPlugin {

    private static Config<MertiesMixologyConfig> config = null;

    public MertiesMixologyPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        config = this.withConfig("MertiesMixology_config", MertiesMixologyConfig.CODEC);
    }

    @Override
    protected void setup() {
        config.save();
        // Command and welcome-message event registration removed for now;
        // real /mixology behavior comes with the legacy Java port.
    }

    public static Config<MertiesMixologyConfig> getConfig() {
        return config;
    }
}