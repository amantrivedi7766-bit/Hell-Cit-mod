package com.hellcitmod.cit.util;

import net.minecraft.item.ItemStack;

import java.lang.reflect.Method;

public final class ComponentLookup {
    private ComponentLookup() {
    }

    public static Integer customModelData(ItemStack stack) {
        try {
            Class<?> dataTypes = Class.forName("net.minecraft.component.DataComponentTypes");
            Object key = dataTypes.getField("CUSTOM_MODEL_DATA").get(null);
            for (Method method : ItemStack.class.getMethods()) {
                if (method.getName().equals("get") && method.getParameterCount() == 1) {
                    Object value = method.invoke(stack, key);
                    if (value == null) {
                        return null;
                    }
                    for (Method getter : value.getClass().getMethods()) {
                        if (getter.getName().equals("value") && getter.getParameterCount() == 0) {
                            Object out = getter.invoke(value);
                            if (out instanceof Integer i) {
                                return i;
                            }
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException ignored) {
            // Runtime-safe fallback for versions with no component API.
        }
        return null;
    }
}
