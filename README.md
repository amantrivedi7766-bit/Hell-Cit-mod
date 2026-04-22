# Hell-Cit-mod

Fabric based CIT (Custom Item Texture) mod scaffold for Minecraft **1.21.1 → 1.21.6**.

## Features implemented
- Resource-pack CIT `.properties` parsing from `assets/<namespace>/cit/**/*.properties`
- Rule matching by:
  - `matchItems`
  - `nbt.display.Name` (+ `matchCase=true|false`)
  - `nbt.CustomModelData` / `CustomModelData`
  - `enchantment`, `enchantmentLevel`
  - `stackSize`
  - `damage`
- Runtime model override via `ItemRenderer` mixin
- Fast LRU cache for repeated stack lookups
- Graceful fallback (invalid files are skipped, default model remains)
- Commands:
  - `/cit reload`
  - `/cit debug`

## Resource-pack layout
Place properties, model JSON, and texture in one logical CIT folder and connect through the `.properties` file:

```text
assets/<namespace>/
  cit/
    swords/
      fire_blade.properties
      fire_blade.json
      fire_blade.png
```

Example `fire_blade.properties`:

```properties
matchItems=minecraft:diamond_sword
nbt.display.Name=Fire Blade
matchCase=false
enchantment=minecraft:fire_aspect
enchantmentLevel=1
model=fire_blade
```

> `model=fire_blade` resolves relative to the properties folder (`cit/swords/fire_blade`).

## Dev notes
- Loader: Fabric
- Java: 21
- Designed with modular parser/registry/resolver/cache so minor version migration stays manageable.
