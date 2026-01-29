package com.mertie.mixology;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * Merties Mixology Plugin
 *
 * @author Mertie
 * @version 1.0.0
 */
public class MixologyPlugin extends JavaPlugin {

    private static MixologyPlugin instance;

    public MixologyPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        getLogger().at(Level.INFO).log("[Mixology] Plugin loaded!");
    }

    public static MixologyPlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("[Mixology] Plugin setup!");

        // Register commands
        registerCommands();
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("[Mixology] Plugin enabled!");
    }

    @Override
    public void shutdown() {
        getLogger().at(Level.INFO).log("[Mixology] Plugin disabled!");
    }

    private void registerCommands() {
        getCommandRegistry().registerCommand(new MixologyCommand());
    }
}
