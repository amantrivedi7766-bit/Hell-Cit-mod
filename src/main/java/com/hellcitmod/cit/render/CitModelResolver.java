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
import java.util.concurrent.atomic.AtomicLong;

public final class CitModelResolver {
    private static final CitRuleRegistry REGISTRY = new CitRuleRegistry();
    private static final CitMatchCache CACHE = new CitMatchCache();

    private static final AtomicLong LOOKUPS = new AtomicLong();
    private static final AtomicLong CACHE_HITS = new AtomicLong();
    private static final AtomicLong MATCHES = new AtomicLong();

    private CitModelResolver() {
    }

    public static void bootstrap() {
        // no-op: explicit initialization point for future hooks
    }

    public static void reload(List<CitRule> rules) {
        REGISTRY.rebuild(rules, id -> Optional.ofNullable(Registries.ITEM.get(id)));
        CACHE.clear();
        LOOKUPS.set(0L);
        CACHE_HITS.set(0L);
        MATCHES.set(0L);
        HellCitMod.LOGGER.info("Loaded {} CIT rules", REGISTRY.size());
    }

    public static Optional<Identifier> resolve(ItemStack stack) {
        LOOKUPS.incrementAndGet();

        Optional<Identifier> cached = CACHE.get(stack);
        if (cached != null) {
            CACHE_HITS.incrementAndGet();
            return cached;
        }

        for (CitRule rule : REGISTRY.forItem(stack.getItem())) {
            if (!rule.enabled() || !"item".equals(rule.type())) {
                continue;
            }

            boolean ok = true;
            for (var condition : rule.conditions()) {
                if (!condition.test(stack)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                MATCHES.incrementAndGet();
                Optional<Identifier> result = Optional.of(rule.modelId());
                CACHE.put(stack, result);
                return result;
            }
        }

        Optional<Identifier> miss = Optional.empty();
        CACHE.put(stack, miss);
        return miss;
    }

    public static String debugStats() {
        long lookups = LOOKUPS.get();
        long hits = CACHE_HITS.get();
        long matches = MATCHES.get();
        double hitRatio = lookups == 0 ? 0.0 : (hits * 100.0D / lookups);
        return "rules=" + REGISTRY.size() + ", lookups=" + lookups + ", cacheHits=" + hits +
                String.format(", hitRatio=%.2f%%, matches=%d", hitRatio, matches);
    }

    public static void clearCache() {
        CACHE.clear();
    }
}
