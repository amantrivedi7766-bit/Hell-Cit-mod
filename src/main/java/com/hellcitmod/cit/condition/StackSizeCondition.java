package com.hellcitmod.cit.condition;

import net.minecraft.item.ItemStack;

public record StackSizeCondition(int min, int max) implements CitCondition {
    @Override
    public boolean test(ItemStack stack) {
        int c = stack.getCount();
        return c >= min && c <= max;
    }
}
