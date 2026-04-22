package com.hellcitmod.cit.condition;

import net.minecraft.item.ItemStack;

import java.util.regex.Pattern;

public final class WildcardNameCondition implements CitCondition {
    private final Pattern pattern;

    public WildcardNameCondition(String wildcard, boolean ignoreCase) {
        this.pattern = Pattern.compile(wildcardToRegex(wildcard), ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
    }

    @Override
    public boolean test(ItemStack stack) {
        return pattern.matcher(stack.getName().getString()).matches();
    }

    private String wildcardToRegex(String wildcard) {
        StringBuilder regex = new StringBuilder("^");
        for (char c : wildcard.toCharArray()) {
            switch (c) {
                case '*' -> regex.append(".*");
                case '?' -> regex.append('.');
                case '.', '(', ')', '[', ']', '$', '^', '{', '}', '|', '+', '\\' -> regex.append('\\').append(c);
                default -> regex.append(c);
            }
        }
        regex.append('$');
        return regex.toString();
    }
}
