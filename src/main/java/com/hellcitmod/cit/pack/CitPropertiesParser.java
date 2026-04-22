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

        List<Identifier> items = parseItems(p.getProperty("matchItems", p.getProperty("items", "")));
        if (items.isEmpty()) {
            throw new IOException("No matchItems in " + sourceId);
        }

        Identifier modelId = parseModelId(sourceId, p);
        List<CitCondition> conditions = parseConditions(p);
        int weight = parseInt(p.getProperty("weight", "0"), 0);

        return new CitRule(sourceId.toString(), items, modelId, conditions, weight);
    }

    private List<CitCondition> parseConditions(Properties p) {
        List<CitCondition> out = new ArrayList<>();
        String name = p.getProperty("nbt.display.Name");
        if (name != null && !name.isBlank()) {
            boolean caseSensitive = Boolean.parseBoolean(p.getProperty("matchCase", "false"));
            out.add(new NameCondition(name, caseSensitive));
        }

        String cmd = p.getProperty("nbt.CustomModelData", p.getProperty("CustomModelData"));
        if (cmd != null) {
            out.add(new CustomModelDataCondition(parseInt(cmd, -1)));
        }

        String ench = p.getProperty("enchantment", p.getProperty("enchantments"));
        if (ench != null && ench.contains(":")) {
            int level = parseInt(p.getProperty("enchantmentLevel", "1"), 1);
            out.add(new EnchantCondition(Identifier.of(ench), level));
        }

        if (p.containsKey("stackSize")) {
            int[] range = parseRange(p.getProperty("stackSize"), 1, 64);
            out.add(new StackSizeCondition(range[0], range[1]));
        }

        if (p.containsKey("damage")) {
            int[] range = parseRange(p.getProperty("damage"), 0, Integer.MAX_VALUE);
            out.add(new DamageCondition(range[0], range[1]));
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

    private int parseInt(String raw, int fallback) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }
}
