package com.hellcitmod.cit.command;

import com.hellcitmod.cit.render.CitModelResolver;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class CitCommands {
    private CitCommands() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("cit")
                        .then(ClientCommandManager.literal("reload")
                                .executes(ctx -> {
                                    MinecraftClient client = MinecraftClient.getInstance();
                                    client.reloadResources();
                                    ctx.getSource().sendFeedback(Text.literal("[CIT] Resource reload triggered"));
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("debug")
                                .executes(ctx -> {
                                    MinecraftClient client = MinecraftClient.getInstance();
                                    if (client.player == null) {
                                        return 0;
                                    }
                                    var held = client.player.getMainHandStack();
                                    var result = CitModelResolver.resolve(held).map(Object::toString).orElse("<default>");
                                    ctx.getSource().sendFeedback(Text.literal("[CIT] Match: " + result));
                                    return 1;
                                }))
        ));
    }
}
