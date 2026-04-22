package com.hellcitmod.cit.condition;

import net.minecraft.item.ItemStack;

public record DamagePercentCondition(double minPercent, double maxPercent) implements CitCondition {
    @Override
    public boolean test(ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) {
            return false;
        }
        double percent = (stack.getDamage() * 100.0D) / maxDamage;
        return percent >= minPercent && percent <= maxPercent;
    }
}
