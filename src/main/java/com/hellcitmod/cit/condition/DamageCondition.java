package com.hellcitmod.cit.condition;

import net.minecraft.item.ItemStack;

public record DamageCondition(int min, int max) implements CitCondition {
    @Override
    public boolean test(ItemStack stack) {
        int d = stack.getDamage();
        return d >= min && d <= max;
    }
}
