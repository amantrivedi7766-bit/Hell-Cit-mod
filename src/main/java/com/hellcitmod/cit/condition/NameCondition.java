package com.hellcitmod.cit.condition;

import net.minecraft.item.ItemStack;

public record NameCondition(String expected, boolean caseSensitive) implements CitCondition {
    @Override
    public boolean test(ItemStack stack) {
        String name = stack.getName().getString();
        return caseSensitive ? expected.equals(name) : expected.equalsIgnoreCase(name);
    }
}
