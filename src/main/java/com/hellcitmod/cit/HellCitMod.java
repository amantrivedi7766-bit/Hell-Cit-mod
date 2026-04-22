package com.hellcitmod.cit;

import com.hellcitmod.cit.command.CitCommands;
import com.hellcitmod.cit.pack.CitResourceReloader;
import com.hellcitmod.cit.render.CitModelResolver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HellCitMod implements ClientModInitializer {
    public static final String MOD_ID = "hellcit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new CitResourceReloader());
        CitCommands.register();
        CitModelResolver.bootstrap();
        LOGGER.info("Hell CIT initialized");
    }
}
