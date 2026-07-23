package com.mertie.mixology;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.Message;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * Simple /mixology command that displays the plugin name
 */
public class MixologyCommand extends AbstractAsyncCommand {

    public MixologyCommand() {
        super("mixology", "Display Mixology plugin information", false);
    }

    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("Merties Mixology Plugin v1.0.0"));
        context.sendMessage(Message.raw("A plugin for creating custom cocktails and mixology stations!"));
        return CompletableFuture.completedFuture(null);
    }
}
