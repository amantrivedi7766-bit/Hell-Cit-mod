package com.hellcitmod.cit.condition;

import com.hellcitmod.cit.util.ComponentLookup;
import net.minecraft.item.ItemStack;

public record CustomModelDataCondition(int expected) implements CitCondition {
    @Override
    public boolean test(ItemStack stack) {
        Integer data = ComponentLookup.customModelData(stack);
        return data != null && data == expected;
    }
}
