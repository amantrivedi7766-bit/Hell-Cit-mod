package com.hellcitmod.cit.condition;

import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface CitCondition {
    boolean test(ItemStack stack);
}
