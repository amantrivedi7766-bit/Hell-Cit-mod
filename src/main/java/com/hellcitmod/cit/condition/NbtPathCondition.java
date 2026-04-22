package com.hellcitmod.cit.condition;

import com.hellcitmod.cit.util.ComponentLookup;
import net.minecraft.item.ItemStack;

public record NbtPathCondition(String path, String expectedValue, boolean regex) implements CitCondition {
    @Override
    public boolean test(ItemStack stack) {
        String value = ComponentLookup.nbtPath(stack, path);
        if (value == null) {
            return false;
        }
        return regex ? value.matches(expectedValue) : expectedValue.equals(value);
    }
}
