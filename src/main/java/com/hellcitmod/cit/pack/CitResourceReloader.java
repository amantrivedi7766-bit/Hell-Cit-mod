package com.hellcitmod.cit.pack;

import com.hellcitmod.cit.HellCitMod;
import com.hellcitmod.cit.model.CitRule;
import com.hellcitmod.cit.render.CitModelResolver;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class CitResourceReloader implements IdentifiableResourceReloadListener {
    private static final Identifier ID = Identifier.of(HellCitMod.MOD_ID, "cit_rules");
    private final CitPropertiesParser parser = new CitPropertiesParser();

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> prepare(manager), prepareExecutor)
                .thenCompose(synchronizer::whenPrepared)
                .thenAcceptAsync(CitModelResolver::reload, applyExecutor);
    }

    private List<CitRule> prepare(ResourceManager manager) {
        List<CitRule> rules = new ArrayList<>();
        Map<Identifier, List<Resource>> resources = manager.findAllResources("cit", id -> id.getPath().endsWith(".properties"));

        for (var entry : resources.entrySet()) {
            for (Resource resource : entry.getValue()) {
                try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                    rules.add(parser.parse(entry.getKey(), reader));
                } catch (IOException e) {
                    HellCitMod.LOGGER.warn("Skipping invalid CIT properties {} ({})", entry.getKey(), e.getMessage());
                }
            }
        }
        return rules;
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of();
    }
}
