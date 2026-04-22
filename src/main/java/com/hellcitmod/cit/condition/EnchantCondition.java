package com.hellcitmod.cit.condition;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;

public record EnchantCondition(Identifier enchantmentId, int minLevel) implements CitCondition {
    @Override
    public boolean test(ItemStack stack) {
        ItemEnchantmentsComponent ench = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (RegistryEntry<Enchantment> entry : ench.getEnchantments()) {
            Identifier id = entry.getKey().map(k -> k.getValue()).orElse(null);
            if (enchantmentId.equals(id) && ench.getLevel(entry) >= minLevel) {
                return true;
            }
        }
        return false;
    }
}
