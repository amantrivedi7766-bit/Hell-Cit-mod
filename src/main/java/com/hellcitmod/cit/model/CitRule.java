package com.hellcitmod.cit.model;

import com.hellcitmod.cit.condition.CitCondition;
import net.minecraft.util.Identifier;

import java.util.List;

public record CitRule(
        String source,
        List<Identifier> items,
        Identifier modelId,
        String type,
        String hand,
        List<CitCondition> conditions,
        int priority,
        boolean enabled
) {
}
