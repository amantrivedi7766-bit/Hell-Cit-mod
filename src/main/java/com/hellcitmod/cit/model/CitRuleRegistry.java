package com.hellcitmod.cit.model;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.*;

public final class CitRuleRegistry {
    private final Map<Item, List<CitRule>> byItem = new HashMap<>();

    public void rebuild(List<CitRule> rules, java.util.function.Function<Identifier, Optional<Item>> itemResolver) {
        byItem.clear();
        for (CitRule rule : rules) {
            for (Identifier id : rule.items()) {
                itemResolver.apply(id).ifPresent(item -> byItem.computeIfAbsent(item, ignored -> new ArrayList<>()).add(rule));
            }
        }

        for (List<CitRule> list : byItem.values()) {
            list.sort(Comparator.comparingInt(CitRule::priority).reversed());
        }
    }

    public List<CitRule> forItem(Item item) {
        return byItem.getOrDefault(item, List.of());
    }

    public int size() {
        return byItem.values().stream().mapToInt(List::size).sum();
    }
}
