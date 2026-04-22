package com.hellcitmod.cit.pack;

import com.hellcitmod.cit.HellCitMod;
import com.hellcitmod.cit.condition.*;
import com.hellcitmod.cit.model.CitRule;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class CitPropertiesParser {
    public CitRule parse(Identifier sourceId, Reader reader) throws IOException {
        Properties p = new Properties();
        p.load(reader);

        boolean enabled = !"false".equalsIgnoreCase(p.getProperty("enabled", "true"));
        if (!enabled) {
            throw new IOException("Rule disabled in " + sourceId);
        }

        String type = p.getProperty("type", "item");
        if (!type.equals("item")) {
            throw new IOException("Unsupported CIT type " + type + " in " + sourceId);
        }

        List<Identifier> items = parseItems(p.getProperty("matchItems", p.getProperty("items", "")));
        if (items.isEmpty()) {
            throw new IOException("No matchItems in " + sourceId);
        }

        Identifier modelId = parseModelId(sourceId, p);
        List<CitCondition> conditions = parseConditions(p);
        int weight = parseInt(p.getProperty("weight", p.getProperty("priority", "0")), 0);
        String hand = p.getProperty("hand", "any");

        return new CitRule(sourceId.toString(), items, modelId, type, hand, conditions, weight, enabled);
    }

    private List<CitCondition> parseConditions(Properties p) {
        List<CitCondition> out = new ArrayList<>();

        String name = p.getProperty("nbt.display.Name", p.getProperty("name"));
        if (name != null && !name.isBlank()) {
            boolean caseSensitive = Boolean.parseBoolean(p.getProperty("matchCase", "false"));
            boolean regex = Boolean.parseBoolean(p.getProperty("nameRegex", "false"));
            out.add(new NameCondition(name, caseSensitive, regex));
        }

        String lore = p.getProperty("nbt.display.Lore", p.getProperty("lore"));
        if (lore != null && !lore.isBlank()) {
            boolean caseSensitive = Boolean.parseBoolean(p.getProperty("loreCaseSensitive", "false"));
            boolean regex = Boolean.parseBoolean(p.getProperty("loreRegex", "false"));
            out.add(new LoreCondition(lore, caseSensitive, regex));
        }

        String cmd = p.getProperty("nbt.CustomModelData", p.getProperty("CustomModelData"));
        if (cmd != null) {
            out.add(new CustomModelDataCondition(parseInt(cmd, -1)));
        }

        String enchants = p.getProperty("enchantments", p.getProperty("enchantment"));
        if (enchants != null) {
            int level = parseInt(p.getProperty("enchantmentLevel", "1"), 1);
            for (String token : enchants.split(",")) {
                String trimmed = token.trim();
                if (!trimmed.isEmpty() && trimmed.contains(":")) {
                    out.add(new EnchantCondition(Identifier.of(trimmed), level));
                }
            }
        }

        if (p.containsKey("stackSize")) {
            int[] range = parseRange(p.getProperty("stackSize"), 1, 64);
            out.add(new StackSizeCondition(range[0], range[1]));
        }

        if (p.containsKey("damage")) {
            int[] range = parseRange(p.getProperty("damage"), 0, Integer.MAX_VALUE);
            out.add(new DamageCondition(range[0], range[1]));
        }

        if (p.containsKey("damagePercent")) {
            double[] range = parseRangeDouble(p.getProperty("damagePercent"), 0.0, 100.0);
            out.add(new DamagePercentCondition(range[0], range[1]));
        }

        for (String key : p.stringPropertyNames()) {
            if (key.startsWith("nbt.") && !key.equals("nbt.display.Name") && !key.equals("nbt.display.Lore") && !key.equals("nbt.CustomModelData")) {
                String nbtPath = key.substring("nbt.".length());
                String value = p.getProperty(key);
                boolean regex = Boolean.parseBoolean(p.getProperty(key + ".regex", "false"));
                out.add(new NbtPathCondition(nbtPath, value, regex));
            }
        }
        return out;
    }

    private List<Identifier> parseItems(String value) {
        List<Identifier> out = new ArrayList<>();
        for (String token : value.split("\\s+")) {
            if (token.isBlank()) continue;
            try {
                out.add(token.contains(":") ? Identifier.of(token) : Identifier.of("minecraft", token));
            } catch (Exception e) {
                HellCitMod.LOGGER.warn("Bad item id {}", token);
            }
        }
        return out;
    }

    private Identifier parseModelId(Identifier sourceId, Properties p) throws IOException {
        String model = p.getProperty("model");
        String texture = p.getProperty("texture");
        if (model != null && !model.isBlank()) {
            return normalize(sourceId, model);
        }
        if (texture != null && !texture.isBlank()) {
            return normalize(sourceId, texture);
        }
        throw new IOException("Missing model/texture key in " + sourceId);
    }

    private Identifier normalize(Identifier sourceId, String path) {
        String clean = path.replace(".png", "").replace(".json", "");
        if (clean.contains(":")) {
            return Identifier.of(clean);
        }
        String namespace = sourceId.getNamespace();
        String sourcePath = sourceId.getPath();
        int idx = sourcePath.lastIndexOf('/');
        String folder = idx < 0 ? "cit" : sourcePath.substring(0, idx);
        return Identifier.of(namespace, folder + "/" + clean);
    }

    private int[] parseRange(String value, int defaultMin, int defaultMax) {
        if (value.contains("-")) {
            String[] split = value.split("-", 2);
            return new int[]{parseInt(split[0], defaultMin), parseInt(split[1], defaultMax)};
        }
        int fixed = parseInt(value, defaultMin);
        return new int[]{fixed, fixed};
    }

    private double[] parseRangeDouble(String value, double defaultMin, double defaultMax) {
        if (value.contains("-")) {
            String[] split = value.split("-", 2);
            return new double[]{parseDouble(split[0], defaultMin), parseDouble(split[1], defaultMax)};
        }
        double fixed = parseDouble(value, defaultMin);
        return new double[]{fixed, fixed};
    }

    private int parseInt(String raw, int fallback) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private double parseDouble(String raw, double fallback) {
        try {
            return Double.parseDouble(raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }
}
