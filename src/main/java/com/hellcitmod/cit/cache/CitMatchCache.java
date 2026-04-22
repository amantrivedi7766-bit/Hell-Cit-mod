package com.hellcitmod.cit.cache;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class CitMatchCache {
    private final Map<String, Optional<Identifier>> lru = new LinkedHashMap<>(512, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Optional<Identifier>> eldest) {
            return size() > 512;
        }
    };

    public Optional<Identifier> get(ItemStack stack) {
        return lru.get(key(stack));
    }

    public void put(ItemStack stack, Optional<Identifier> value) {
        lru.put(key(stack), value);
    }

    public void clear() {
        lru.clear();
    }

    private String key(ItemStack stack) {
        return stack.getItem().toString() + "|" + stack.getName().getString() + "|" + stack.getCount() + "|" + stack.getDamage() + "|" + stack.getComponents();
    }
}
