package com.hellcitmod.cit.util;

import net.minecraft.item.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class ComponentLookup {
    private ComponentLookup() {
    }

    public static Integer customModelData(ItemStack stack) {
        try {
            Class<?> dataTypes = Class.forName("net.minecraft.component.DataComponentTypes");
            Object key = dataTypes.getField("CUSTOM_MODEL_DATA").get(null);
            Object value = invokeStackGet(stack, key);
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
        } catch (ReflectiveOperationException ignored) {
            // Runtime-safe fallback for versions with no component API.
        }
        return null;
    }

    public static List<String> loreLines(ItemStack stack) {
        List<String> out = new ArrayList<>();
        try {
            Class<?> dataTypes = Class.forName("net.minecraft.component.DataComponentTypes");
            Object key = dataTypes.getField("LORE").get(null);
            Object lore = invokeStackGet(stack, key);
            if (lore == null) {
                return out;
            }

            for (Method m : lore.getClass().getMethods()) {
                if ((m.getName().equals("lines") || m.getName().equals("styledLines")) && m.getParameterCount() == 0) {
                    Object lines = m.invoke(lore);
                    if (lines instanceof Iterable<?> iterable) {
                        for (Object line : iterable) {
                            out.add(String.valueOf(line));
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException ignored) {
            // optional in some versions
        }
        return out;
    }

    public static String nbtPath(ItemStack stack, String path) {
        try {
            Method nbtMethod = ItemStack.class.getMethod("getNbt");
            Object nbt = nbtMethod.invoke(stack);
            if (nbt == null) {
                return null;
            }

            String[] parts = path.split("\\.");
            Object current = nbt;
            for (String part : parts) {
                Method contains = current.getClass().getMethod("contains", String.class);
                boolean present = (boolean) contains.invoke(current, part);
                if (!present) {
                    return null;
                }
                Method get = current.getClass().getMethod("get", String.class);
                current = get.invoke(current, part);
                if (current == null) {
                    return null;
                }
            }
            return current.toString();
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static Object invokeStackGet(ItemStack stack, Object key) throws ReflectiveOperationException {
        for (Method method : ItemStack.class.getMethods()) {
            if (method.getName().equals("get") && method.getParameterCount() == 1) {
                try {
                    return method.invoke(stack, key);
                } catch (IllegalArgumentException ignored) {
                    // try next overload if arg type does not match
                }
            }
        }
        return null;
    }
}
