package com.hellcitmod.cit.condition;

import com.hellcitmod.cit.util.ComponentLookup;
import net.minecraft.item.ItemStack;

import java.util.List;

public record LoreCondition(String expected, boolean caseSensitive, boolean regex) implements CitCondition {
    @Override
    public boolean test(ItemStack stack) {
        List<String> loreLines = ComponentLookup.loreLines(stack);
        if (loreLines.isEmpty()) {
            return false;
        }

        for (String line : loreLines) {
            if (matches(line)) {
                return true;
            }
        }
        return false;
    }

    private boolean matches(String input) {
        if (regex) {
            return input.matches(expected);
        }
        return caseSensitive ? expected.equals(input) : expected.equalsIgnoreCase(input);
    }
}
