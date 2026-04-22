package com.hellcitmod.cit.render;

import com.hellcitmod.cit.HellCitMod;
import com.hellcitmod.cit.cache.CitMatchCache;
import com.hellcitmod.cit.model.CitRule;
import com.hellcitmod.cit.model.CitRuleRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public final class CitModelResolver {
    private static final CitRuleRegistry REGISTRY = new CitRuleRegistry();
    private static final CitMatchCache CACHE = new CitMatchCache();

    private CitModelResolver() {
    }

    public static void bootstrap() {
        // no-op: explicit initialization point for future hooks
    }

    public static void reload(List<CitRule> rules) {
        REGISTRY.rebuild(rules, id -> Optional.ofNullable(Registries.ITEM.get(id)));
        CACHE.clear();
        HellCitMod.LOGGER.info("Loaded {} CIT rules", REGISTRY.size());
    }

    public static Optional<Identifier> resolve(ItemStack stack) {
        Optional<Identifier> cached = CACHE.get(stack);
        if (cached != null) {
            return cached;
        }

        for (CitRule rule : REGISTRY.forItem(stack.getItem())) {
            boolean ok = true;
            for (var condition : rule.conditions()) {
                if (!condition.test(stack)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                Optional<Identifier> result = Optional.of(rule.modelId());
                CACHE.put(stack, result);
                return result;
            }
        }

        Optional<Identifier> miss = Optional.empty();
        CACHE.put(stack, miss);
        return miss;
    }

    public static void clearCache() {
        CACHE.clear();
    }
}
