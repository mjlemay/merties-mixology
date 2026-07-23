package dev.hytalemodding;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import dev.hytalemodding.commands.MertiesMixologyCommand;
import dev.hytalemodding.config.MertiesMixologyConfig;
import dev.hytalemodding.events.MertiesMixologyEvent;

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
        this.getCommandRegistry().registerCommand(new MertiesMixologyCommand("MertiesMixology", "An MertiesMixology command"));
        if (getConfig().get().isEnabledWelcomeMessage()) {
            this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, MertiesMixologyEvent::onPlayerReady);
        }
    }

    public static Config<MertiesMixologyConfig> getConfig() {
        return config;
    }
}