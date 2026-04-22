package com.hellcitmod.cit.cache;

import com.hellcitmod.cit.util.ComponentLookup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class CitMatchCache {
    private final Map<Long, Optional<Identifier>> lru = new LinkedHashMap<>(1024, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Optional<Identifier>> eldest) {
            return size() > 1024;
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

    private long key(ItemStack stack) {
        int itemHash = stack.getItem().hashCode();
        int count = stack.getCount();
        int damage = stack.getDamage();
        int cmd = ComponentLookup.customModelData(stack) == null ? -1 : ComponentLookup.customModelData(stack);
        int nameHash = stack.getName().getString().hashCode();
        int ench = stack.hasEnchantments() ? stack.getEnchantments().hashCode() : 0;

        long h = 17L;
        h = h * 31 + itemHash;
        h = h * 31 + count;
        h = h * 31 + damage;
        h = h * 31 + cmd;
        h = h * 31 + nameHash;
        h = h * 31 + ench;
        return h;
    }
}
